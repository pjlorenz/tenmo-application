package com.techelevator.tenmo.controller;

import java.math.BigDecimal;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthorizationException;
import com.techelevator.tenmo.model.NewTransferDTO;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferAlreadyExistsException;
import com.techelevator.tenmo.model.TransferAuthorization;
import com.techelevator.tenmo.model.TransferUnsuccessfulException;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDoesNotExistException;


@RestController
@RequestMapping("/transfers")
@PreAuthorize("isAuthenticated()")
public class TransferController {
	

	private AccountDAO accountDAO;
	private UserDAO userDAO;
	private TransferDAO transferDAO;

	public TransferController(AccountDAO accountDAO, UserDAO userDAO, TransferDAO transferDAO) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		this.transferDAO = transferDAO;
	}
	
	@RequestMapping(value = "/id", method = RequestMethod.POST)
	public Transfer getTransferFromTransferId(@RequestBody int transferId) {
		Transfer transfer = transferDAO.getTransferById(transferId);
		return transfer;
	}

	
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public Transfer createSendTransfer(@RequestBody NewTransferDTO transferDto) {
		Transfer transfer = buildTransferFromTransferDTO(transferDto);
		transfer = transferDAO.addSendTransfer(transfer);
		transferBucksBetweenAccounts(transfer);
		
		
		return transfer;
	}
	
	@RequestMapping(value = "/id/reject", method = RequestMethod.POST)
	public Transfer rejectSendTransfer(@RequestBody int transferId) {
		Transfer rejectedTransfer = transferDAO.getTransferFromTransferId(transferId);
		rejectedTransfer.setTransfer_status_id(3);
		transferDAO.updateStatus(rejectedTransfer);
		return rejectedTransfer;
	}
	
	  @RequestMapping(value = "/user", method = RequestMethod.POST)
	    public List<Transfer> getArrayOfUsersTransfers(@RequestBody int userId) {
	        try {
	        	return transferDAO.getTransfersForUser(userId); 
	           
	        } catch (UsernameNotFoundException e) {
	            throw new UserDoesNotExistException();
	        }
	    }
	
	@RequestMapping(value = "/{transferId}", method = RequestMethod.PUT)
	public Transfer updateTransferStatus(@PathVariable int transferId, @Valid @RequestBody NewTransferDTO dto, Principal principal) {
		int newStatus = dto.getTransferStatusId();
		Transfer transfer = transferDAO.getTransferFromTransferId(transferId);
		validateAuthorizationToUpdateStatus(principal, transfer);
		if (newStatus == 2) {
			transfer.approve();
		}
		transferDAO.updateStatus(transfer);
		return transfer;
	}
	
	
	
	private Transfer buildTransferFromTransferDTO(NewTransferDTO transferDTO) {
		String  userNameFrom = accountDAO.getUsernameFromAccountId(transferDTO.getSendingAccountId());
		User userFrom = userDAO.findByUsername(userNameFrom);
		
		String userNameTo = accountDAO.getUsernameFromAccountId(transferDTO.getReceivingAccountId());
		User userTo = userDAO.findByUsername(userNameTo);
	
		int transferId = transferDAO.getNextTransferId();
		int transferTypeId = 2;
		int transferStatusId = 2;
		int accountFromId = transferDTO.getSendingAccountId();
		int accountToId = transferDTO.getReceivingAccountId();
		BigDecimal amount = transferDTO.getAmount();

		
		return new Transfer(transferId, transferTypeId, transferStatusId, accountFromId, accountToId, amount);
	}
	
	
	
	private void transferBucksBetweenAccounts(Transfer transfer) {
		String userNameFrom = accountDAO.getUsernameFromAccountId(transfer.getAccount_from());
		int userIdFrom = userDAO.findIdByUsername(userNameFrom);
		Account accountFrom = accountDAO.getAccountByUserId(userIdFrom);
		
		String userNameTo = accountDAO.getUsernameFromAccountId(transfer.getAccount_to());
		int userIdTo = userDAO.findIdByUsername(userNameTo);
		Account accountTo = accountDAO.getAccountByUserId(userIdTo);
		
		accountFrom.transfer(accountTo, transfer.getAmount());
		accountDAO.updateBalance(accountFrom);
		accountDAO.updateBalance(accountTo);
	}

	private void validateAuthorizationToView(Principal principal, Transfer transfer) {
		TransferAuthorization auth = new TransferAuthorization(principal, transfer);
		if(!auth.isAllowedToView()) {
			throw new AuthorizationException();
		}
	}
	
	private void validateAuthorizationToCreate(Principal principal, Transfer transfer) {
		TransferAuthorization auth = new TransferAuthorization(principal, transfer);
		if(!auth.isAllowedToCreate()) {
			throw new AuthorizationException();
		}
	}
	
	private void validateAuthorizationToUpdateStatus(Principal principal, Transfer transfer) {
		TransferAuthorization auth = new TransferAuthorization(principal, transfer);
		if(!auth.isAllowedToApproveOrReject()) {
			throw new AuthorizationException();
		}
	}
	

	

}