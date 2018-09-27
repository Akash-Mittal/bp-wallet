package com.bp.wallet.server;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.server.enity.Wallet;
import com.bp.wallet.server.repository.WalletRepository;

@SpringBootApplication
public class GRPCServerApplication {

	private static final Logger log = LoggerFactory.getLogger(GRPCServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GRPCServerApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(WalletRepository repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new Wallet.Builder().balance(BigDecimal.TEN).userId(2L).currency(CURRENCY.EUR).build());
			repository.save(new Wallet.Builder().balance(BigDecimal.TEN).userId(21L).currency(CURRENCY.EUR).build()); // fetch
																														// all
																														// customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Wallet customer : repository.findAll()) {
				log.info(customer.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			repository.findById(1L).ifPresent(customer -> {
				log.info("Customer found with findById(1L):");
				log.info("--------------------------------");
				log.info(customer.toString());
				log.info("");
			});

			// fetch customers by last name
			log.info("Customer found with findByLastName('Bauer'):");
			log.info("--------------------------------------------");
			repository.findByCurrency(CURRENCY.EUR).forEach(bauer -> {
				log.info(bauer.toString());
			});
			log.info("");
		};
	}

}
