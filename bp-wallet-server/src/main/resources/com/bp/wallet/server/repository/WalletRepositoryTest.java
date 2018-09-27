package com.bp.wallet.server.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.server.enity.Wallet;

@RunWith(SpringRunner.class)
@DataJpaTest
public class WalletRepositoryTest {

	@Autowired
	WalletRepository repository;

	@Before
	public void setUp() throws Exception {
		repository.deleteAll();
		repository.save(new Wallet.Builder().balance(BigDecimal.TEN).userId(12L).currency(CURRENCY.EUR).build());
		repository.save(new Wallet.Builder().balance(BigDecimal.TEN).userId(22L).currency(CURRENCY.EUR).build());
		repository.save(new Wallet.Builder().balance(BigDecimal.TEN).userId(32L).currency(CURRENCY.EUR).build());
		repository.save(new Wallet.Builder().balance(BigDecimal.TEN).userId(42L).currency(CURRENCY.EUR).build());
	}

	@Test
	public void testSetupWithFindAll() {
		List<Wallet> wallets = repository.findAll();
		assertThat(wallets).hasSize(4);
	}

}
