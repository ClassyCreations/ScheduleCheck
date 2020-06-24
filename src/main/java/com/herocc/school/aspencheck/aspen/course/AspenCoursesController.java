package com.herocc.school.aspencheck.aspen.course;

import com.herocc.school.aspencheck.AspenCheck;
import com.herocc.school.aspencheck.ErrorInfo;
import com.herocc.school.aspencheck.JSONReturn;
import com.herocc.school.aspencheck.aspen.AspenWebFetch;
import io.swagger.v3.oas.annotations.Operation;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/{district-id}/aspen")
public class AspenCoursesController {

  @Operation(description = "Index of enrolled courses, their grades, teachers, etc")
  @GetMapping("/course")
  public ResponseEntity<JSONReturn> serveSchedule(@PathVariable(value="district-id") String districtName,
                                                  @RequestParam(value="moreData", defaultValue="false") String moreData,
                                                  @RequestHeader(value="ASPEN_UNAME") String u,
                                                  @RequestHeader(value="ASPEN_PASS") String p){

    if (u != null && p != null) {
      return new ResponseEntity<>(new JSONReturn(getCourses(new AspenWebFetch(districtName, u, p), moreData.equals("true")), new ErrorInfo()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new JSONReturn(null, new ErrorInfo("Invalid Credentials", 0, "No username or password given")), HttpStatus.UNAUTHORIZED);
    }
  }

  @GetMapping("/course/{course-id}")
  public ResponseEntity<JSONReturn> serveCourseInfo(@PathVariable(value="district-id") String districtName,
                                                    @PathVariable(value="course-id") String course,
                                                    @RequestHeader(value="ASPEN_UNAME") String u,
                                                    @RequestHeader(value="ASPEN_PASS") String p){

    if (u != null && p != null) {
      AspenWebFetch a = new AspenWebFetch(districtName, u, p);
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
        AspenCheck.rollbar.error(e, "Error while parsing CourseList of user from " + a.districtName);
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
