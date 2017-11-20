package com.herocc.school.aspencheck.aspen.course;

import org.jsoup.nodes.Element;

public class Course {
  
  public Course(Element element) {
    final String id = element.getElementsByTag("td").get(1).id().trim();
    final String name = element.getElementsByTag("td").get(1).text().trim(); // Also possibly td[3]
    final String code = element.getElementsByTag("td").get(2).text().trim();
    final String term = element.getElementsByTag("td").get(4).text().trim();
    final String teacher = element.getElementsByTag("td").get(5).text().trim();
    final String room = element.getElementsByTag("td").get(6).text().trim();
    final String termGrade = element.getElementsByTag("td").get(7).text().trim(); // TODO Map<> of all 4 quarter grades
  }
}
