package com.betpawa.wallet.client.service;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.betpawa.wallet.client.dto.WalletClientRequest;
import com.betpawa.wallet.client.dto.WalletClientResponse;
import com.betpawa.wallet.client.enums.STATUS;
import com.betpawa.wallet.client.runner.Deposit;
import com.bp.wallet.proto.CURRENCY;

@Service
public class WalletClientService {
    private static final Logger logger = LoggerFactory.getLogger(WalletClientService.class);

    private static final long DURATION_SECONDS = 60;

    @Autowired
    private ApplicationContext applicationContext;

    public WalletClientResponse execute(final WalletClientRequest walletClientRequest) throws InterruptedException {
        long time = System.currentTimeMillis();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        AtomicBoolean done = new AtomicBoolean();
        scheduler.schedule(() -> done.set(true), DURATION_SECONDS, TimeUnit.SECONDS);
        Long rpcCount = Long.valueOf(0);
        try {
            while (!done.get()) {
                Deposit myThread = applicationContext.getBean(Deposit.class, Long.valueOf(1234L), BigDecimal.TEN,
                        CURRENCY.EUR);
                logger.info("---->" + myThread + "---->" + myThread.get());
                rpcCount++;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WalletClientResponse.Builder().status(STATUS.FAIL).build();
        } finally {
            logger.info("Excecution done time taken: {} {}", (System.currentTimeMillis() - time) / 1000, " Seconds.");
            logger.info("Did {} RPCs/s", rpcCount / DURATION_SECONDS);

        }
        return new WalletClientResponse.Builder().status(STATUS.SUCCESS).build();
    }
}
