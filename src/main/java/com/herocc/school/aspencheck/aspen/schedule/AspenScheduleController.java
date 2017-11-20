package com.herocc.school.aspencheck.aspen.schedule;

import com.herocc.school.aspencheck.AspenCheck;
import com.herocc.school.aspencheck.District;
import com.herocc.school.aspencheck.ErrorInfo;
import com.herocc.school.aspencheck.JSONReturn;
import com.herocc.school.aspencheck.aspen.AspenWebFetch;
import org.jsoup.Connection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/{district-id}/aspen")
public class AspenScheduleController {
  
  @RequestMapping("schedule")
  public ResponseEntity<JSONReturn> serveSchedule(@PathVariable(value="district-id") String district,
                                  @RequestHeader(value="ASPEN_UNAME", required=false) String u,
                                  @RequestHeader(value="ASPEN_PASS", required=false) String p){
    
    District d = AspenCheck.config.districts.get(district);
    d.refresh();
    
    if (u != null && p != null) return new ResponseEntity<>(new JSONReturn(getSchedule(d.districtName, u, p), new ErrorInfo()), HttpStatus.OK);
    return new ResponseEntity<>(new JSONReturn(d.schedule, new ErrorInfo()), HttpStatus.OK);
  }
  
  public static void refreshSchedule(District d) {
    d.schedule = getSchedule(d.districtName, d.aspenUsername, d.aspenPassword);
  }
  
  public static Schedule getSchedule(String districtName, String username, String password) {
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
