package com.herocc.school.aspencheck;

public class GenericRestController {
  protected long refreshTime = 0 / 1000; // UNIX Timestamp
  
  protected long getNextRefreshTime() {
    return refreshTime + Configs.REFRESH_INTERVAL;
  }
}
