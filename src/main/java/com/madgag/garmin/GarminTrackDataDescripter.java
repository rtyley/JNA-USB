package com.madgag.garmin;

import org.joda.time.ReadableInstant;

public class GarminTrackDataDescripter {

	private final BasicDataTypes.Coord posn;
	private final ReadableInstant time;

	public GarminTrackDataDescripter(BasicDataTypes.Coord posn, ReadableInstant time) {
		this.posn = posn;
		this.time = time;
	}
	
	public String toString() {
		return time.toString()+":"+posn;
	}

}
