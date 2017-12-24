package com.herocc.school.aspencheck.aspen.student;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.herocc.school.aspencheck.AspenCheck;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class Student {
  private Document studentPage;
  
  @JsonUnwrapped public Map<String, String> info;
  
  public Student(Document d) {
    if (d == null) return;
    this.studentPage = d;
    
    info = getListedProperties();
  }
  
  private Map<String, String> getListedProperties() {
    Map<String, String> values = new HashMap<>();
    Elements elements = studentPage.body().getElementsByAttributeValueContaining("id", "Property|");
    
    for (Element e : elements) {
      values.put(AspenCheck.textToCammelCase(e.getElementsByAttributeValueContaining("class", "detailProperty").text(), true), e.getElementsByAttributeValueContaining("class", "detailValue").text());
    }
    return values;
  }
}
