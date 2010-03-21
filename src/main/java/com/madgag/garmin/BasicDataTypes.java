package com.madgag.garmin;

import static java.lang.Math.PI;
import static org.joda.time.DateTimeZone.UTC;
import static org.joda.time.Duration.standardSeconds;

import java.nio.ByteBuffer;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

public class BasicDataTypes {

	public static class Coord {
		private final double lon;
		private final double lat;
	
		Coord(double lon, double lat) {
			this.lon = lon;
			this.lat = lat;
		}
	
		public static Coord fromPositionType(ByteBuffer buffer) {
			double lat = buffer.getInt() * (180D / (1l << 31));
			double lon = buffer.getInt() * (180D / (1l << 31));
			return new Coord(lon, lat);
		}
		
		public static void fromRadianPositionType(ByteBuffer buffer) {
			double lat = buffer.getDouble() * (180 / PI);
			double lon = buffer.getDouble() * (180 / PI);
			System.out.println("lat=" + lat + " lon=" + lon);
		}
	
		@Override
		public String toString() {
			return lat + "," + lon;
		}
	}

	// 12:00 am December 31, 1989 UTC.
	public final static DateTime EPOCH = new DateTime(1989, 12, 31, 0, 0, 0, 0, UTC);
	
	public static ReadableInstant readTimeType(ByteBuffer buffer) {
		int seconds = buffer.getInt();
		return EPOCH.plus(standardSeconds(seconds));
	}
	
	public static Duration readDurationInCentiseconds(ByteBuffer buffer) {
		return new Duration(buffer.getInt()*10L);
	}
}
