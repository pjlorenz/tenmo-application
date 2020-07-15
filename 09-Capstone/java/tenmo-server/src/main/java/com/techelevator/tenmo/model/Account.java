package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {
	
	private Integer accountId;
	private Integer userId;
	private BigDecimal accountBalance;
	
	public Account() { }
	
	public Account(Integer accountId, Integer userId, BigDecimal accountBalance) {
		this.accountId = accountId;
		this.userId = userId;
		this.accountBalance = accountBalance;
	}
	
	public Integer getAccountId() {
		return accountId;
	}
	
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	
	public Integer getUserId() {
		return userId;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
		
		public BigDecimal getAccountBalance() {
		return accountBalance;
	}
	
	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}
	
	public void addMoney(BigDecimal amount) {
		accountBalance = accountBalance.add(amount);
	}
	
	public void subtractMoney(BigDecimal amount) {
		accountBalance = accountBalance.subtract(amount);
	}
	
	public void transfer(Account accountTo, BigDecimal amount) {
		if (getAccountBalance().compareTo(amount) == 0 || getAccountBalance().compareTo(amount) == 1) {
			setAccountBalance(getAccountBalance().subtract(amount));
			accountTo.setAccountBalance(accountTo.getAccountBalance().add(amount));
		}
		
	}
	
}
