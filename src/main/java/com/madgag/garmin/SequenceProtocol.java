package com.madgag.garmin;

public interface SequenceProtocol<T> {

	public PacketSequenceInterpreter<T> createSequenceInterpreter(int numRecords);

	public short getCommandId();

}
