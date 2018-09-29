package com.betpawa.wallet.client.enums;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betpawa.wallet.client.dto.ClientResponse;
import com.bp.wallet.proto.BalanceRequest;
import com.bp.wallet.proto.BalanceResponse;
import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.proto.DepositRequest;
import com.bp.wallet.proto.DepositResponse;
import com.bp.wallet.proto.WalletServiceGrpc.WalletServiceFutureStub;
import com.bp.wallet.proto.WithdrawRequest;
import com.bp.wallet.proto.WithdrawResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import io.grpc.Status;

public enum TRANSACTION {

    DEPOSIT {
        @Override
        public ClientResponse doTransact(final WalletServiceFutureStub futureStub, final Long userID,
                final BigDecimal amount, final CURRENCY currency, final String stats) {
            final ClientResponse clientResponse = new ClientResponse.Builder().execution_STATUS(STATUS.FAIL).build();
            logger.info(stats + DEPOSIT.name());

            ListenableFuture<DepositResponse> response = futureStub.deposit(DepositRequest.newBuilder()
                    .setAmount(amount.toPlainString()).setUserID(userID).setCurrency(currency).build());
            // final CountDownLatch latch = new CountDownLatch(1);

            Futures.addCallback(response, new FutureCallback<DepositResponse>() {
                @Override
                public void onSuccess(DepositResponse result) {

                    logger.info("Deposited Succesfully");
                    clientResponse.setExecutionStatus(STATUS.SUCCESS);
                    clientResponse.setDepositResponse(result);
                    // latch.countDown();

                }

                @Override
                public void onFailure(Throwable t) {
                    logger.warn(Status.fromThrowable(t).getDescription());
                    // latch.countDown();
                }
            });
            return clientResponse;
        }
    },
    WITHDRAW {
        @Override
        public ClientResponse doTransact(final WalletServiceFutureStub futureStub, final Long userID,
                final BigDecimal amount, final CURRENCY currency, final String stats) {
            logger.info(stats + WITHDRAW.name());
            final ClientResponse clientResponse = new ClientResponse.Builder().execution_STATUS(STATUS.FAIL).build();
            ListenableFuture<WithdrawResponse> response = null;
            response = futureStub.withdraw(WithdrawRequest.newBuilder().setUserID(userID)
                    .setAmount(amount.toPlainString()).setCurrency(currency).build());
            // final CountDownLatch latch = new CountDownLatch(1);

            Futures.addCallback(response, new FutureCallback<WithdrawResponse>() {
                @Override
                public void onSuccess(WithdrawResponse result) {
                    logger.info("Withdrawn Succesfully");
                    clientResponse.setExecutionStatus(STATUS.SUCCESS);
                    clientResponse.setWithdrawResponse(result);
                    // latch.countDown();
                }

                @Override
                public void onFailure(Throwable t) {
                    logger.warn(Status.fromThrowable(t).getDescription());
                    // latch.countDown();

                }
            });
            return clientResponse;

        }
    },
    BALANCE {
        @Override
        public ClientResponse doTransact(final WalletServiceFutureStub futureStub, final Long userID,
                final BigDecimal amount, final CURRENCY currency, final String stats) {
            final ClientResponse clientResponse = new ClientResponse.Builder().execution_STATUS(STATUS.FAIL).build();
            logger.info(stats + BALANCE.name());
            ListenableFuture<BalanceResponse> response = futureStub
                    .balance(BalanceRequest.newBuilder().setUserID(userID).build());
            final CountDownLatch latch = new CountDownLatch(1);

            Futures.addCallback(response, new FutureCallback<BalanceResponse>() {
                @Override
                public void onSuccess(BalanceResponse result) {
                    logger.info("Balance Checked for user:{} Amount:{}", userID, Client.buildGetBalanceLogLine(result));
                    clientResponse.setExecutionStatus(STATUS.SUCCESS);
                    clientResponse.setBalanceResponse(result);
                    latch.countDown();

                }

                @Override
                public void onFailure(Throwable t) {
                    logger.warn(Status.fromThrowable(t).getDescription());
                    latch.countDown();

                }
            });
            return clientResponse;

        }
    };

    public abstract ClientResponse doTransact(final WalletServiceFutureStub futureStub, final Long userID,
            final BigDecimal amount, final CURRENCY currency, final String stats);

    private static final Logger logger = LoggerFactory.getLogger(TRANSACTION.class);

}