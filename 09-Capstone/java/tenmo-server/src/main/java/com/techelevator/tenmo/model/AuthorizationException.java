package com.techelevator.tenmo.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus( value = HttpStatus.BAD_REQUEST, reason = "Not allowed to view/create/approve or reject.")
public class AuthorizationException extends RuntimeException {

}