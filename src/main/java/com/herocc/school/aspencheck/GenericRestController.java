package com.herocc.school.aspencheck;

public abstract class GenericRestController {
  protected long refreshTime = 0 / 1000; // UNIX Timestamp
  
  public GenericRestController() { refresh(); }
  
  protected long getNextRefreshTime() {
    return refreshTime + Configs.REFRESH_INTERVAL;
  }
  
  protected abstract void refresh();
}
