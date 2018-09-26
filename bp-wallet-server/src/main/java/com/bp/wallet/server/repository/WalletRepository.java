package com.bp.wallet.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bp.wallet.server.auto.entities.generated.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

}
