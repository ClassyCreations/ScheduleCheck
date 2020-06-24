package com.herocc.school.aspencheck.aspen.schedule;

import com.herocc.school.aspencheck.AspenCheck;
import com.herocc.school.aspencheck.District;
import com.herocc.school.aspencheck.ErrorInfo;
import com.herocc.school.aspencheck.JSONReturn;
import com.herocc.school.aspencheck.aspen.AspenWebFetch;
import io.swagger.v3.oas.annotations.Operation;
import org.jsoup.Connection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/{district-id}/aspen")
public class AspenScheduleController {

  @Operation(description = "Returns schedule information, such as day, block, etc.")
  @GetMapping("schedule")
  public ResponseEntity<JSONReturn> serveSchedule(@PathVariable(value="district-id") String districtName,
                                  @RequestHeader(value="ASPEN_UNAME", required=false) String u,
                                  @RequestHeader(value="ASPEN_PASS", required=false) String p){

    District d = AspenCheck.config.districts.get(districtName);

    if (u != null && p != null) {
      return new ResponseEntity<>(new JSONReturn(getSchedule(districtName, u, p), new ErrorInfo()), HttpStatus.OK);
    } else if (d != null) {
      return new ResponseEntity<>(new JSONReturn(d.schedule, new ErrorInfo()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new JSONReturn(null, new ErrorInfo("No configured district for schedule", 0, "No username or password given, and no cache configured for your district")), HttpStatus.NOT_FOUND);
    }
  }

  public static void refreshSchedule(District d) {
    d.schedule = getSchedule(d);
  }

  public static Schedule getSchedule(District d) {
    AspenWebFetch aspenWebFetch = new AspenWebFetch(d);
    Connection.Response schedulePage = aspenWebFetch.getSchedulePage();
    if (schedulePage != null) {
      try {
        return new Schedule(schedulePage.parse(), d);
      } catch (IOException e) {
        e.printStackTrace();
        AspenCheck.rollbar.error(e, "Error while parsing SchedulePage of " + d.districtName);
      }
    }
    return null;
  }

  private static Schedule getSchedule(String districtName, String username, String password) {
    AspenWebFetch aspenWebFetch = new AspenWebFetch(districtName, username, password);
    Connection.Response schedulePage = aspenWebFetch.getSchedulePage();
    if (schedulePage != null) {
      try {
        return new Schedule(schedulePage.parse());
      } catch (IOException e) {
        e.printStackTrace();
        AspenCheck.rollbar.error(e, "Error while parsing SchedulePage of " + districtName);
      }
    }
    return null;
  }
}
