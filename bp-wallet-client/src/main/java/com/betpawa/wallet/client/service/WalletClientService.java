package com.betpawa.wallet.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.betpawa.wallet.client.dto.WalletClientRequest;
import com.betpawa.wallet.client.dto.WalletClientResponse;
import com.betpawa.wallet.client.enums.STATUS;
import com.betpawa.wallet.client.runner.RoundRunner;

@Service
public class WalletClientService {
    private static final Logger logger = LoggerFactory.getLogger(WalletClientService.class);

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationContext applicationContext;

    public WalletClientResponse execute(final WalletClientRequest walletClientRequest) {
        RoundRunner myThread = applicationContext.getBean(RoundRunner.class, walletClientRequest.getNumberOfRounds(),
                "", Long.valueOf(99));
        myThread.setNumberOfrounds(walletClientRequest.getNumberOfRounds());
        myThread.setStats("");
        myThread.setUserID(Long.valueOf(1));
        taskExecutor.execute(myThread);
        return new WalletClientResponse.Builder().status(STATUS.SUCCESS).build();
    }
}
