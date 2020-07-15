package com.techelevator.tenmo.services;

import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.TransferDTO;
import com.techelevator.tenmo.models.TransferStatus;
import com.techelevator.tenmo.models.User;


public class TransferService {

	private String BASE_TRANSFER_URL;
    private RestTemplate restTemplate = new RestTemplate();
    public static String AUTH_TOKEN = "";
    
    public TransferService(String url) {
		this.BASE_TRANSFER_URL = url;
	}
    
    public Transfer createSendTransfer(TransferDTO transferDto, String authToken) {
    	HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(authToken);
    	HttpEntity<TransferDTO> entity = new HttpEntity<>(transferDto, headers);
    	ResponseEntity<Transfer> response = restTemplate.exchange(BASE_TRANSFER_URL + "transfers", HttpMethod.POST, entity, Transfer.class);
    	return response.getBody();
    }
    
    public Transfer[] getArrayOfUsersTransfers(int userId, String authToken) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(authToken);
    	HttpEntity<Integer> entity = new HttpEntity<>(userId, headers);
        ResponseEntity<Transfer[]> response = restTemplate.exchange((BASE_TRANSFER_URL + "transfers/user"), HttpMethod.POST, entity, Transfer[].class);
        return response.getBody();  
    }
    
    public Transfer getTransferFromTransferId(int transferId, String authToken) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(authToken);
    	HttpEntity<Integer> entity = new HttpEntity<>(transferId, headers);
        ResponseEntity<Transfer> response = restTemplate.exchange((BASE_TRANSFER_URL + "transfers/id"), HttpMethod.POST, entity, Transfer.class);
        return response.getBody(); 
    }
    
    public Transfer approvePendingTransfer(Integer transferId, String authToken) {
    	TransferStatusUpdateDTO dto = new TransferStatusUpdateDTO("Approved");
    	HttpEntity<TransferStatusUpdateDTO> entity = new HttpEntity<>(dto, authHeaders(authToken));
    	ResponseEntity<Transfer> response = restTemplate.exchange(BASE_TRANSFER_URL, HttpMethod.PUT, entity, Transfer.class);
    	return response.getBody();
    }
    
    public Transfer rejectPendingTransfer(Integer transferId, String authToken) {
    	TransferStatusUpdateDTO dto = new TransferStatusUpdateDTO("Rejected");
    	HttpEntity<TransferStatusUpdateDTO> entity = new HttpEntity<>(dto, authHeaders(authToken));
    	ResponseEntity<Transfer> response = restTemplate.exchange(BASE_TRANSFER_URL, HttpMethod.PUT, entity, Transfer.class);
    	return response.getBody();
    }
    
    public Transfer rejectSendTransfer(Integer transferId, String authToken) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(authToken);
    	HttpEntity<Integer> entity = new HttpEntity<>(transferId, headers);
       ResponseEntity<Transfer> response = restTemplate.exchange((BASE_TRANSFER_URL + "transfers/id/reject"), HttpMethod.POST, entity, Transfer.class);
       return response.getBody();
    }
    
    private HttpHeaders authHeaders(String authToken) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setBearerAuth(authToken);
    	return headers;
    }
    
    private static class TransferStatusUpdateDTO {
    	private String transferStatus;
    	public TransferStatusUpdateDTO(String transferStatus) {
    		if(TransferStatus.isValid(transferStatus)) {
    			this.transferStatus = transferStatus;
    		} else {
				throw new IllegalArgumentException("Invlaid transferStatus: " + transferStatus);
			}
    	}
    }
    
    private String createTransferExceptionMessage(RestClientResponseException ex) {
		String message = null;
		if (ex.getRawStatusCode() == 400 && ex.getResponseBodyAsString().length() == 0) {
		    message = ex.getRawStatusCode() + " : {\"timestamp\":\"" + LocalDateTime.now() + "+00:00\",\"status\":400,\"error\":\"Invalid credentials\",\"message\":\"Registration failed: Invalid username or password\",\"path\":\"/register\"}";
		}
		else {
		    message = ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString();
		}
		return message;
	}
 
}
