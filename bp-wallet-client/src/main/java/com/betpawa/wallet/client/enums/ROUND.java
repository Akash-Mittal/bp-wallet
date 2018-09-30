package com.betpawa.wallet.client.enums;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.task.TaskExecutor;

import com.bp.wallet.proto.BaseRequest;
import com.bp.wallet.proto.BaseResponse;
import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.proto.WalletServiceGrpc.WalletServiceFutureStub;
import com.google.common.util.concurrent.ListenableFuture;

public enum ROUND {
    A {

        @Override
        public List<ListenableFuture<BaseResponse>> goExecute(final WalletServiceFutureStub futureStub,
                final Long userID, final TaskExecutor taskExecuter) {
            List<ListenableFuture<BaseResponse>> list = new ArrayList<>();
            list.add(TRANSACTION.DEPOSIT.doTransact(
                    futureStub, BaseRequest.newBuilder().setUserID(userID)
                            .setAmount(AMOUNT.HUNDERED.getAmount().toPlainString()).setCurrency(CURRENCY.USD).build(),
                    taskExecuter));
            list.add(TRANSACTION.DEPOSIT.doTransact(
                    futureStub, BaseRequest.newBuilder().setUserID(userID)
                            .setAmount(AMOUNT.HUNDERED.getAmount().toPlainString()).setCurrency(CURRENCY.USD).build(),
                    taskExecuter));
            return list;
        }

    };

    public abstract List<ListenableFuture<BaseResponse>> goExecute(final WalletServiceFutureStub futureStub,
            final Long userID, final TaskExecutor taskExecuter);

}