package com.herocc.school.aspencheck;

import com.herocc.school.aspencheck.calendar.Event;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CSVAnnouncementsTest {
  private CSVParse csv = new CSVParse(new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("MorningAnnouncements.csv").toURI()))));
  private Event event = csv.getEvents(false).get(2);
  
  public CSVAnnouncementsTest() throws IOException, URISyntaxException {}
  
  @Test
  public void expectedAmount() {
    assert csv.getEvents(false).size() == 3;
  }
  
  @Test
  public void expectedEventTitle() {
    assert "MHS".equals(event.getTitle());
  }
  
  @Test
  public void expectedEventDescription() {
    assert "Cool thing in the High School on 8/30/2017 at 7:15:00 AM, contact Mr. Redacted for more information"
        .equals(event.getDescription());
       assert true;
  }
}
