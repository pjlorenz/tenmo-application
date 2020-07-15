package com.techelevator.tenmo.controller;


import java.util.List;

import com.techelevator.tenmo.dao.AccountDAO;



import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import com.techelevator.tenmo.model.UserDoesNotExistException;


@RestController
@PreAuthorize("isAuthenticated()")
public class UserController {
	

	private AccountDAO accountDAO;
	private UserDAO userDAO;

	public UserController(AccountDAO accountDAO, UserDAO userDAO) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
	}
	

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> getUsersList() {
        try {
        	return userDAO.listAllUsers(); 
           
        } catch (UsernameNotFoundException e) {
            throw new UserDoesNotExistException();
        }
    }
    
    @RequestMapping(value = "/users/username/id", method = RequestMethod.POST)
    public String getUsernameFromId(@RequestBody int userId) {
        try {
        	return userDAO.getUsernameFromId(userId);
           
        } catch (UsernameNotFoundException e) {
            throw new UserDoesNotExistException();
        }
    }


}