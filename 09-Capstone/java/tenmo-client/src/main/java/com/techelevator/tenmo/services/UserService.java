package com.techelevator.tenmo.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.techelevator.tenmo.models.User;


public class UserService {

	private String BASE_SERVICE_URL;
    private RestTemplate restTemplate = new RestTemplate();

	public UserService(String url) {
		// TODO Auto-generated constructor stub
		this.BASE_SERVICE_URL = url;
	}
	
	public User[] getArrayOfUsers(String authToken) throws AuthenticationServiceException {
        HttpEntity<?> entity = new HttpEntity<>(authHeaders(authToken));
        ResponseEntity<User[]> response = restTemplate.exchange((BASE_SERVICE_URL + "users"), HttpMethod.GET, entity, User[].class);
        return response.getBody();               
    }
	
	private HttpHeaders authHeaders(String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		return headers;
	}
	
	public String getUsernameFromId(int userId, String authToken) {
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(authToken);
    	HttpEntity<Integer> entity = new HttpEntity<>(userId, headers);
		ResponseEntity<String> username = restTemplate.exchange((BASE_SERVICE_URL + "users/username/id"), HttpMethod.POST, entity, String.class);
		return username.getBody();
	}

}
