package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.hibernate.validator.internal.util.logging.Log;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.NewTransferDTO;
import com.techelevator.tenmo.model.Transfer;


@Service
public class TransferSqlDAO implements TransferDAO{

	private UserDAO userDAO;
	private AccountDAO accountDAO;
	private JdbcTemplate jdbcTemplate;
	
	public TransferSqlDAO(UserDAO userDAO, AccountDAO accountDAO, JdbcTemplate jdbcTemplate) {
		this.userDAO = userDAO;
		this.accountDAO = accountDAO;
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public boolean createSendTransfer(NewTransferDTO transfer) {
		boolean transferCreated = false;
		int transferTypeId = 2;
		int transferStatusId = 2;
		String receivingUserName = accountDAO.getUsernameFromAccountId(transfer.getReceivingAccountId());
		String sendingUserName = accountDAO.getUsernameFromAccountId(transfer.getSendingAccountId());
		if (transfer.getAmount().compareTo(accountDAO.getAccountFromUsername(sendingUserName).getAccountBalance()) == 0 || 
				transfer.getAmount().compareTo(accountDAO.getAccountFromUsername(sendingUserName).getAccountBalance()) == 1) {
			transferStatusId = 3;
		}
		int accountFromId = transfer.getSendingAccountId();
		int accountToId = transfer.getReceivingAccountId();
        String insertTransfer = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount)"
        		+ " VALUES (?, ?, ?, ?, ?)";
      
        transferCreated = jdbcTemplate.update(insertTransfer, transferTypeId, transferStatusId, accountFromId, accountToId, transfer.getAmount()) == 1;
        
        
       return transferCreated;
	}
	
	@Override
	public Transfer getTransferById(int transferId) {
		Transfer transfer = null;
		String sql = "SELECT * FROM transfers WHERE transfer_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
		while (results.next()) {
			transfer = mapRowToTransfer(results);
		}
		return transfer;
	}
	
	@Override
	public List<Transfer> findAll() {
		List<Transfer> transfers = new ArrayList<>();
		String sql = "SELECT * FROM transfers";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while(results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}
		return transfers;
	}
	
	@Override
	public List<Transfer> getTransfersForUser(int userId) { 
		List<Transfer> transfers = new ArrayList<>();
		String sql = "SELECT * FROM transfers " 
				+ "WHERE (account_from IN (SELECT account_id FROM accounts WHERE user_id = ?) "
				+ "OR account_to IN (SELECT account_id FROM accounts WHERE user_id = ?))";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
		while(results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}
		return transfers;
		
	}
	
	@Override
	public Transfer getTransferFromTransferId(int transfer_id) {
		Transfer transfer = null;
		String sql = "SELECT * FROM transfers WHERE transfer_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transfer_id);
		while (results.next()) {
			transfer = mapRowToTransfer(results);
		}
		return transfer;
		
	}
	
	private Transfer mapRowToTransfer(SqlRowSet rs) {
		return new Transfer(rs.getInt("transfer_id"), rs.getInt("transfer_type_id"), rs.getInt("transfer_status_id"), 
				rs.getInt("account_from"), rs.getInt("account_to"), rs.getBigDecimal("amount"));
				
	}
	
	@Override
	public Transfer addSendTransfer(Transfer newTransfer) {
		String sql = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		
		int newTransferId = getNextTransferId();
		int transferTypeId = newTransfer.getTransfer_type_id();
		int transferStatusId = newTransfer.getTransfer_status_id();
		String fromUserName = accountDAO.getUsernameFromAccountId(newTransfer.getAccount_from());
		Account fromAccount = accountDAO.getAccountByUserId(userDAO.findIdByUsername(fromUserName));
		String toUserName = accountDAO.getUsernameFromAccountId(newTransfer.getAccount_to());
		Account toAccount = accountDAO.getAccountByUserId(userDAO.findIdByUsername(toUserName));
		
		jdbcTemplate.update(sql, newTransferId, transferTypeId, transferStatusId, fromAccount.getAccountId(), toAccount.getAccountId(), newTransfer.getAmount());
		
		
		return getTransferFromTransferId(newTransferId);
	}
	
	@Override
	public List<Transfer> getPendingTransfersForUser(int currentUserId) {
		String sql = "SELECT * FROM transfers WHERE transfer_status_id = 1 "
				+ "AND (account_from IN (SELECT account_id FROM accounts WHERE account_id = ?) "
				+ "OR account_to IN (SELECT account_id FROM accounts WHERE account_id = ?))";
		List<Transfer> transfers = new ArrayList<>();
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, currentUserId);
		while(results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}
		
		return transfers;
	}
	
	@Override
	public int getNextTransferId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_transfer_id')");
		if(nextIdResult.next()) {
			return nextIdResult.getInt(1);
		} else {
			throw new RuntimeErrorException(null, "Something went wrong while getting an id for the new transfer");
		}
	}
	
	private int getTransferTypeId(String transferType) {
		String sql = "SELECT transfer_type_id FROM transfer_types WHERE transfer_type_desc = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferType);
		if(results.next()) {
			return results.getInt(1);
		} else {
			throw new RuntimeException("Unable to lookup transferType " + transferType);
		}
	}
	
	@Override
	public void updateStatus(Transfer transfer) {
		String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?";
		int transferStatusId = transfer.getTransfer_status_id();
		jdbcTemplate.update(sql, transferStatusId, transfer.getTransfer_id());
	}
	
	private Integer getTransferStatusId(String transferStatus) {
		String sql = "SELECT transfer_status_id FROM transfer_statuses WHERE transfer_status_desc = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferStatus);
		if(results.next()) {
			return results.getInt(1);
		} else {
			throw new RuntimeErrorException(null, "Unable to lookup transferStatus " + transferStatus);
		}
	}

}
