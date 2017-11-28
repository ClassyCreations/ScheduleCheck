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
  
  @RequestMapping("/course")
  public ResponseEntity<JSONReturn> serveSchedule(@PathVariable(value="district-id") String district,
                                                  @RequestParam(value="moreData", defaultValue="false") String moreData,
                                                  @RequestHeader(value="ASPEN_UNAME", required=false) String u,
                                                  @RequestHeader(value="ASPEN_PASS", required=false) String p){
    
    District d = AspenCheck.config.districts.get(district);
    
    if (u != null && p != null) {
      return new ResponseEntity<>(new JSONReturn(getCourses(new AspenWebFetch(d.districtName, u, p), moreData.equals("true")), new ErrorInfo()), HttpStatus.OK);
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
      AspenWebFetch a = new AspenWebFetch(d.districtName, u, p);
      Course c = getCourse(a, course).getMoreInformation(a);
      if (c == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new JSONReturn(null, new ErrorInfo("Course not Found", 404, "The course you tried to fetch doesn't exist or was inaccessible")));
      return new ResponseEntity<>(new JSONReturn(getCourse(a, course), new ErrorInfo()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new JSONReturn(null, new ErrorInfo("Invalid Credentials", 0, "No username or password given")), HttpStatus.UNAUTHORIZED);
    }
  }
  
  public static List<Course> getCourses(AspenWebFetch a) { return getCourses(a, false); }
  
  public static List<Course> getCourses(AspenWebFetch a, boolean moreData) {
    Connection.Response classListPage = a.getCourseListPage();
    List<Course> courses = new ArrayList<>();
    if (classListPage != null) {
      try {
        for (Element classRow : classListPage.parse().body().getElementsByAttributeValueContaining("class", "listCell listRowHeight")) {
          Course c = new Course(classRow);
          c = moreData ? c.getMoreInformation(a) : c;
          courses.add(c);
        }
      } catch (IOException e) {
        e.printStackTrace();
        AspenCheck.rollbar.error(e, "Error while parsing CourseList of " + a.districtName + "'s " + a.username);
      }
    }
    return courses;
  }
  
  public static Course getCourse(AspenWebFetch a, String courseId, boolean moreData) {
    List<Course> enrolledCourses = getCourses(a);
    
    for (Course c : enrolledCourses) {
      if (c.id.equalsIgnoreCase(courseId) || c.code.equalsIgnoreCase(courseId) || c.name.equalsIgnoreCase(courseId))
        return moreData ? c.getMoreInformation(a) : c;
    }
    return null;
  }
  
  public static Course getCourse(AspenWebFetch a, String courseId) {
    return getCourse(a, courseId, true);
  }
}
