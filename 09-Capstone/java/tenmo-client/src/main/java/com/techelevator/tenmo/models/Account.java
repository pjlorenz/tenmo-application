package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Account {
	
	private Integer accountId;
	private BigDecimal accountBalance;
	private Integer userId;
	
	public Integer getAccountId() {
		return accountId;
	}
	
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
		
		public BigDecimal getAccountBalance() {
		return accountBalance;
	}
	
	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public void transfer(Account accountTo, BigDecimal amount) {
		if (getAccountBalance().compareTo(amount) == 0 || getAccountBalance().compareTo(amount) == 1) {
			setAccountBalance(getAccountBalance().subtract(amount));
			accountTo.setAccountBalance(getAccountBalance().add(amount));
		}
	}
	

}
