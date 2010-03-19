package com.madgag.garmin;

public class GarminRunDescripter {

	private final int trackIndex;
	private final int firstLapIndex;
	private final int lastLapIndex;

	public GarminRunDescripter(int trackIndex, int firstLapIndex, int lastLapIndex) {
		this.trackIndex = trackIndex;
		this.firstLapIndex = firstLapIndex;
		this.lastLapIndex = lastLapIndex;
	}

}
