package com.bp.wallet.server.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.proto.DepositRequest;
import com.bp.wallet.proto.DepositResponse;
import com.bp.wallet.server.config.TestConfiguration;

import io.grpc.stub.StreamObserver;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class WalletServerServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(WalletServerServiceTest.class);

    @Autowired
    WalletServerService service;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testDeposit() throws InterruptedException {
        StreamObserver<DepositResponse> responseObserver = new StreamObserver<DepositResponse>() {
            @Override
            public void onNext(DepositResponse value) {
                logger.debug("onnext");
            }

            @Override
            public void onError(Throwable t) {
                logger.debug("error");

            }

            @Override
            public void onCompleted() {
                logger.debug("completed");
            }
        };

        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (final AtomicLong i = new AtomicLong(); i.get() < 100; i.incrementAndGet()) {
            pool.execute(new Thread() {
                @Override
                public void run() {
                    service.deposit(DepositRequest.newBuilder().setAmount("1000").setUserID(i.get())
                            .setCurrency(CURRENCY.GBP).build(), responseObserver);
                }
            });
        }
        pool.awaitTermination(40, TimeUnit.SECONDS);
        pool.shutdown();
        responseObserver.onCompleted();
    }
}
