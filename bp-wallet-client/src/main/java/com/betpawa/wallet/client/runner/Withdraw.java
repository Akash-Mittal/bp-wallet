package com.betpawa.wallet.client.runner;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.proto.WalletServiceGrpc.WalletServiceFutureStub;
import com.bp.wallet.proto.WithdrawRequest;
import com.bp.wallet.proto.WithdrawResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import io.grpc.Status;

@Component
@Scope("prototype")
public class Withdraw implements Supplier<WithdrawResponse> {
    private static final Logger logger = LoggerFactory.getLogger(Withdraw.class);

    @Autowired
    private WalletServiceFutureStub futureStub;
    @Autowired
    private ExecutorService executorService;

    private Long userID;
    private BigDecimal amount;
    private CURRENCY currency;

    @Override
    public WithdrawResponse get() {
        try {

            ListenableFuture<WithdrawResponse> withdrawResponseLF = futureStub.withdraw(WithdrawRequest.newBuilder()
                    .setUserID(userID).setAmount(amount.toPlainString()).setCurrency(currency).build());
            Futures.addCallback(withdrawResponseLF, new FutureCallback<WithdrawResponse>() {
                @Override
                public void onSuccess(WithdrawResponse result) {
                    logger.info("Withdrawn Succesfully");
                    // latch.countDown();

                }

                @Override
                public void onFailure(Throwable t) {
                    logger.warn(Status.fromThrowable(t).getDescription());
                    // latch.countDown();
                }
            }, executorService);

            return withdrawResponseLF.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("--->", e);
        }

        return WithdrawResponse.newBuilder().build();
    }

    public Long getUserID()

    {

        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public CURRENCY getCurrency() {
        return currency;
    }

    public void setCurrency(CURRENCY currency) {
        this.currency = currency;
    }

    public Withdraw(Long userID, BigDecimal amount, CURRENCY currency) {
        super();
        this.userID = userID;
        this.amount = amount;
        this.currency = currency;
    }

}
