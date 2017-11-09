package com.herocc.school.aspencheck.aspen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Schedule {
  private Document schedPage;
  
  public int day;
  @JsonIgnore public String currentClass;
  public boolean classInSession;
  public String block;
  public String advisoryBlock;
  public int blockOfDay;
  public ArrayList<String> blockOrder;
  @Getter public long asOf = System.currentTimeMillis() / 1000;
  
  public Schedule(Document schedPage){
    this.schedPage = schedPage;
    this.day = getDay();
    this.currentClass = getCurrentClass();
    this.classInSession = isClassInSession();
    this.block = getBlock();
    this.advisoryBlock = getAdvisoryBlock();
    this.blockOfDay = getBlockOfDay();
    this.blockOrder = getDaySchedule();
  }
  
  final int blocksInDay = 6;
  
  private int getDay() {
    Elements matching = schedPage.body().getElementsByAttributeValueContaining("style", "border: solid 1px red;");
    try {
      matching.first().text();
    } catch (NullPointerException e) {
      return 0;
    }
    Matcher m = Pattern.compile("\\d+").matcher(matching.first().text());
    
    if (m.find()) {
      String thing = m.group(0);
      int dayNumber = Integer.parseInt(thing);
      if (dayNumber >= 1 && dayNumber <= 7) {
        return dayNumber;
      }
    }
    return 0;
  }
  
  private String getCurrentClass(){
    Elements matching = schedPage.body().getElementsByAttributeValueContaining("style", "border: solid 1px red;");
    try {
      return matching.get(1).text(); // Get second element with red border (first is day)
    } catch (IndexOutOfBoundsException e){
      return null;
    }
  }
  
  private String getBlock(){
    String currentClass = this.getCurrentClass();
    if (currentClass == null) return "Z";
    String gotBlock = currentClass.replaceAll("\\d","");
    gotBlock = gotBlock.substring(gotBlock.length() - 1);
    if (Pattern.matches("[A-G]", gotBlock)) return gotBlock;
    return "Z";
  }
  
  private int getBlockOfDay(){
    try {
      return Integer.valueOf(String.valueOf(schedPage.body().getElementsByAttributeValueContaining("style", "border: solid 1px red;")
              .get(1).parent().getElementsByAttributeValueContaining("width", "5%").get(0).text().charAt(0)));
    } catch (IndexOutOfBoundsException e){
      return 6;
    }
  }
  
  private ArrayList<String> getDaySchedule(){
    ArrayList<String> blocks = new ArrayList<>();
    if (day == 0) return blocks;
    
    Elements trs = schedPage.body().getElementsByAttributeValueContaining("class", "listHeader headerLabelBackground")
            .first().siblingElements();
    for (int i = 0; i < Math.min(trs.size(), blocksInDay); i++) { // 6 is number of blocks in the day
      Element tr = trs.get(i);
      String text = tr.children().get(day).getElementsByAttributeValueContaining("style", "font-weight: bold").text();
      if (!text.isEmpty()) blocks.add(String.valueOf(text.charAt(0)));
    }
    return blocks;
  }
  
  private String getAdvisoryBlock() {
    String advisoryBlock = "Z"; // Return Z if there is no advisory
    
    Elements trs = schedPage.body().getElementsByAttributeValueContaining("class", "listHeader headerLabelBackground")
            .first().siblingElements();
    if (trs.size() <= blocksInDay) return advisoryBlock;
    String text = trs.get(blocksInDay).children().get(day).getElementsByAttributeValueContaining("style", "font-weight: bold").text();
    if (!text.isEmpty()) advisoryBlock = String.valueOf(text.charAt(0));
    // TODO for now this returns an (unknown magic) number, try to decipher what it means
    return advisoryBlock;
  }
  
  private boolean isClassInSession() {
    return currentClass != null;
  }
}
