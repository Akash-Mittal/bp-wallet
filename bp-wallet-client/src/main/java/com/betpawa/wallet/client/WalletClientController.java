package com.betpawa.wallet.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class WalletClientController {

	@Autowired
	private WalletClientService walletClientService;

	@PostMapping
	public WalletClientResponse printMessage(@RequestBody WalletClientRequest walletClientRequest) {
		return walletClientService.execute(walletClientRequest);
	}
}
