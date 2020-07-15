package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import com.techelevator.tenmo.model.NewTransferDTO;
import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {
	
	boolean createSendTransfer(NewTransferDTO transferDTO);
	
	Transfer getTransferFromTransferId(int transfer_id);
	
	Transfer getTransferById(int transferId);
	
	Transfer addSendTransfer(Transfer newTransfer);
	
	List<Transfer> getTransfersForUser(int userId);
	
	List<Transfer> findAll();
	
	List<Transfer> getPendingTransfersForUser(int currentUserId);
	
	void updateStatus(Transfer transfer);
	
	int getNextTransferId();
}
