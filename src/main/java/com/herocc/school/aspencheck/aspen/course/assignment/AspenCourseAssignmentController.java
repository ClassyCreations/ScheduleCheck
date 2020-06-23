package com.herocc.school.aspencheck.aspen.course.assignment;

import com.herocc.school.aspencheck.ErrorInfo;
import com.herocc.school.aspencheck.JSONReturn;
import com.herocc.school.aspencheck.aspen.AspenWebFetch;
import com.herocc.school.aspencheck.aspen.course.Course;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.herocc.school.aspencheck.aspen.course.AspenCoursesController.getCourse;

@CrossOrigin
@RestController
@RequestMapping("/{district-id}/aspen")
public class AspenCourseAssignmentController {

  @GetMapping("/course/{course-id}/assignment")
  public ResponseEntity<JSONReturn> serveAssignmentList(@PathVariable(value="district-id") String districtName,
                                                        @PathVariable(value="course-id") String course,
                                                        @RequestHeader(value="ASPEN_UNAME", required=false) String u,
                                                        @RequestHeader(value="ASPEN_PASS", required=false) String p){

    if (u != null && p != null) {
      AspenWebFetch aspenWebFetch = new AspenWebFetch(districtName, u, p);
      List<Assignment> a = getAssignmentList(aspenWebFetch, getCourse(aspenWebFetch, course, false));
      if (a == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new JSONReturn(null, new ErrorInfo("j", 9, "b")));
      return new ResponseEntity<>(new JSONReturn(a, new ErrorInfo()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new JSONReturn(null, new ErrorInfo("Invalid Credentials", 0, "No username or password given")), HttpStatus.UNAUTHORIZED);
    }
  }

  public static List<Assignment> getAssignmentList(AspenWebFetch a, Course course){
    List<Assignment> assignments = new ArrayList<>();
    Connection.Response assignmentsPage = a.getCourseAssignmentsPage(course.id);

    if (assignmentsPage != null) {
      try {
        for (Element assignmentRow : assignmentsPage.parse().body().getElementsByAttributeValueContaining("class", "listCell listRowHeight")) {
          if (!assignmentRow.text().trim().contains("No matching records")) assignments.add(new Assignment(assignmentRow));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return assignments;
  }
}
