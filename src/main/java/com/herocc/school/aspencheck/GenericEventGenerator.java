package com.herocc.school.aspencheck;

import javax.json.JsonArrayBuilder;
import java.io.IOException;
import java.util.List;

public interface GenericEventGenerator {
  List getEvents(boolean checkEventsOccurringNow) throws IOException;
  JsonArrayBuilder getJsonData();
}
