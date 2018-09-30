package com.bp.wallet.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableRetry
public class BPServerApplication {

    private static final Logger log = LoggerFactory.getLogger(BPServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BPServerApplication.class, args);
    }
}
