package com.madgag.garmin;

public interface Unpacker<I> {

	I unpack(GarminPacket packet);

}
