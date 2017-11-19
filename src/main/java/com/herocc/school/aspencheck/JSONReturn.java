package com.herocc.school.aspencheck;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JSONReturn extends TimestampedObject {
  public Object data;
  public ErrorInfo errors;
}
