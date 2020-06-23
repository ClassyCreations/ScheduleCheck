package com.herocc.school.aspencheck.aspen.student;

import com.herocc.school.aspencheck.ErrorInfo;
import com.herocc.school.aspencheck.JSONReturn;
import com.herocc.school.aspencheck.aspen.AspenWebFetch;
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
public class AspenStudentController {

  @GetMapping("student")
  public ResponseEntity<JSONReturn> serveSchedule(@PathVariable(value="district-id") String districtName,
                                  @RequestHeader(value="ASPEN_UNAME", required=false) String u,
                                  @RequestHeader(value="ASPEN_PASS", required=false) String p) {

    if (u != null && p != null) {
      return new ResponseEntity<>(new JSONReturn(getStudent(districtName, u, p), new ErrorInfo()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new JSONReturn(null, new ErrorInfo("Invalid Credentials", 0, "No username or password given")), HttpStatus.UNAUTHORIZED);
    }
  }

  public static Student getStudent(String districtName, String u, String p) {
    AspenWebFetch aspenWebFetch = new AspenWebFetch(districtName, u, p);
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
