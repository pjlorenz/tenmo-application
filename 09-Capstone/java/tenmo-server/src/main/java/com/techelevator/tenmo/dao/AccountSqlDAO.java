package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.model.Account;

@Service
public class AccountSqlDAO implements AccountDAO{

	private JdbcTemplate jdbcTemplate;
	private UserDAO userDAO;
	
	public AccountSqlDAO(JdbcTemplate jdbcTemplate, UserDAO userDAO) {
		 this.jdbcTemplate = jdbcTemplate;
		 this.userDAO = userDAO;
	}
	
	@Override
    public BigDecimal getAccountBalance(String username) {
		Integer userID = userDAO.findIdByUsername(username);
        return jdbcTemplate.queryForObject("select balance from accounts where user_id = ?", BigDecimal.class, userID);
    }
	
	@Override
    public int getAccountIdFromUsername(String username) {
		int userID = userDAO.findIdByUsername(username);
        return jdbcTemplate.queryForObject("SELECT accounts.account_id FROM accounts "
        		+ "INNER JOIN users ON users.user_id = accounts.user_id"
        		+ " WHERE accounts.user_id = ?", Integer.class, userID);
    }
	
	@Override
    public Account getAccountFromUsername(String username) {
		Account account = null;
		Integer userID = userDAO.findIdByUsername(username);
        String sql = "select account_id, user_id, balance from accounts where user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userID);
        
        while (results.next()) {
        	account = mapRowToAccount(results);
        }
        
        return account;
    }
	
	@Override
	public Account getAccountFromAccountId(int accountId) {
		Account account = null;
        String sql = "select account_id, user_id, balance from accounts where account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        
        while (results.next()) {
        	account = mapRowToAccount(results);
        }
        
        return account;
	}
	
	@Override
	public void updateBalance(Account account) {
		String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
		jdbcTemplate.update(sql, account.getAccountBalance(), account.getAccountId());
	}
	
	@Override
    public BigDecimal withdrawMoney(Principal principal, BigDecimal withdrawnAmount) {
		Account userAccount = getAccountFromUsername(principal.getName());
		Integer accountId = userAccount.getAccountId();
		BigDecimal currentBalance = userAccount.getAccountBalance();
		BigDecimal balanceAfterWithdraw = currentBalance;
		
		if (currentBalance.compareTo(withdrawnAmount) == 1 || currentBalance.compareTo(withdrawnAmount) == 0) {
			balanceAfterWithdraw.subtract(withdrawnAmount);
		}
		
		
        String sql = "UPDATE accounts SET balance = ? WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, balanceAfterWithdraw, accountId);
        
        return results.getBigDecimal("balance");
    }
	
	@Override
	public BigDecimal depositMoney(Principal principal, BigDecimal deposittedAmount) {
		Account userAccount = getAccountFromUsername(principal.getName());
		Integer accountId = userAccount.getAccountId();
		BigDecimal currentBalance = userAccount.getAccountBalance();
		BigDecimal balanceAfterWithdraw = currentBalance.add(deposittedAmount);
		
        String sql = "UPDATE accounts SET balance = ? WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, balanceAfterWithdraw, accountId);
        
        return results.getBigDecimal("balance");
	}
	
	private Account mapRowToAccount(SqlRowSet rs) {
		return new Account(rs.getInt("account_id"), rs.getInt("user_id"), rs.getBigDecimal("balance"));
	}
	
	@Override
	public String getUsernameFromAccountId(int accountId) {
		String sql = "SELECT user_id FROM accounts WHERE account_id = ?";
		int userId = 0;
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if (results.next()) {
          userId = results.getInt("user_id");
        }
        String sql2 = "SELECT username FROM users WHERE user_id = ?";
    
        
        SqlRowSet results2 = jdbcTemplate.queryForRowSet(sql2, userId);
        if (results2.next()) {
        return results2.getString("username");
        }
        return null;
	}
	
	@Override
	public Account getAccountByUserId(int userId) {
		String sql = "SELECT * FROM accounts WHERE user_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
		if (results.next()) {
			return mapRowToAccount(results);
		}
		return null;
	}

}
