package com.betpawa.wallet.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.betpawa.wallet.client.dto.WalletClientRequest;
import com.betpawa.wallet.client.service.WalletClientService;

@RestController
@RequestMapping("/")
public class WalletClientController {

    @Autowired
    private WalletClientService walletClientService;

    @PostMapping
    public void execute(@RequestBody WalletClientRequest walletClientRequest) throws InterruptedException {
        // walletClientService.execute(walletClientRequest);
        walletClientService.runRounds(walletClientRequest);
    }
}
