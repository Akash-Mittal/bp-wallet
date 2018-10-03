package com.bp.wallet.server.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bp.wallet.proto.BaseRequest;
import com.bp.wallet.proto.BaseResponse;
import com.bp.wallet.proto.STATUS;
import com.bp.wallet.proto.WalletServiceGrpc;
import com.bp.wallet.server.dto.BalanceResponseDTO;
import com.bp.wallet.server.enity.Wallet;
import com.bp.wallet.server.enity.WalletPK;
import com.bp.wallet.server.exception.BPValidationException;
import com.bp.wallet.server.repository.WalletRepository;
import com.bp.wallet.server.validation.BPAmountValidator;
import com.bp.wallet.server.validation.BPCurrencyValidator;
import com.bp.wallet.server.validation.BPWalletValidator;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.springboot.autoconfigure.grpc.server.GrpcService;

@GrpcService(WalletServiceGrpc.class)
@Transactional
public class WalletServerService extends WalletServiceGrpc.WalletServiceImplBase {

	private static final String COMMA = ",";
	private static final String COLON = ":";
	private static final Logger logger = LoggerFactory.getLogger(WalletServerService.class);
	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private BPAmountValidator bpAmountValidator;

	@Autowired
	private BPCurrencyValidator bpCurrencyValidator;
	@Autowired
	private BPWalletValidator bpWalletValidator;

	@Autowired
	private BalanceResponseDTO balanceResponseDTO;

	@Override
	public void deposit(final BaseRequest request, final StreamObserver<BaseResponse> responseObserver) {
		try {
			bpAmountValidator.validate(request.getAmount());
			bpCurrencyValidator.checkCurrency(request.getCurrency());
			final BigDecimal balanceToADD = get(request.getAmount());
			logger.info("Request Recieved for UserID:{} For Amount:{}{} ", request.getUserID(), request.getAmount(),
					request.getCurrency());

			Optional<Wallet> wallet = walletRepository.getUserWalletsByCurrencyAndUserID(request.getUserID(),
					request.getCurrency());

			if (wallet.isPresent()) {
				walletRepository.updateBalance(wallet.get().getBalance().add(balanceToADD), request.getUserID(),
						request.getCurrency());
			} else {
				walletRepository.saveAndFlush(
						new Wallet(new WalletPK(request.getUserID(), request.getCurrency()), balanceToADD));
			}
			responseObserver.onNext(BaseResponse.newBuilder().setStatus(STATUS.TRANSACTION_SUCCESS).build());
			responseObserver.onCompleted();
			logger.info("Wallet Updated SuccessFully");

		} catch (BPValidationException e) {
			logger.error(e.getErrorStatus().name());
			responseObserver
					.onError(new StatusRuntimeException(e.getStatus().withDescription(e.getErrorStatus().name())));
		} catch (Exception e) {
			logger.error("------------>", e);
			responseObserver.onError(new StatusRuntimeException(Status.UNKNOWN.withDescription(e.getMessage())));
		}
	}

	@Override
	public void withdraw(final BaseRequest request, final StreamObserver<BaseResponse> responseObserver) {

		logger.info("Request Recieved for UserID:{} For Amount:{}{} ", request.getUserID(), request.getAmount(),
				request.getCurrency());
		try {
			final BigDecimal balanceToWithdraw = get(request.getAmount());
			bpAmountValidator.validate(request.getAmount());
			bpCurrencyValidator.checkCurrency(request.getCurrency());
			Optional<Wallet> wallet = walletRepository.getUserWalletsByCurrencyAndUserID(request.getUserID(),
					request.getCurrency());

			bpWalletValidator.validateWallet(wallet);
			bpAmountValidator.checkAmountLessThanBalance(wallet.get().getBalance(), balanceToWithdraw);
			walletRepository.updateBalance(wallet.get().getBalance().subtract(balanceToWithdraw), request.getUserID(),
					request.getCurrency());
			responseObserver.onNext(BaseResponse.newBuilder().setStatus(STATUS.TRANSACTION_SUCCESS).build());
			responseObserver.onCompleted();
			logger.info("Wallet Updated SuccessFully");
		} catch (BPValidationException e) {
			logger.error(e.getErrorStatus().name());
			responseObserver
					.onError(new StatusRuntimeException(e.getStatus().withDescription(e.getErrorStatus().name())));
		} catch (Exception e) {
			logger.error("------------>", e);
			responseObserver.onError(new StatusRuntimeException(Status.UNKNOWN.withDescription(e.getMessage())));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public void balance(final BaseRequest request, final StreamObserver<BaseResponse> responseObserver) {
		logger.info("Request Recieved for UserID:{}", request.getUserID());
		try {
			Optional<List<Wallet>> userWallets = walletRepository.findByWalletPK_UserID(request.getUserID());
			bpWalletValidator.validate(userWallets);
			String balance = balanceResponseDTO.getBalanceAsString(userWallets);
			logger.info(balance);
			responseObserver.onNext(BaseResponse.newBuilder().setStatusMessage(balance)
					.setStatus((STATUS.TRANSACTION_SUCCESS)).build());
			responseObserver.onCompleted();
		} catch (BPValidationException e) {
			logger.error(e.getErrorStatus().name());
			responseObserver
					.onError(new StatusRuntimeException(e.getStatus().withDescription(e.getErrorStatus().name())));
		} catch (Exception e) {
			logger.error("------------>", e);
			responseObserver.onError(new StatusRuntimeException(Status.UNKNOWN.withDescription(e.getMessage())));
		}

	}

	private BigDecimal get(final String val) {
		return new BigDecimal(val);
	}
}
