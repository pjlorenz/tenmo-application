package com.techelevator.tenmo.services;

import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;

import java.math.BigDecimal;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;


public class AccountService {
	
	public static String AUTH_TOKEN = "";
	
	private String BASE_SERVICE_URL;
    private final RestTemplate restTemplate = new RestTemplate();

	public AccountService(String url) {
		// TODO Auto-generated constructor stub
		this.BASE_SERVICE_URL = url;
	}
	
	public BigDecimal getAccountBalance(String authToken) throws AuthenticationServiceException {
        HttpEntity<?> entity = new HttpEntity<>(authHeaders(authToken));
        ResponseEntity<BigDecimal> response = restTemplate.exchange((BASE_SERVICE_URL + "account/balance"), HttpMethod.GET, entity, BigDecimal.class);
        return response.getBody();                                 
    }
	
	public void updateBalance(Account account, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(authToken);
		HttpEntity<Account> entity = new HttpEntity<Account>(account, headers);
		restTemplate.exchange(BASE_SERVICE_URL + "account/balance", HttpMethod.PUT, entity, BigDecimal.class);
	}
	
	public Integer getAccountIdFromUserName(String username, String authToken) {
			HttpEntity<?> entity = new HttpEntity<>(authHeaders(authToken));
	        return restTemplate.exchange(BASE_SERVICE_URL + "account/id/" + username, HttpMethod.GET, entity, Integer.class).getBody();
	}
	
	public Account getAccountFromAccountId(Integer accountId, String authToken) {
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(authToken);
    	HttpEntity<Integer> entity = new HttpEntity<>(accountId, headers);
        ResponseEntity<Account> response = restTemplate.exchange((BASE_SERVICE_URL + "account/id"), HttpMethod.POST, entity, Account.class);
        return response.getBody(); 	
        }
	
	public Account getAccountFromUsername(String username, String authToken) {
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(authToken);
    	HttpEntity<String> entity = new HttpEntity<>(username, headers);
        ResponseEntity<Account> response = restTemplate.exchange((BASE_SERVICE_URL + "account/username"), HttpMethod.POST, entity, Account.class);
        return response.getBody(); 		
        }
	
	private HttpHeaders authHeaders(String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		return headers;
	}

}
