package com.herocc.school.aspencheck;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
public class JSONReturn {
  public ResponseEntity data;
  public ErrorInfo errors;
}
