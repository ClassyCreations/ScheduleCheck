package com.herocc.school.aspencheck.calendar;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;

public class Calendar {
	private Document cal;
	
	public boolean isHalfDay;
	public boolean isNoSchool;
	public ArrayList<String> events;
	
	public Calendar(Document cal){
		this.cal = cal;
		
		this.events = getEvents();
		this.isHalfDay = isHalfDay();
		this.isNoSchool = isNoSchool();
	}
	
	private ArrayList<String> getEvents() {
		Elements matching = cal.body().getElementsByAttributeValueContaining("class", "tribe-events-list-event-title");
		ArrayList<String> events = new ArrayList<>();
		for (Element match : matching){
			events.add(match.text());
		}
		return events;
	}
	
	private boolean isHalfDay() {
		Elements matching = cal.body().getElementsByAttributeValueContaining("class", "tribe-events-list-event-title");
		
		return matching.text().contains("Early Release");
	}
	
	private boolean isNoSchool() {
		Elements matching = cal.body().getElementsByAttributeValueContaining("class", "tribe-events-list-event-title");
		
		// (Almost) one-liner from http://stackoverflow.com/a/40815567/1709894
		boolean isWeekend = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
						.contains(LocalDate.now(ZoneId.of("America/New_York")).getDayOfWeek());
		
		return matching.text().contains("No School") || isWeekend;
	}
	
	public JsonObjectBuilder getJsonData(){
		JsonArrayBuilder jsonEvents = Json.createArrayBuilder();
		for (String eventName : events){
			jsonEvents.add(eventName);
		}
		return Json.createObjectBuilder()
						.add("isHalfDay", isHalfDay)
						.add("isNoSchool", isNoSchool)
						.add("events", jsonEvents);
	}
}
