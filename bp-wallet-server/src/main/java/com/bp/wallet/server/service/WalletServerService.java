package com.bp.wallet.server.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
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
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public class WalletServerService extends WalletServiceGrpc.WalletServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(WalletServerService.class);
    @Autowired
    private WalletRepository walletRepository;

    @Override
    @Retryable(value = { Exception.class,
            HibernateOptimisticLockingFailureException.class }, maxAttempts = 1, backoff = @Backoff(delay = 5000))
    public void deposit(final DepositRequest request, final StreamObserver<DepositResponse> responseObserver) {
        final BigDecimal balanceToADD = get(request.getAmount());
        try {
            if (checkAmountGreaterThanZero(balanceToADD) && checkCurrency(request.getCurrency())) {
                logger.debug("Request Recieved for UserID:{} For Amount:{}{} ", request.getUserID(),
                        request.getAmount(), request.getCurrency());

                Optional<Wallet> wallet = walletRepository
                        .findByWalletPK_UserIDAndWalletPK_Currency(request.getUserID(), request.getCurrency());
                if (wallet.isPresent()) {
                    walletRepository.updateBalance(wallet.get().getBalance().add(balanceToADD), request.getUserID(),
                            request.getCurrency());
                } else {
                    walletRepository.saveAndFlush(
                            new Wallet(new WalletPK(request.getUserID(), request.getCurrency()), (balanceToADD)));
                }
                responseObserver.onNext(DepositResponse.newBuilder().setUserID(request.getUserID()).build());
                responseObserver.onCompleted();
                logger.debug("Wallet Updated SuccessFully");

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
    public void withdraw(final WithdrawRequest request, final StreamObserver<WithdrawResponse> responseObserver) {

        logger.info("Request Recieved for UserID:{} For Amount:{}{} ", request.getUserID(), request.getAmount(),
                request.getCurrency());
        try {
            final BigDecimal balanceToWithdraw = get(request.getAmount());
            final BigDecimal existingBalance = BigDecimal.ZERO;

            if (checkAmountGreaterThanZero(balanceToWithdraw) && checkCurrency(request.getCurrency())) {
                walletRepository.findById(new WalletPK(request.getUserID(), request.getCurrency()))
                        .ifPresent(wallet -> {
                            existingBalance.add(wallet.getBalance());
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
                                // logger.warn(StatusMessage.INSUFFICIENT_BALANCE.name());
                                // responseObserver.onError(new StatusRuntimeException(Status.FAILED_PRECONDITION
                                // .withDescription(StatusMessage.INSUFFICIENT_BALANCE.name())));
                            }

                        });
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
    public void balance(final BalanceRequest request, final StreamObserver<BalanceResponse> responseObserver) {
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
