package com.techelevator.tenmo.models;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import org.springframework.lang.NonNull;

public class TransferDTO {



	@NonNull
    private int receivingAccountId;
	@Nonnull
    private int sendingAccountId;
	@NonNull
    private BigDecimal amount;
	@Nonnull
	private int transferType;
	@Nonnull
	private int transferStatusId;

    public TransferDTO(int receivingAccountId, int sendingAccountId, BigDecimal amount, int transferType, int transferStatusId) {
		this.receivingAccountId = receivingAccountId;
		this.sendingAccountId = sendingAccountId;
		this.amount = amount;
		this.transferType = transferType;
		this.transferStatusId = transferStatusId;
	}
    
	public int getReceivingAccountId() {
        return receivingAccountId;
    }

    public void setReceivingAccountId(int receivingAccountId) {
        this.receivingAccountId = receivingAccountId;
    }

    public int getSendingAccountId() {
        return sendingAccountId;
    }

    public void setSendingAccountId(int sendingAccountId) {
        this.sendingAccountId = sendingAccountId;
    }

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }
    
    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }
	
	
}


