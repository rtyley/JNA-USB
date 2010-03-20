package com.madgag.garmin;

import static org.joda.time.DateTimeZone.UTC;
import static org.joda.time.Duration.standardSeconds;

import java.nio.ByteBuffer;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;

public class BasicDataTypes {

	// 12:00 am December 31, 1989 UTC.
	private final static Instant EPOCH = new DateTime(1989, 12, 31, 0, 0, 0, 0, UTC).toInstant();
	
	public static Instant readTimeType(ByteBuffer buffer) {
		int seconds = buffer.getInt();
		return EPOCH.plus(standardSeconds(seconds));
	}
	
	public static Duration readDurationInCentiseconds(ByteBuffer buffer) {
		return new Duration(buffer.getInt()*10L);
	}
}
