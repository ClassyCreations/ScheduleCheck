package com.herocc.school.aspencheck;

import org.springframework.scheduling.annotation.Async;

public abstract class GenericRestController {
  protected long lastRefreshTimestamp = 0 / 1000; // UNIX Timestamp
  
  public GenericRestController() { refresh(); }
  
  protected long getNextRefreshTime() {
    return lastRefreshTimestamp + Configs.REFRESH_INTERVAL;
  }
  
  @Async protected abstract void refresh();
}
