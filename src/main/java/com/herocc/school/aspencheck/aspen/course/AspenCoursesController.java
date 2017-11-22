package com.herocc.school.aspencheck.aspen.course;

import com.herocc.school.aspencheck.AspenCheck;
import com.herocc.school.aspencheck.District;
import com.herocc.school.aspencheck.ErrorInfo;
import com.herocc.school.aspencheck.JSONReturn;
import com.herocc.school.aspencheck.aspen.AspenWebFetch;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/{district-id}/aspen")
public class AspenCoursesController {
  private static AspenWebFetch a;
  
  @RequestMapping("/course")
  public ResponseEntity<JSONReturn> serveSchedule(@PathVariable(value="district-id") String district,
                                                  @RequestHeader(value="ASPEN_UNAME", required=false) String u,
                                                  @RequestHeader(value="ASPEN_PASS", required=false) String p){
    
    District d = AspenCheck.config.districts.get(district);
    
    if (u != null && p != null) {
      return new ResponseEntity<>(new JSONReturn(getCourses(d.districtName, u, p), new ErrorInfo()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new JSONReturn(null, new ErrorInfo("Invalid Credentials", 0, "No username or password given")), HttpStatus.UNAUTHORIZED);
    }
  }
  
  @RequestMapping("/course/{course-id}")
  public ResponseEntity<JSONReturn> serveCourseInfo(@PathVariable(value="district-id") String district,
                                                    @PathVariable(value="course-id") String course,
                                                    @RequestHeader(value="ASPEN_UNAME", required=false) String u,
                                                    @RequestHeader(value="ASPEN_PASS", required=false) String p){
    
    District d = AspenCheck.config.districts.get(district);
  
    if (u != null && p != null) {
      a = new AspenWebFetch(d.districtName, u, p);
      Course c = getCourse(d.districtName, course, u, p).getMoreInformation(a);
      if (c == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new JSONReturn(null, new ErrorInfo("j", 9, "b")));
      return new ResponseEntity<>(new JSONReturn(getCourse(d.districtName, course, u, p), new ErrorInfo()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new JSONReturn(null, new ErrorInfo("Invalid Credentials", 0, "No username or password given")), HttpStatus.UNAUTHORIZED);
    }
  }
  
  public static List<Course> getCourses(String districtName, String username, String password) {
    Connection.Response classListPage = a.getClassListPage();
    List<Course> courses = new ArrayList<>();
    if (classListPage != null) {
      try {
        for (Element classRow : classListPage.parse().body().getElementsByAttributeValueContaining("class", "listCell listRowHeight")) {
          courses.add(new Course(classRow));
        }
      } catch (IOException e) {
        e.printStackTrace();
        AspenCheck.rollbar.error(e, "Error while parsing CourseList of " + districtName + "'s " + username);
      }
    }
    return courses;
  }
  
  public static Course getCourse(String districtName, String courseId, String username, String password) {
    List<Course> enrolledCourses = getCourses(districtName, username, password);
  
    for (Course c : enrolledCourses) {
      if (c.id.equalsIgnoreCase(courseId) || c.code.equalsIgnoreCase(courseId) || c.name.equalsIgnoreCase(courseId))
        return c.getMoreInformation(a);
    }
    return null;
  }
}
