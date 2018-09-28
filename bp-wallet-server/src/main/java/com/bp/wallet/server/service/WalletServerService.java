package com.bp.wallet.server.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bp.wallet.proto.Balance;
import com.bp.wallet.proto.BalanceRequest;
import com.bp.wallet.proto.BalanceResponse;
import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.proto.DepositRequest;
import com.bp.wallet.proto.DepositResponse;
import com.bp.wallet.proto.StatusMessage;
import com.bp.wallet.proto.WalletServiceGrpc;
import com.bp.wallet.proto.WithdrawRequest;
import com.bp.wallet.proto.WithdrawResponse;
import com.bp.wallet.server.enity.Wallet;
import com.bp.wallet.server.enity.WalletPK;
import com.bp.wallet.server.exception.BPServiceException;
import com.bp.wallet.server.repository.WalletRepository;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.springboot.autoconfigure.grpc.server.GrpcService;

@GrpcService(WalletServiceGrpc.class)
public class WalletServerService extends WalletServiceGrpc.WalletServiceImplBase {

	private static final Logger logger = LoggerFactory.getLogger(WalletServerService.class);

	private final WalletRepository walletRepository;

	@Autowired
	public WalletServerService(WalletRepository walletRepository) {
		super();
		this.walletRepository = walletRepository;
	}

	@Override
	@Transactional
	public synchronized void deposit(DepositRequest request, StreamObserver<DepositResponse> responseObserver) {
		BigDecimal balanceToADD = get(request.getAmount());
		try {
			if (checkAmountGreaterThanZero(balanceToADD) && checkCurrency(request.getCurrency())) {
				BigDecimal currentBalance = BigDecimal.ZERO;
				logger.info("Request Recieved for UserID:{} For Amount:{}{} ", request.getUserID(), request.getAmount(),
						request.getCurrency());
				BigDecimal newBalance = currentBalance.add(balanceToADD);
				walletRepository.save(
						new Wallet(new WalletPK(request.getUserID(), request.getCurrency()), get(request.getAmount())));

				responseObserver.onNext(DepositResponse.newBuilder().setUserID(request.getUserID())
						.setAmount(newBalance.toPlainString()).build());
				responseObserver.onCompleted();
				logger.info("Wallet Updated SuccessFully New Balance:{}", newBalance);

			} else {
				logger.warn(StatusMessage.AMOUNT_SHOULD_BE_GREATER_THAN_ZERO.name() + "OR"
						+ StatusMessage.INVALID_CURRENCY.name());
				responseObserver.onError(new StatusRuntimeException(Status.FAILED_PRECONDITION
						.withDescription(StatusMessage.AMOUNT_SHOULD_BE_GREATER_THAN_ZERO.name() + "OR"
								+ StatusMessage.INVALID_CURRENCY.name())));
			}
			super.deposit(request, responseObserver);
		} catch (BPServiceException e) {
			responseObserver.onError(new StatusRuntimeException(e.getStatus().withDescription(e.getMessage())));

		} catch (Exception e) {
			logger.error(StatusMessage.UNRECOGNIZED.name(), e);
			responseObserver.onError(
					new StatusRuntimeException(Status.UNKNOWN.withDescription(StatusMessage.UNRECOGNIZED.name())));
		}
	}

	@Override
	@Transactional

	public synchronized void withdraw(WithdrawRequest request, StreamObserver<WithdrawResponse> responseObserver) {

		logger.info("Request Recieved for UserID:{} For Amount:{}{} ", request.getUserID(), request.getAmount(),
				request.getCurrency());
		try {
			BigDecimal balanceToWithdraw = get(request.getAmount());
			if (checkAmountGreaterThanZero(balanceToWithdraw) && checkCurrency(request.getCurrency())) {
				walletRepository.findById(new WalletPK(request.getUserID(), request.getCurrency()))
						.ifPresent(wallet -> {
							BigDecimal existingBalance = wallet.getBalance();
							if (existingBalance.compareTo(balanceToWithdraw) >= 0) {
								BigDecimal newBalance = existingBalance.subtract(balanceToWithdraw);
								wallet.setBalance(newBalance);
								walletRepository.save(wallet);
								responseObserver
										.onNext(WithdrawResponse.newBuilder().setBalance(newBalance.toPlainString())
												.setCurrency(request.getCurrency()).build());
								responseObserver.onCompleted();
								logger.info("Wallet Updated SuccessFully New Balance:{}", newBalance);

							} else {
								logger.warn(StatusMessage.INSUFFICIENT_BALANCE.name());
								responseObserver.onError(new StatusRuntimeException(Status.FAILED_PRECONDITION
										.withDescription(StatusMessage.INSUFFICIENT_BALANCE.name())));
							}

						});
			} else {
				logger.warn(StatusMessage.AMOUNT_SHOULD_BE_GREATER_THAN_ZERO.name() + "OR"
						+ StatusMessage.INVALID_CURRENCY.name());
				responseObserver.onError(new StatusRuntimeException(Status.FAILED_PRECONDITION
						.withDescription(StatusMessage.AMOUNT_SHOULD_BE_GREATER_THAN_ZERO.name() + "OR"
								+ StatusMessage.INVALID_CURRENCY.name())));
			}
		} catch (BPServiceException e) {
			responseObserver.onError(new StatusRuntimeException(e.getStatus().withDescription(e.getMessage())));

		} catch (Exception e) {
			logger.error(StatusMessage.UNRECOGNIZED.name(), e);
			responseObserver.onError(
					new StatusRuntimeException(Status.UNKNOWN.withDescription(StatusMessage.UNRECOGNIZED.name())));
		}
	}

	@Override
	@Transactional
	public synchronized void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
		logger.info("Request Recieved for UserID:{}", request.getUserID());
		try {
			List<Wallet> userWallets = walletRepository.findByWalletPK_UserID(request.getUserID());
			List<Balance> balanceList = new ArrayList<>();

			final StringBuilder balance = new StringBuilder();
			userWallets.forEach(wallet -> {
				Balance bl = Balance.newBuilder().setAmount(wallet.getBalance().toPlainString())
						.setCurrency(wallet.getWalletPK().getCurrency()).build();
				balance.append(wallet.getWalletPK().getCurrency() + ":" + wallet.getBalance());
				balanceList.add(bl);
			});
			logger.info(balance.toString());
			responseObserver.onNext(BalanceResponse.newBuilder().addAllRemainingBalance(balanceList).build());
			responseObserver.onCompleted();
		} catch (BPServiceException e) {
			logger.warn(StatusMessage.USER_DOES_NOT_EXIST.name());
			responseObserver.onError(new StatusRuntimeException(
					Status.FAILED_PRECONDITION.withDescription(StatusMessage.USER_DOES_NOT_EXIST.name())));

		} catch (Exception e) {
			logger.error(StatusMessage.UNRECOGNIZED.name(), e);
			responseObserver.onError(
					new StatusRuntimeException(Status.UNKNOWN.withDescription(StatusMessage.UNRECOGNIZED.name())));
		}

	}

	private boolean checkAmountGreaterThanZero(BigDecimal amount) {
		boolean valid = false;
		if (amount.compareTo(BigDecimal.ZERO) > 0) {
			valid = true;
		}
		return valid;
	}

	private boolean checkCurrency(CURRENCY currency) {
		boolean valid = true;
		if (currency.equals(CURRENCY.UNRECOGNIZED)) {
			valid = false;
		}
		return valid;
	}

	private BigDecimal get(String val) {
		return new BigDecimal(val);
	}
}
