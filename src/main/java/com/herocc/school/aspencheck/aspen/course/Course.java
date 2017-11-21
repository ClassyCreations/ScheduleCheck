package com.herocc.school.aspencheck.aspen.course;

import org.jsoup.nodes.Element;

public class Course {
  
  public String id;
  public String name;
  public String code;
  public String term;
  public String teacher;
  public String room;
  public String termGrade;
  
  public Course(Element element) {
    id = element.getElementsByTag("td").get(1).id().trim();
    name = element.getElementsByTag("td").get(1).text().trim(); // Also possibly td[3]
    code = element.getElementsByTag("td").get(2).text().trim();
    term = element.getElementsByTag("td").get(4).text().trim();
    teacher = element.getElementsByTag("td").get(5).text().trim();
    room = element.getElementsByTag("td").get(6).text().trim();
    termGrade = element.getElementsByTag("td").get(7).text().trim(); // TODO Map<> of all 4 quarter grades
  }
}
