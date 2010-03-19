package com.madgag.garmin;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

import java.util.List;

public class HomogeneousSequenceInterpreter<I> implements PacketSequenceInterpreter<List<I>> {

	private final List<I> list;
	private final Unpacker<I> unpacker;

	public HomogeneousSequenceInterpreter(int numRecords, Unpacker<I> unpacker) {
		this.unpacker = unpacker;
		list = newArrayListWithExpectedSize(numRecords);
	}
	
	@Override
	public List<I> getResult() {
		return list;
	}

	@Override
	public void process(GarminPacket packet) {
		list.add(unpacker.unpack(packet));
	}

}
