package com.madgag.garmin;

import org.joda.time.Interval;

import com.madgag.garmin.BasicDataTypes.Coord;

public class GarminLapDescripter {

	private final BasicDataTypes.Coord begin;
	private final BasicDataTypes.Coord end;
	private final Interval timeInterval;

	public GarminLapDescripter(Interval timeInterval, BasicDataTypes.Coord begin, BasicDataTypes.Coord end) {
		this.timeInterval = timeInterval;
		this.begin = begin;
		this.end = end;
	}

}
