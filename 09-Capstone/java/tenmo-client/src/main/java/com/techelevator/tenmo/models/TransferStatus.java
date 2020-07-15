package com.techelevator.tenmo.models;

public class TransferStatus {

	private int transfer_status_id;
	private String transfer_status_description;
	
	public int getTransfer_status_id() {
		return transfer_status_id;
	}
	public void setTransfer_status_id(int transfer_status_id) {
		this.transfer_status_id = transfer_status_id;
	}
	public String getTransfer_status_description() {
		return transfer_status_description;
	}
	public void setTransfer_status_description(String transfer_status_description) {
		this.transfer_status_description = transfer_status_description;
	}
	
	public static boolean isValid(String transferStatus) {
		boolean valid = false;
		if (transferStatus.equals("Pending")) {
			valid = true;
			return valid;
		} else if (transferStatus.equals("Approved")) {
			valid = true;
			return valid;
		} else if (transferStatus.equals("Rejected")) {
			valid = true;
			return valid;
		}
		return valid;
	}
	
	

}
