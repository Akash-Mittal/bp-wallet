package com.betpawa.wallet.client.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.betpawa.wallet.client.dto.WalletClientRequest;
import com.betpawa.wallet.client.enums.TRANSACTION;
import com.betpawa.wallet.client.runner.UserSupplier;
import com.bp.wallet.proto.BaseRequest;
import com.bp.wallet.proto.BaseResponse;
import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.proto.STATUS;
import com.bp.wallet.proto.WalletServiceGrpc.WalletServiceFutureStub;
import com.google.common.util.concurrent.ListenableFuture;

@Service
public class WalletClientService {
    private static final Logger logger = LoggerFactory.getLogger(WalletClientService.class);

    private static final long DURATION_SECONDS = 150;

    @Autowired
    private WalletServiceFutureStub futureStub;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private UserSupplier userSupplier;

    public Map<STATUS, AtomicLong> runRounds(final WalletClientRequest walletClientRequest) {

        final Map<STATUS, AtomicLong> statusMap = new EnumMap<STATUS, AtomicLong>(STATUS.class);
        statusMap.put(STATUS.TRANSACTION_SUCCESS, new AtomicLong(0));
        statusMap.put(STATUS.TRANSACTION_FAILED, new AtomicLong(0));

        final List<ListenableFuture<BaseResponse>> roundsLFResponse = new ArrayList<>();
        userSupplier.setWalletClientRequest(walletClientRequest);
        roundsLFResponse.addAll(userSupplier.get());
        roundsLFResponse.forEach(listenableFuture -> {
            try {
                logger.info(roundsLFResponse.size() + ":  " + listenableFuture.get().getStatus().name(),
                        listenableFuture.get().getStatusMessage());
                statusMap.get(STATUS.TRANSACTION_SUCCESS).incrementAndGet();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                statusMap.get(STATUS.TRANSACTION_FAILED).incrementAndGet();
            }
        });
        return statusMap;
    }

    public void execute(final WalletClientRequest walletClientRequest) throws InterruptedException {
        long time = System.currentTimeMillis();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        AtomicBoolean done = new AtomicBoolean();
        scheduler.schedule(() -> done.set(true), DURATION_SECONDS, TimeUnit.MILLISECONDS);
        Long rpcCount = Long.valueOf(0);
        List<ListenableFuture<BaseResponse>> roundsLFResponse = new ArrayList<>();
        try {
            while (!done.get()) {
                roundsLFResponse
                        .add(TRANSACTION.DEPOSIT.doTransact(
                                futureStub, BaseRequest.newBuilder().setUserID(123L)
                                        .setAmount(BigDecimal.TEN.toPlainString()).setCurrency(CURRENCY.EUR).build(),
                                taskExecutor));
                rpcCount++;
            }
            roundsLFResponse.forEach(lf -> {
                try {
                    logger.info(roundsLFResponse.size() + ":  " + lf.get().getStatus().name(),
                            lf.get().getStatusMessage());
                } catch (Exception e) {
                    logger.error("--------->" + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            logger.info("Excecution done time taken: {} {}", (System.currentTimeMillis() - time) / 1000, " Seconds.");
            logger.info("Did {} RPCs/s", roundsLFResponse.size());

        }
    }
}
