package com.betpawa.wallet.client.integration;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import com.betpawa.wallet.client.enums.AMOUNT;
import com.betpawa.wallet.client.enums.TRANSACTION;
import com.bp.wallet.proto.BaseRequest;
import com.bp.wallet.proto.BaseResponse;
import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.proto.WalletServiceGrpc.WalletServiceFutureStub;
import com.google.common.util.concurrent.ListenableFuture;

public class IntegrationTest {

    @Autowired
    private WalletServiceFutureStub futureStub;

    @Autowired
    private TaskExecutor taskExecuter;

    public void tests() throws InterruptedException, ExecutionException {
        ListenableFuture<BaseResponse> futureResponse = null;
        BaseResponse response = null;
        Long userID = 1L;
        futureResponse = TRANSACTION.WITHDRAW.doTransact(
                futureStub, BaseRequest.newBuilder().setUserID(userID)
                        .setAmount(AMOUNT.TWOHUNDERED.getAmount().toPlainString()).setCurrency(CURRENCY.USD).build(),
                taskExecuter);
        response = futureResponse.get();
        response.getStatus();
        futureResponse = TRANSACTION.DEPOSIT.doTransact(
                futureStub, BaseRequest.newBuilder().setUserID(userID)
                        .setAmount(AMOUNT.HUNDERED.getAmount().toPlainString()).setCurrency(CURRENCY.USD).build(),
                taskExecuter);
        response = futureResponse.get();

        futureResponse = TRANSACTION.BALANCE.doTransact(futureStub, BaseRequest.newBuilder().setUserID(userID).build(),
                taskExecuter);
        response = futureResponse.get();

        futureResponse = TRANSACTION.WITHDRAW.doTransact(
                futureStub, BaseRequest.newBuilder().setUserID(userID)
                        .setAmount(AMOUNT.TWOHUNDERED.getAmount().toPlainString()).setCurrency(CURRENCY.USD).build(),
                taskExecuter);
        response = futureResponse.get();
        futureResponse = TRANSACTION.DEPOSIT.doTransact(
                futureStub, BaseRequest.newBuilder().setUserID(userID)
                        .setAmount(AMOUNT.HUNDERED.getAmount().toPlainString()).setCurrency(CURRENCY.EUR).build(),
                taskExecuter);
        response = futureResponse.get();
        futureResponse = TRANSACTION.BALANCE.doTransact(futureStub, BaseRequest.newBuilder().setUserID(userID).build(),
                taskExecuter);
        response = futureResponse.get();
        futureResponse = TRANSACTION.WITHDRAW.doTransact(
                futureStub, BaseRequest.newBuilder().setUserID(userID)
                        .setAmount(AMOUNT.TWOHUNDERED.getAmount().toPlainString()).setCurrency(CURRENCY.USD).build(),
                taskExecuter);
        response = futureResponse.get();
        futureResponse = TRANSACTION.DEPOSIT.doTransact(
                futureStub, BaseRequest.newBuilder().setUserID(userID)
                        .setAmount(AMOUNT.HUNDERED.getAmount().toPlainString()).setCurrency(CURRENCY.USD).build(),
                taskExecuter);
        response = futureResponse.get();
        futureResponse = TRANSACTION.BALANCE.doTransact(futureStub, BaseRequest.newBuilder().setUserID(userID).build(),
                taskExecuter);
        response = futureResponse.get();
        futureResponse = TRANSACTION.WITHDRAW.doTransact(
                futureStub, BaseRequest.newBuilder().setUserID(userID)
                        .setAmount(AMOUNT.TWOHUNDERED.getAmount().toPlainString()).setCurrency(CURRENCY.USD).build(),
                taskExecuter);
        response = futureResponse.get();
        futureResponse = TRANSACTION.BALANCE.doTransact(futureStub, BaseRequest.newBuilder().setUserID(userID).build(),
                taskExecuter);
        response = futureResponse.get();
        futureResponse = TRANSACTION.WITHDRAW.doTransact(
                futureStub, BaseRequest.newBuilder().setUserID(userID)
                        .setAmount(AMOUNT.TWOHUNDERED.getAmount().toPlainString()).setCurrency(CURRENCY.USD).build(),
                taskExecuter);
        response = futureResponse.get();

    }

}
