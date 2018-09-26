package com.betpawa.wallet.client.runner;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.betpawa.wallet.WalletServiceGrpc.WalletServiceFutureStub;
import com.betpawa.wallet.client.Client;

@Component
@Scope("prototype")
public class RoundRunner2 implements Runner {

	@Autowired
    private WalletServiceFutureStub walletServiceFutureStub;

	
	private String stats;
    
    private Integer userID;
    
    
    @Override
    public void run() {
        for (int i = 1; i <= numberOfrounds; i++) {
			Client.ROUND.values()[ThreadLocalRandom.current().nextInt(0, (Client.ROUND.values().length))]
                    .goExecute(walletServiceFutureStub, userID, stats + ":Round:" + i);
        }
    }

    
    public String getStats() {
		return stats;
	}

	public void setStats(String stats) {
		this.stats = stats;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public Integer getNumberOfrounds() {
		return numberOfrounds;
	}

	public void setNumberOfrounds(Integer numberOfrounds) {
		this.numberOfrounds = numberOfrounds;
	}

	private Integer numberOfrounds;
    


}
