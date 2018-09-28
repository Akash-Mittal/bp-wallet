package com.bp.wallet.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bp.wallet.server.enity.Wallet;
import com.bp.wallet.server.enity.WalletPK;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, WalletPK> {
    List<Wallet> findByWalletPK_UserID(Long userID);

}
