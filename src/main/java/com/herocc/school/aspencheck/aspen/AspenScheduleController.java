package com.herocc.school.aspencheck.aspen;

import com.herocc.school.aspencheck.AspenCheck;
import com.herocc.school.aspencheck.GenericRestController;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.logging.Level;

@RestController
public class AspenScheduleController extends GenericRestController {
  private Schedule schedule;
  
  @RequestMapping("/aspen/schedule")
  public Schedule getSchedule(@RequestHeader(value="ASPEN_UNAME", required=false) String u,
                              @RequestHeader(value="ASPEN_PASS", required=false) String p){
    
    final String username = (u == null ? AspenCheck.username : u);
    final String password = (p == null ? AspenCheck.password : p);
    
    if (System.currentTimeMillis() / 1000 > getNextRefreshTime()) {
      AspenCheck.log.log(Level.FINE, "Refreshing Aspen Schedule, " + System.currentTimeMillis() / 1000 + " > " + getNextRefreshTime());
      new Thread(() -> refreshSchedule(username, password)).start();
    }
    return schedule;
  }
  
  @Cacheable("publicSchedule")
  @CacheEvict(value = "publicSchedule", allEntries=true)
  public Schedule refreshSchedule(String username, String password) {
    AspenWebFetch aspenWebFetch = new AspenWebFetch(username, password);
    if (aspenWebFetch.schedulePage != null) {
      try {
        refreshTime = System.currentTimeMillis() / 1000;
        schedule = new Schedule(aspenWebFetch.schedulePage.parse());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return schedule;
  }
}
