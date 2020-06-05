package com.hackernews.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class HackernewsApiApplicationTests {

	//testing the logic
	@Test
	void testUnixTimeDiff() {
		long now = Instant.now().getEpochSecond();
		//now - 5 minutes in milliseconds
		long pastTime = Instant.now().minusSeconds(600).getEpochSecond();
		long minutes = (now - pastTime) / 60;
		System.out.println(minutes);
		assertEquals(5, minutes);
	}

}
