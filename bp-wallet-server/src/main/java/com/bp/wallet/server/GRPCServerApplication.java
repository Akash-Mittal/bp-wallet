package com.bp.wallet.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GRPCServerApplication {

	private static final Logger log = LoggerFactory.getLogger(GRPCServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GRPCServerApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner demo(WalletRepository repository) {
//		return (args) -> {
//			// customers
//			log.info("Customers found with findAll():");
//			log.info("-------------------------------");
//			for (Wallet customer : repository.findAll()) {
//				log.info(customer.toString());
//			}
//			log.info("");
//
//			// fetch customers by last name
//			log.info("Customer found with findByLastName('Bauer'):");
//			log.info("--------------------------------------------");
//			repository.findByCurrency(CURRENCY.EUR).forEach(bauer -> {
//				log.info(bauer.toString());
//			});
//			log.info("");
//		};
//	}

}
