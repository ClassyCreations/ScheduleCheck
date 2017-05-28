package com.herocc.school.aspencheck;

import com.herocc.school.aspencheck.aspen.Schedule;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class ScheduleTests {
	private Schedule schedule = new Schedule(Jsoup.parse(this.getClass().getClassLoader().getResourceAsStream("AspenSchedule.html"), "UTF-8", ""));
	
	public ScheduleTests() throws IOException {}
	
	@Test
	public void expectedDay(){
		assert schedule.day == 1;
	}
	
	@Test
	public void expectedBlock(){
		assert "A".equals(schedule.block);
	}
	
	@Test
	public void expectedBlockOfDay(){
		assert schedule.blockOfDay == 1;
	}
	
	@Test
	public void expectedClass() {
		assert "228-05 ALGEBRA 2 Teacher, Math 230 A".equals(schedule.currentClass);
	}
	
	@Test
	public void expectedInSession() {
		assert schedule.classInSession;
	}
	
	@Test
	public void expectedBlockSequence(){
		ArrayList<String> dayOneOrder = new ArrayList<>();
		dayOneOrder.add("A"); // Find a better way to do this?
		dayOneOrder.add("B");
		dayOneOrder.add("C");
		dayOneOrder.add("D");
		dayOneOrder.add("E");
		dayOneOrder.add("F");
		assert schedule.blockOrder.equals(dayOneOrder);
	}
}
