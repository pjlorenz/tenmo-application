package com.techelevator.tenmo.models;

import org.springframework.lang.NonNull;

public class User {

	@NonNull
	private Integer id;
	@NonNull
	private String username;

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	

}
