package com.bp.wallet.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.server.enity.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
	List<Wallet> findByCurrency(CURRENCY currency);

}
