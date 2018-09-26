package com.bp.wallet.server.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.betpawa.wallet.CURRENCY;
import com.betpawa.wallet.DepositRequest;
import com.betpawa.wallet.DepositResponse;
import com.betpawa.wallet.StatusMessage;
import com.betpawa.wallet.WalletServiceGrpc.WalletServiceImplBase;
import com.bp.wallet.server.auto.entities.generated.Wallet;
import com.bp.wallet.server.exception.BPServiceException;
import com.bp.wallet.server.repository.WalletRepository;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.springboot.autoconfigure.grpc.server.GrpcService;

@GrpcService(WalletServiceImplBase.class)
public class WalletServiceTemp extends WalletServiceImplBase {

	private static final Logger logger = LoggerFactory.getLogger(WalletServiceTemp.class);

	private final WalletRepository walletRepository;

	@Autowired
	public WalletServiceTemp(WalletRepository walletRepository) {
		super();
		this.walletRepository = walletRepository;
	}

	@Override
	public synchronized void deposit(DepositRequest request, StreamObserver<DepositResponse> responseObserver) {
		Float balanceToADD = request.getAmount();
		try {
			if (checkAmountGreaterThanZero(balanceToADD) && checkCurrency(request.getCurrency())) {
				Float currentBalance = Float.valueOf(0);
				Wallet userWallet;

				logger.info("Request Recieved for UserID:{} For Amount:{}{} ", request.getUserID(), request.getAmount(),
						request.getCurrency());
				Float newBalance = Float.sum(currentBalance, balanceToADD);

				walletRepository.save(new Wallet.Builder().userId(Long.valueOf(request.getUserID()))
						.balance(BigDecimal.valueOf(request.getAmount())).currency(request.getCurrency().name())
						.build());

				responseObserver.onNext(
						DepositResponse.newBuilder().setUserID(request.getUserID()).setAmount(newBalance).build());
				responseObserver.onCompleted();
				logger.info("Wallet Updated SuccessFully New Balance:{}", newBalance);

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

	public void deposit(Long userID, BigDecimal Amount, CURRENCY currency) {
		walletRepository.save(new Wallet.Builder().userId(userID).balance(Amount).currency(currency.name()).build());
	}

	private boolean checkAmountGreaterThanZero(Float amount) {
		boolean valid = false;
		if (amount > 0F && amount < Float.MAX_VALUE / 2F) {
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
}
