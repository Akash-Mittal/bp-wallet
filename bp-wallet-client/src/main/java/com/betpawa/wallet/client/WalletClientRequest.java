package com.betpawa.wallet.client;

import java.util.concurrent.ExecutorService;

import com.betpawa.wallet.WalletServiceGrpc.WalletServiceFutureStub;

public final class WalletClientRequest {

    private  Integer numberOfUsers;
    private Integer numberOfRequests;
    private Integer numberOfRounds;
	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}
	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}
	public Integer getNumberOfRequests() {
		return numberOfRequests;
	}
	public void setNumberOfRequests(Integer numberOfRequests) {
		this.numberOfRequests = numberOfRequests;
	}
	public Integer getNumberOfRounds() {
		return numberOfRounds;
	}
	public void setNumberOfRounds(Integer numberOfRounds) {
		this.numberOfRounds = numberOfRounds;
	}
}
