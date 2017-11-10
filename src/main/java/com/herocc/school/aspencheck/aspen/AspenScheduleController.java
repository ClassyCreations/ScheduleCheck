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
@RequestMapping("aspen")
public class AspenScheduleController extends GenericRestController {
  private Schedule schedule;
  
  @RequestMapping("schedule")
  public Schedule restScheduleHandler(@RequestHeader(value="ASPEN_UNAME", required=false) String u,
                                      @RequestHeader(value="ASPEN_PASS", required=false) String p){
  
    if (u != null && p != null) return getSchedule(u, p);
    
    if (System.currentTimeMillis() / 1000 > getNextRefreshTime()) {
      AspenCheck.log.log(Level.INFO, "Refreshing Aspen Schedule, " + System.currentTimeMillis() / 1000 + " > " + getNextRefreshTime());
      new Thread(this::refreshSchedule).start();
    }
    return schedule;
  }
  
  @Cacheable("publicSchedule")
  @CacheEvict(value = "publicSchedule", allEntries=true)
  public Schedule refreshSchedule() {
    schedule = getSchedule(AspenCheck.username, AspenCheck.password);
    refreshTime = System.currentTimeMillis() / 1000;
    return schedule;
  }
  
  public Schedule getSchedule(String username, String password) {
    AspenWebFetch aspenWebFetch = new AspenWebFetch(username, password);
    if (aspenWebFetch.schedulePage != null) {
      try {
        return schedule = new Schedule(aspenWebFetch.schedulePage.parse());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
  
  @Override
  protected void refresh() {
    refreshSchedule();
  }
}
