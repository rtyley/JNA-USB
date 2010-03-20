package com.madgag.garmin;

import org.joda.time.Interval;

import com.madgag.garmin.Main.Coord;

public class GarminLapDescripter {

	private final Coord begin;
	private final Coord end;
	private final Interval timeInterval;

	public GarminLapDescripter(Interval timeInterval, Coord begin, Coord end) {
		this.timeInterval = timeInterval;
		this.begin = begin;
		this.end = end;
	}

}
