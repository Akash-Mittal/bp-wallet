package com.betpawa.wallet.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.betpawa.wallet.client.runner.RoundRunner2;

import io.grpc.Channel;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;

@Service
public class WalletClientService {
	private static final Logger logger = LoggerFactory.getLogger(WalletClientService.class);
	@GrpcClient("bp-wallet-server")
	private Channel serverChannel;
	
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationContext applicationContext;
    

	public WalletClientResponse execute(final WalletClientRequest walletClientRequest)  {
		try {
			Thread.sleep(2000);
			RoundRunner2 myThread = applicationContext.getBean(RoundRunner2.class);
			myThread.setNumberOfrounds(walletClientRequest.getNumberOfRounds());
			myThread.setStats("");
			myThread.setUserID(1);
	        taskExecutor.execute(myThread);
		} catch (InterruptedException e) {
			logger.error("",e);
			return new WalletClientResponse.Builder().status(STATUS.FAIL).build();		

		}
		return new WalletClientResponse.Builder().status(STATUS.SUCCESS).build();		
	}
}
