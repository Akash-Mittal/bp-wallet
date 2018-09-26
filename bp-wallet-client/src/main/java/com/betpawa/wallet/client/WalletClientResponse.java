package com.betpawa.wallet.client;

public class WalletClientResponse {

	private STATUS status;

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public static class Builder {
		private STATUS status;

		public Builder status(STATUS status) {
			this.status = status;
			return this;
		}

		public WalletClientResponse build() {
			return new WalletClientResponse(this);
		}
	}

	private WalletClientResponse(Builder builder) {
		this.status = builder.status;
	}
}
