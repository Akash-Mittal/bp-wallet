package com.betpawa.wallet.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.grpc.Channel;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;

@Service
public class WalletClientService {
	@GrpcClient("bp-wallet-server")
	private Channel serverChannel;
    private static final Logger logger = LoggerFactory.getLogger(WalletClientService.class);

	public WalletClientResponse execute(final WalletClientRequest walletClientRequest)  {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			logger.error("",e);
		}
		return new WalletClientResponse.Builder().status(STATUS.SUCCESS).build();		
	}
}
