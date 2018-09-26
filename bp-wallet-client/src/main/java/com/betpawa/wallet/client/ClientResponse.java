package com.betpawa.wallet.client;

import com.betpawa.wallet.BalanceResponse;
import com.betpawa.wallet.DepositResponse;
import com.betpawa.wallet.WithdrawResponse;

public class ClientResponse {
    private DepositResponse depositResponse;
    private WithdrawResponse withdrawResponse;
    private BalanceResponse balanceResponse;
    private STATUS execution_STATUS;

    public DepositResponse getDepositResponse() {
        return depositResponse;
    }

    public void setDepositResponse(DepositResponse depositResponse) {
        this.depositResponse = depositResponse;
    }

    public WithdrawResponse getWithdrawResponse() {
        return withdrawResponse;
    }

    public void setWithdrawResponse(WithdrawResponse withdrawResponse) {
        this.withdrawResponse = withdrawResponse;
    }

    public BalanceResponse getBalanceResponse() {
        return balanceResponse;
    }

    public void setBalanceResponse(BalanceResponse balanceResponse) {
        this.balanceResponse = balanceResponse;
    }

    public STATUS getExecution_STATUS() {
        return execution_STATUS;
    }

    public void setExecutionStatus(STATUS execution_STATUS) {
        this.execution_STATUS = execution_STATUS;
    }

    public static class Builder {
        private DepositResponse depositResponse;
        private WithdrawResponse withdrawResponse;
        private BalanceResponse balanceResponse;
        private STATUS execution_STATUS;

        public Builder depositResponse(DepositResponse depositResponse) {
            this.depositResponse = depositResponse;
            return this;
        }

        public Builder withdrawResponse(WithdrawResponse withdrawResponse) {
            this.withdrawResponse = withdrawResponse;
            return this;
        }

        public Builder balanceResponse(BalanceResponse balanceResponse) {
            this.balanceResponse = balanceResponse;
            return this;
        }

        public Builder execution_STATUS(STATUS execution_STATUS) {
            this.execution_STATUS = execution_STATUS;
            return this;
        }

        public ClientResponse build() {
            return new ClientResponse(this);
        }
    }

    private ClientResponse(Builder builder) {
        this.depositResponse = builder.depositResponse;
        this.withdrawResponse = builder.withdrawResponse;
        this.balanceResponse = builder.balanceResponse;
        this.execution_STATUS = builder.execution_STATUS;
    }
}
