package com.madgag.garmin;

public interface PacketSequenceInterpreter<T> {
	void process(GarminPacket packet);
	
	T getResult();

}
