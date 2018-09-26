package com.betpawa.wallet.client;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bp.wallet.proto.BalanceRequest;
import com.bp.wallet.proto.BalanceResponse;
import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.proto.DepositRequest;
import com.bp.wallet.proto.DepositResponse;
import com.bp.wallet.proto.WalletServiceGrpc.WalletServiceFutureStub;
import com.bp.wallet.proto.WithdrawRequest;
import com.bp.wallet.proto.WithdrawResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import io.grpc.Status;

public interface Client {

	int MEAN_FACTOR = 3;
	int DEFAULT_WAIT_TIME = 1;
	int AVERAGE_RPC_PER_ROUND = 7;

	enum AMOUNT {

		ZERO(BigDecimal.valueOf(0)), HUNDERED(BigDecimal.valueOf(100)), TWOHUNDERED(BigDecimal.valueOf(200)),
		THREEHUNDERED(BigDecimal.valueOf(300)), FOURHUNDERED(BigDecimal.valueOf(400)),
		FIVEHUNDERED(BigDecimal.valueOf(5000));

		private BigDecimal amount;

		public BigDecimal getAmount() {
			return amount;
		}

		private AMOUNT(BigDecimal amount) {
			this.amount = amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}
	}

	enum ROUND {
		A {
			@Override
			public void goExecute(final WalletServiceFutureStub futureStub, final Long userID, final String stats) {
				TRANSACTION.DEPOSIT.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.USD,
						A.name() + ":" + stats);
				TRANSACTION.WITHDRAW.doTransact(futureStub, userID, AMOUNT.TWOHUNDERED.getAmount(), CURRENCY.USD,
						A.name() + ":" + stats);
				TRANSACTION.DEPOSIT.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.EUR,
						A.name() + ":" + stats);
				TRANSACTION.WITHDRAW.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.USD,
						A.name() + ":" + stats);
				TRANSACTION.BALANCE.doTransact(futureStub, userID, null, null, A.name() + ":" + stats);
				TRANSACTION.WITHDRAW.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.USD,
						A.name() + ":" + stats);
			}
		},
		B {
			@Override
			public void goExecute(final WalletServiceFutureStub futureStub, final Long userID, final String stats) {
				TRANSACTION.WITHDRAW.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.GBP,
						B.name() + ":" + stats);
				TRANSACTION.DEPOSIT.doTransact(futureStub, userID, AMOUNT.THREEHUNDERED.getAmount(), CURRENCY.GBP,
						B.name() + ":" + stats);
				TRANSACTION.WITHDRAW.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.GBP,
						B.name() + ":" + stats);
				TRANSACTION.WITHDRAW.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.GBP,
						B.name() + ":" + stats);
				TRANSACTION.WITHDRAW.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.GBP,
						B.name() + ":" + stats);
				TRANSACTION.WITHDRAW.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.GBP,
						B.name() + ":" + stats);
				TRANSACTION.WITHDRAW.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.GBP,
						B.name() + ":" + stats);
			}
		},
		C {
			@Override
			public void goExecute(final WalletServiceFutureStub futureStub, final Long userID, final String stats) {
				TRANSACTION.BALANCE.doTransact(futureStub, userID, null, null, C.name() + ":" + stats);
				TRANSACTION.DEPOSIT.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.USD,
						C.name() + ":" + stats);
				TRANSACTION.DEPOSIT.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.USD,
						C.name() + ":" + stats);
				TRANSACTION.WITHDRAW.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.USD,
						C.name() + ":" + stats);
				TRANSACTION.DEPOSIT.doTransact(futureStub, userID, AMOUNT.HUNDERED.getAmount(), CURRENCY.USD,
						C.name() + ":" + stats);
				TRANSACTION.BALANCE.doTransact(futureStub, userID, null, null, C.name() + ":" + stats);
				TRANSACTION.WITHDRAW.doTransact(futureStub, userID, AMOUNT.TWOHUNDERED.getAmount(), CURRENCY.USD,
						C.name() + ":" + stats);
				TRANSACTION.BALANCE.doTransact(futureStub, userID, null, null, C.name() + ":" + stats);

			}
		};

		public abstract void goExecute(final WalletServiceFutureStub futureStub, final Long userID, final String stats);

	}

	enum TRANSACTION {

		DEPOSIT {
			@Override
			public ClientResponse doTransact(final WalletServiceFutureStub futureStub, final Long userID,
					final BigDecimal amount, final CURRENCY currency, final String stats) {
				final ClientResponse clientResponse = new ClientResponse.Builder().execution_STATUS(STATUS.FAIL)
						.build();
				logger.info(stats + DEPOSIT.name());

				ListenableFuture<DepositResponse> response = futureStub.deposit(DepositRequest.newBuilder()
						.setAmount(amount.toPlainString()).setUserID(userID).setCurrency(currency).build());
				final CountDownLatch latch = new CountDownLatch(1);

				Futures.addCallback(response, new FutureCallback<DepositResponse>() {
					@Override
					public void onSuccess(DepositResponse result) {

						logger.info("Deposited Succesfully", result.getCurrencyValue());
						clientResponse.setExecutionStatus(STATUS.SUCCESS);
						clientResponse.setDepositResponse(result);
						latch.countDown();

					}

					@Override
					public void onFailure(Throwable t) {
						logger.warn(Status.fromThrowable(t).getDescription());
						latch.countDown();

					}
				});
				return clientResponse;
			}
		},
		WITHDRAW {
			@Override
			public ClientResponse doTransact(final WalletServiceFutureStub futureStub, final Long userID,
					final BigDecimal amount, final CURRENCY currency, final String stats) {
				logger.info(stats + WITHDRAW.name());
				final ClientResponse clientResponse = new ClientResponse.Builder().execution_STATUS(STATUS.FAIL)
						.build();
				ListenableFuture<WithdrawResponse> response = null;
				response = futureStub.withdraw(WithdrawRequest.newBuilder().setUserID(userID)
						.setAmount(amount.toPlainString()).setCurrency(currency).build());
				final CountDownLatch latch = new CountDownLatch(1);

				Futures.addCallback(response, new FutureCallback<WithdrawResponse>() {
					@Override
					public void onSuccess(WithdrawResponse result) {

						logger.info("Withdrawn Succesfully" + result.getBalance());
						clientResponse.setExecutionStatus(STATUS.SUCCESS);
						clientResponse.setWithdrawResponse(result);
						latch.countDown();

					}

					@Override
					public void onFailure(Throwable t) {
						logger.warn(Status.fromThrowable(t).getDescription());
						latch.countDown();

					}
				});
				return clientResponse;

			}
		},
		BALANCE {
			@Override
			public ClientResponse doTransact(final WalletServiceFutureStub futureStub, final Long userID,
					final BigDecimal amount, final CURRENCY currency, final String stats) {
				final ClientResponse clientResponse = new ClientResponse.Builder().execution_STATUS(STATUS.FAIL)
						.build();
				logger.info(stats + BALANCE.name());
				ListenableFuture<BalanceResponse> response = futureStub
						.balance(BalanceRequest.newBuilder().setUserID(userID).build());
				final CountDownLatch latch = new CountDownLatch(1);

				Futures.addCallback(response, new FutureCallback<BalanceResponse>() {
					@Override
					public void onSuccess(BalanceResponse result) {
						logger.info("Balance Checked for user:{} Amount:{}", userID, buildGetBalanceLogLine(result));
						clientResponse.setExecutionStatus(STATUS.SUCCESS);
						clientResponse.setBalanceResponse(result);
						latch.countDown();

					}

					@Override
					public void onFailure(Throwable t) {
						logger.warn(Status.fromThrowable(t).getDescription());
						latch.countDown();

					}
				});
				return clientResponse;

			}
		};

		public abstract ClientResponse doTransact(final WalletServiceFutureStub futureStub, final Long userID,
				final BigDecimal amount, final CURRENCY currency, final String stats);

		private static final Logger logger = LoggerFactory.getLogger(TRANSACTION.class);

	}

	static Long getOptimizedWaitingTime(final WalletClientParams clientParams) {
		Long defaultWaitTime = Long.valueOf(DEFAULT_WAIT_TIME);
		defaultWaitTime = Long.valueOf((clientParams.getNumberOfUsers() * clientParams.getNumberOfRequests()
				* clientParams.getNumberOfRounds() * AVERAGE_RPC_PER_ROUND) / MEAN_FACTOR);
		return defaultWaitTime;
	}

	static Long getTotalNumberOfRPCS(final WalletClientParams clientParams) {
		return getOptimizedWaitingTime(clientParams) * MEAN_FACTOR;
	}

	static void pingServer() {
		// For Health Check
	}

	static String buildGetBalanceLogLine(BalanceResponse balanceResponse) {
		StringBuilder stringBuilder = new StringBuilder();
		balanceResponse.getRemainingBalanceList().stream().forEach(balance -> {
			stringBuilder.append(balance.getAmount()).append(balance.getCurrency().name()).append(" ");
		});
		return stringBuilder.toString();
	}
}