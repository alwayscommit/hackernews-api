package com.hackernews.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class HackernewsApiApplicationTests {

	//testing the logic
	@Test
	void testUnixTimeDiff() {
		long now = Instant.now().getEpochSecond();
		//now - 5 minutes in seconds
		long pastTime = Instant.now().minusSeconds(300).getEpochSecond();
		long minutes = (now - pastTime) / 60 ;
		assertEquals(5, minutes);
	}
	
/*	@Test
	void testUnixYear() {
		LocalDate profileCreationDate = Instant.ofEpochSecond(1397159285L).atZone(ZoneId.systemDefault()).toLocalDate();
		assertEquals("2014-04-11", profileCreationDate.toString());
		assertEquals(6, Period.between(profileCreationDate, LocalDate.now()).getYears());
	}*/
	
/*	@Test
	void testUnixTime() {
		LocalDateTime creationTime = Instant.ofEpochSecond(1591455729L).atZone(ZoneId.systemDefault()).toLocalDateTime();
		System.out.println(creationTime);
	}*/
	
	

}
