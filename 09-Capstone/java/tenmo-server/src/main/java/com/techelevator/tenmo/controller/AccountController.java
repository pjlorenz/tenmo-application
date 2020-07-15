package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.security.Principal;

import javax.validation.Valid;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDoesNotExistException;


@RestController
@RequestMapping("/account")
@PreAuthorize("isAuthenticated()")
public class AccountController {
	

	private AccountDAO accountDAO;
	private UserDAO userDAO;
	private TransferDAO transferDAO;

	public AccountController(AccountDAO accountDAO, UserDAO userDAO, TransferDAO transferDAO) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		this.transferDAO = transferDAO;
	}
	

    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    public BigDecimal getAccountBalance(Principal principal) {
        try {
           return accountDAO.getAccountFromUsername(principal.getName()).getAccountBalance();
        } catch (UsernameNotFoundException e) {
            throw new UserDoesNotExistException();
        }
    }
    
    @RequestMapping(value = "/balance", method = RequestMethod.PUT)
    public void updateBalance(Account account) {
        try {
           accountDAO.updateBalance(account);
        } catch (UsernameNotFoundException e) {
            throw new UserDoesNotExistException();
        }
    }
    
    @RequestMapping(value = "/id/{username}", method = RequestMethod.GET)
    public Integer getAccountIdFromUserName (@PathVariable String username) {
        try {
           return accountDAO.getAccountIdFromUsername(username);
        } catch (UsernameNotFoundException e) {
            throw new UserDoesNotExistException();
        }
    }
    
    @RequestMapping(value = "/username", method = RequestMethod.POST)
    public Account getAccountFromUsername (@RequestBody String username) {
        try {
           return accountDAO.getAccountFromUsername(username);
        } catch (UsernameNotFoundException e) {
            throw new UserDoesNotExistException();
        }
    }
    
    @RequestMapping(value = "/id", method = RequestMethod.POST)
    public Account getAccountFromAccountId (@RequestBody int accountId) {
        try {
           return accountDAO.getAccountFromAccountId(accountId);
        } catch (UsernameNotFoundException e) {
            throw new UserDoesNotExistException();
        }
    }

}
