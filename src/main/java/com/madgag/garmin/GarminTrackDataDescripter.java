package com.madgag.garmin;

import org.joda.time.Instant;

import com.madgag.garmin.Main.Coord;

public class GarminTrackDataDescripter {

	private final Coord posn;
	private final Instant time;

	public GarminTrackDataDescripter(Coord posn, Instant time) {
		this.posn = posn;
		this.time = time;
	}
	
	public String toString() {
		return time.toString()+":"+posn;
	}

}
