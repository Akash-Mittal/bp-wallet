package com.bp.wallet.server.application;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_wallet")
public class Wallet implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "user_id")
	private Long userId;
	private BigDecimal balance;
	private String currency;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public static class Builder {
		private Long userId;
		private BigDecimal balance;
		private String currency;

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder balance(BigDecimal balance) {
			this.balance = balance;
			return this;
		}

		public Builder currency(String currency) {
			this.currency = currency;
			return this;
		}

		public Wallet build() {
			return new Wallet(this);
		}
	}

	private Wallet(Builder builder) {
		this.userId = builder.userId;
		this.balance = builder.balance;
		this.currency = builder.currency;
	}
}