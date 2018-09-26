package com.betpawa.wallet.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.betpawa.wallet.WalletServiceGrpc;
import com.betpawa.wallet.WalletServiceGrpc.WalletServiceFutureStub;

import io.grpc.Channel;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;

@Configuration
public class WalletClientConfig {
	@GrpcClient("local-grpc-server")
    private Channel serverChannel;
	
    @Bean
    WalletServiceFutureStub getWalletServiceFutureStub(){
    	return WalletServiceGrpc.newFutureStub(serverChannel);
    }
}
