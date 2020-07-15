package com.techelevator.tenmo.model;

import java.security.Principal;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;

public class TransferAuthorization {

	private Principal principal;
	private Transfer transfer;
	private AccountDAO accountDAO;
	private UserDAO userDAO;
	private TransferDAO transferDAO;
	
	public TransferAuthorization(Principal principal, Transfer transfer) {
		this.principal = principal;
		this.transfer = transfer;
	}
	
	
	//explained at 11:10 zoom lecture 2;; this was originally in transfercontroller
	public boolean isAllowedToView() {
		return principalUsername().equals(fromUsername()) ||
				principalUsername().equals(toUsername());
	}
	
	public boolean isAllowedToCreate() {
		boolean isAllowed = false;
		if(transfer.isRequestType()) {
			isAllowed = principalUsername().equals(toUsername());
		} else if (transfer.isSendType()) {
		isAllowed = principalUsername().equals(fromUsername());
		}
		return isAllowed;
	}
	
	public boolean isAllowedToApproveOrReject() {
		return principalUsername().equals(fromUsername());
	}
	
	private String toUsername() {
		int accountIdTo = transfer.getAccount_to();
		String userNameTo = accountDAO.getUsernameFromAccountId(accountIdTo);
		return userNameTo;
	}
	
	private String fromUsername() {
		int accountIdFrom = transfer.getAccount_from();
		String userNameFrom = accountDAO.getUsernameFromAccountId(accountIdFrom);
		return userNameFrom;
	}
	
	private String principalUsername() {
		return principal.getName();
	}

}
