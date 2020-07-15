package com.techelevator.tenmo.model;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.lang.NonNull;

public class NewTransferDTO {


	@NotNull
    private int receivingAccountId;
	@NotNull
    private int sendingAccountId;
	@DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal amount;
	@NotEmpty
	private int transferTypeId;
	@NotNull
	private int transferStatusId;
	

    public NewTransferDTO(int receivingAccountId, int sendingAccountId, BigDecimal amount, int transferTypeId, int transferStatusId) {
		this.receivingAccountId = receivingAccountId;
		this.sendingAccountId = sendingAccountId;
		this.amount = amount;
		this.transferTypeId = transferTypeId;
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
	
	public int getTransferTypeId() {
		return transferTypeId;
	}
	
	public void setTransferTypeId(int transferTypeId) {
		this.transferTypeId = transferTypeId;
	}
	
	public int getTransferStatusId() {
		return transferStatusId;
	}
	
	public void setTransferStatusId(int transferStatusId) {
		this.transferStatusId = transferStatusId;
	}
	
	
	

}