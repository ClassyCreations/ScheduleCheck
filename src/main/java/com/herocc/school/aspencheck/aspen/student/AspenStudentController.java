package com.herocc.school.aspencheck.aspen.student;

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
public class AspenStudentController {
  
  @RequestMapping("student")
  public ResponseEntity<JSONReturn> serveSchedule(@PathVariable(value="district-id") String district,
                                  @RequestHeader(value="ASPEN_UNAME", required=false) String u,
                                  @RequestHeader(value="ASPEN_PASS", required=false) String p) {
    
    District d = AspenCheck.config.districts.get(district);
  
    if (u != null && p != null) {
      return new ResponseEntity<>(new JSONReturn(getStudent(d, u, p), new ErrorInfo()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new JSONReturn(null, new ErrorInfo("Invalid Credentials", 0, "No username or password given")), HttpStatus.UNAUTHORIZED);
    }
  }
  
  public static Student getStudent(District d, String u, String p) {
    AspenWebFetch aspenWebFetch = new AspenWebFetch(d.districtName, u, p);
    Connection.Response studentPage = aspenWebFetch.getStudentInfoPage();
    if (studentPage != null) {
      try {
        return new Student(studentPage.parse());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
}
