package com.bp.wallet.server.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bp.wallet.proto.BaseRequest;
import com.bp.wallet.proto.BaseResponse;
import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.proto.STATUS;
import com.bp.wallet.proto.StatusMessage;
import com.bp.wallet.proto.WalletServiceGrpc;
import com.bp.wallet.server.enity.Wallet;
import com.bp.wallet.server.enity.WalletPK;
import com.bp.wallet.server.exception.BPServiceException;
import com.bp.wallet.server.repository.WalletRepository;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.springboot.autoconfigure.grpc.server.GrpcService;

@GrpcService(WalletServiceGrpc.class)
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public class WalletServerService extends WalletServiceGrpc.WalletServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(WalletServerService.class);
    @Autowired
    private WalletRepository walletRepository;

    @Override
    public void deposit(final BaseRequest request, final StreamObserver<BaseResponse> responseObserver) {
        final BigDecimal balanceToADD = get(request.getAmount());
        try {
            if (checkAmountGreaterThanZero(balanceToADD) && checkCurrency(request.getCurrency())) {
                logger.info("Request Recieved for UserID:{} For Amount:{}{} ", request.getUserID(), request.getAmount(),
                        request.getCurrency());

                Optional<Wallet> wallet = walletRepository
                        .findByWalletPK_UserIDAndWalletPK_Currency(request.getUserID(), request.getCurrency());
                if (wallet.isPresent()) {
                    walletRepository.updateBalance(wallet.get().getBalance().add(balanceToADD), request.getUserID(),
                            request.getCurrency());
                } else {
                    walletRepository.saveAndFlush(
                            new Wallet(new WalletPK(request.getUserID(), request.getCurrency()), (balanceToADD)));
                }
                responseObserver.onNext(BaseResponse.newBuilder().setStatus(STATUS.TRANSACTION_SUCCESS).build());
                responseObserver.onCompleted();
                logger.info("Wallet Updated SuccessFully");
            } else {
                logger.warn(StatusMessage.AMOUNT_SHOULD_BE_GREATER_THAN_ZERO.name() + "OR"
                        + StatusMessage.INVALID_CURRENCY.name());
                responseObserver.onError(new StatusRuntimeException(Status.FAILED_PRECONDITION
                        .withDescription(StatusMessage.AMOUNT_SHOULD_BE_GREATER_THAN_ZERO.name() + "OR"
                                + StatusMessage.INVALID_CURRENCY.name())));
            }
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

            Optional<Wallet> wallet = walletRepository.findByWalletPK_UserIDAndWalletPK_Currency(request.getUserID(),
                    request.getCurrency());
            if (wallet.isPresent()) {
                if (wallet.get().getBalance().compareTo(balanceToWithdraw) >= 0) {
                    walletRepository.updateBalance(wallet.get().getBalance().subtract(balanceToWithdraw),
                            request.getUserID(), request.getCurrency());
                    responseObserver.onNext(BaseResponse.newBuilder().setStatus(STATUS.TRANSACTION_SUCCESS).build());
                    responseObserver.onCompleted();
                    logger.info("Wallet Updated SuccessFully");

                } else {
                    logger.warn(StatusMessage.INSUFFICIENT_BALANCE.name());
                    responseObserver.onError(new StatusRuntimeException(
                            Status.FAILED_PRECONDITION.withDescription(StatusMessage.INSUFFICIENT_BALANCE.name())));
                }
            } else {
                logger.warn(StatusMessage.USER_DOES_NOT_EXIST.name());
                responseObserver.onError(new StatusRuntimeException(
                        Status.FAILED_PRECONDITION.withDescription(StatusMessage.USER_DOES_NOT_EXIST.name())));
            }
        } catch (Exception e) {
            logger.error("------------>", e);
            responseObserver.onError(new StatusRuntimeException(Status.UNKNOWN.withDescription(e.getMessage())));
        }
    }

    @Override
    public void balance(final BaseRequest request, final StreamObserver<BaseResponse> responseObserver) {
        logger.info("Request Recieved for UserID:{}", request.getUserID());
        try {
            List<Wallet> userWallets = walletRepository.findByWalletPK_UserID(request.getUserID());
            final StringBuilder balance = new StringBuilder();
            userWallets.forEach(wallet -> {
                balance.append(wallet.getWalletPK().getCurrency() + ":" + wallet.getBalance());
            });
            logger.info(balance.toString());
            responseObserver.onNext(BaseResponse.newBuilder().setStatusMessage(balance.toString())
                    .setStatus((STATUS.TRANSACTION_SUCCESS)).build());
            responseObserver.onCompleted();
        } catch (BPServiceException e) {
            logger.warn(StatusMessage.USER_DOES_NOT_EXIST.name());
            responseObserver.onError(new StatusRuntimeException(
                    Status.FAILED_PRECONDITION.withDescription(StatusMessage.USER_DOES_NOT_EXIST.name())));
        } catch (Exception e) {
            logger.error("------------>", e);
            responseObserver.onError(new StatusRuntimeException(Status.UNKNOWN.withDescription(e.getMessage())));
        }

    }

    private boolean checkAmountGreaterThanZero(final BigDecimal amount) {
        boolean valid = false;
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            valid = true;
        }
        return valid;
    }

    private boolean checkCurrency(final CURRENCY currency) {
        boolean valid = true;
        if (currency.equals(CURRENCY.UNRECOGNIZED)) {
            valid = false;
        }
        return valid;
    }

    private BigDecimal get(final String val) {
        return new BigDecimal(val);
    }
}
