package com.madgag.garmin;

import static com.madgag.simpleusb.Bits.getShortL;

import java.util.ArrayList;
import java.util.List;

public class GarminProtocolData {
	private final char tag;
	private final short data;

	public GarminProtocolData(char tag, short data) {
		this.tag = tag;
		this.data = data;
		
	}
	
	public static List<GarminProtocolData> fromProtocolArrayData(byte[] paData) {
		List<GarminProtocolData> dataItems=new ArrayList<GarminProtocolData>();
		for (int offset=0;offset<paData.length;offset+=3) {
			dataItems.add(new GarminProtocolData((char)paData[offset], getShortL(paData, offset+1)));
		}
		return dataItems;
	}
	
	@Override
	public String toString() {
		return tag+""+data;
	}
}
