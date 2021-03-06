package io.celsogra.finance.controller;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.celsogra.finance.base.CoinBase;
import io.celsogra.finance.dto.TransactionDTO;
import io.celsogra.finance.dto.WalletDTO;
import io.celsogra.finance.service.TransactionService;
import io.celsogra.finance.service.WalletService;
import io.celsogra.finance.util.CryptUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class WalletController {

    @Autowired
    private CoinBase coinBase;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    public static final String MESSAGE_KEY = "msg";
    public static final String ID_KEY = "id";
    public static final String BALANCE_KEY = "balance";
    public static final String YOU_GOT_KEY = "got";

    @GetMapping(value = "/coinbase", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> coinbase() throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("WalletController.coinbase()");
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(ID_KEY, coinBase.getPublicKeyAsString()));
    }

    @PostMapping(value = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> balance(@RequestBody WalletDTO wallet)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("WalletController.balance()");
        return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap(BALANCE_KEY,
                walletService.balance(CryptUtil.getKeyFromString(wallet.getId()))));
    }

    @PostMapping(value = "/faucet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> faucet(@RequestBody WalletDTO wallet)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("WalletController.getCoinbase()");
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(YOU_GOT_KEY, walletService.faucet(wallet.getId())));
    }

    @PostMapping(value = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createTransaction(@Valid @RequestBody TransactionDTO dto)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        log.info("TransactionController.createTransaction(TransactionDTO) - Params: {}", dto);
        transactionService.addToBlock(dto);
        return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap(MESSAGE_KEY, HttpStatus.OK.name()));
    }

}
