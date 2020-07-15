package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.security.Principal;

import com.techelevator.tenmo.model.Account;

public interface AccountDAO {
	
	BigDecimal getAccountBalance(String username);

	int getAccountIdFromUsername(String username);

	Account getAccountFromUsername(String username);

	BigDecimal withdrawMoney(Principal principal, BigDecimal withdrawnAmount);
	
	BigDecimal depositMoney(Principal principal, BigDecimal deposittedAmount);
	
	String getUsernameFromAccountId(int accountId);
	
	void updateBalance(Account account);
	
	Account getAccountByUserId(int userId);
	
	Account getAccountFromAccountId(int accountId);
}
