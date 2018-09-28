package com.bp.wallet.server.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bp.wallet.proto.CURRENCY;
import com.bp.wallet.server.enity.Wallet;
import com.bp.wallet.server.enity.WalletPK;

@RunWith(SpringRunner.class)
@DataJpaTest
public class WalletRepositoryTest {

	@Autowired
	WalletRepository repository;

	@Before
	public void setUp() throws Exception {
		repository.deleteAll();
		Random random = new Random(123L);
		for (Long userID = 100L; userID < 200; userID++)
			repository.save(new Wallet(new WalletPK(userID, CURRENCY.EUR), BigDecimal.valueOf(random.nextInt(3213))));
	}

	@Test
	public void testSetupWithFindAll() {
		List<Wallet> wallets = repository.findAll();
		assertThat(wallets).hasSize(100);
	}

	@Test
	public void testFindByUserID() {
		List<Wallet> wallets = repository.findByWalletPK_UserID(100L);
		assertThat(wallets).hasSize(1);
	}

}
