package com.techelevator.tenmo.models;

public class AccountCredentials {

    private int userId;

    public AccountCredentials(int userId) {
		this.userId = userId;
	}

//	public int getAccountId() {
//        return accountId;
//    }
//
//    public void setAccountId(int accountId) {
//        this.accountId = accountId;
//    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
