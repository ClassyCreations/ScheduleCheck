package com.herocc.school.aspencheck;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Returns a generic data object
 *
 * @param String title
 */
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo {
  //title: General info about error
  public String title;
  //id: unique id for
  public int id;
  //detail: More specific information about the error.
  public String details;
}
