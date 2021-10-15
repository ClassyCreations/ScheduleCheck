package com.herocc.school.aspencheck.parsers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import com.herocc.school.aspencheck.aspen.schedule.Schedule;
import com.herocc.school.aspencheck.aspen.schedule.parsing.ScheduleDocumentParser;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

public class ScheduleTests {
	private Schedule schedule2017 = new ScheduleDocumentParser(Jsoup.parse(this.getClass().getClassLoader().getResourceAsStream("AspenSchedule.html"), "UTF-8", "")).buildSchedule();
  private Schedule schedule2021 = new ScheduleDocumentParser(Jsoup.parse(this.getClass().getClassLoader().getResourceAsStream("AspenSchedule2021.html"), "UTF-8", "")).buildSchedule();

	public ScheduleTests() throws IOException {}

	@Test
	public void expectedDay() {
		assertTrue(schedule2017.day.startsWith("1"));
    assertTrue(schedule2021.day.startsWith("Fri"));
	}

	@Test
	public void expectedBlock(){
		assertEquals("A", schedule2017.block);
    assertEquals("E", schedule2021.block);
	}

	@Test
	public void expectedBlockOfDay(){
		assertEquals(1, schedule2017.blockOfDay);
	}

	/*@Test
	public void expectedClass() {
		assert "228-05 ALGEBRA 2 Teacher, Math 230 A".equals(schedule.currentClass);
	}*/

	@Test
	public void expectedInSession() {
		assertTrue(schedule2017.classInSession);
    assertTrue(schedule2021.classInSession);
	}

	@Test
	public void expectedBlockSequence(){
		String[] todaysOrder2017 = new String[]{"A", "B", "C", "D", "E", "F"};
		assertArrayEquals(todaysOrder2017, schedule2017.blockOrder.toArray());
	}
}
