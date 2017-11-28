package com.herocc.school.aspencheck.aspen.course.assignment;

import com.herocc.school.aspencheck.AspenCheck;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Assignment {
  
  public String id;
  public String name;
  public String credit;
  public String dateAssigned;
  public String dateDue;
  public String feedback;
  
  
  public Assignment(Element e){
    Elements tdTags = e.getElementsByTag("td");
    id = tdTags.get(1).id().trim();
    name = tdTags.get(1).text().trim();
    dateAssigned = tdTags.get(2).text().trim();
    dateDue = tdTags.get(3).text().trim();
    
    if (!AspenCheck.isNullOrEmpty(tdTags.get(4).getElementsByAttributeValueContaining("class", "percentFieldInlineLabel").text().trim())) {
      credit = tdTags.get(4).getElementsByAttributeValueContaining("class", "percentFieldInlineLabel").text().trim();
    } else {
      credit = tdTags.get(4).text().trim();
    }
    
    // feedback = tdTags.get(5).text().trim(); // TODO when empty, returns percentCredit
  }
  
}
