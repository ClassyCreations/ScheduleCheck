package com.herocc.school.aspencheck.aspen.course;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.herocc.school.aspencheck.aspen.AspenWebFetch;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Course {
  private Element classInfoPage;
  
  public String id;
  public String name;
  public String code;
  public String term;
  public String teacher;
  public String room;
  public String currentTermGrade;
  
  public Map<String, String> postedGrades;
  
  public Course(Element classListRow) {
    id = classListRow.getElementsByTag("td").get(1).id().trim();
    name = classListRow.getElementsByTag("td").get(1).text().trim(); // Also possibly td[3]
    code = classListRow.getElementsByTag("td").get(2).text().trim();
    term = classListRow.getElementsByTag("td").get(4).text().trim();
    teacher = classListRow.getElementsByTag("td").get(5).text().trim();
    room = classListRow.getElementsByTag("td").get(6).text().trim();
    currentTermGrade = classListRow.getElementsByTag("td").get(7).text().trim();
  }
  
  public Course(Element element, Element classInfoPage) {
    this(element);
    this.classInfoPage = classInfoPage;
    
    useMoreInformation();
  }
  
  public Course getMoreInformation(AspenWebFetch webFetch) {
    try {
      this.classInfoPage = webFetch.getClassInfoPage(id).parse().body();
    } catch (IOException e) {
      e.printStackTrace();
    }
    useMoreInformation();
    return this;
  }
  
  private void useMoreInformation() {
    this.postedGrades = getTermGrades();
  }
  
  private Map<String, String> getTermGrades() {
    Map<String, String> termGrades = new HashMap<>();
    Elements e = classInfoPage.getElementsByAttributeValueContaining("class", "listHeaderText inputGridCellActive listCell").get(0).getElementsByTag("td");
    for (int i = e.size() - 1; i >= 1; i--) { // All elements except for the first one (0)
       termGrades.put(String.valueOf(i), e.get(i).text().trim());
    }
    return termGrades;
  }
}
