package com.herocc.school.aspencheck;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException {
  
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
