package com.betpawa.wallet.client.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.betpawa.wallet.client.enums.AMOUNT;
import com.betpawa.wallet.client.enums.TRANSACTION;
import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.proto.WalletServiceGrpc.WalletServiceFutureStub;

@Component
@Scope("prototype")
public class RoundRunner implements Runner {

    @Autowired
    private WalletServiceFutureStub walletServiceFutureStub;
    private Long numberOfrounds;
    private String stats;
    private Long userID;

    public RoundRunner(Long numberOfrounds, String stats, Long userID) {
        super();
        this.numberOfrounds = numberOfrounds;
        this.stats = stats;
        this.userID = userID;
    }

    @Override
    public void run() {
        for (int i = 1; i <= numberOfrounds; i++) {
            // ROUND.values()[ThreadLocalRandom.current().nextInt(0, (ROUND.values().length))]
            // .goExecute(walletServiceFutureStub, userID, stats + ":Round:" + i);
            TRANSACTION.DEPOSIT.doTransact(walletServiceFutureStub, userID, AMOUNT.FIVEHUNDERED.getAmount(),
                    CURRENCY.GBP, stats);
            TRANSACTION.WITHDRAW.doTransact(walletServiceFutureStub, userID, AMOUNT.FIVEHUNDERED.getAmount(),
                    CURRENCY.GBP, stats);
            TRANSACTION.BALANCE.doTransact(walletServiceFutureStub, userID, AMOUNT.FIVEHUNDERED.getAmount(),
                    CURRENCY.GBP, stats);

        }
    }

    public String getStats() {
        return stats;
    }

    public void setStats(String stats) {
        this.stats = stats;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getNumberOfrounds() {
        return numberOfrounds;
    }

    public void setNumberOfrounds(Long numberOfrounds) {
        this.numberOfrounds = numberOfrounds;
    }

}
