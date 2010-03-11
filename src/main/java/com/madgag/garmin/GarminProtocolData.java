package com.madgag.garmin;

import java.util.ArrayList;
import java.util.List;

import com.madgag.simpleusb.Bits;

public class GarminProtocolData {
	private final char tag;
	private final short data;

	public GarminProtocolData(char tag, short data) {
		this.tag = tag;
		this.data = data;
		
	}
	
	public static List<GarminProtocolData> fromProtocolArrayData(byte[] paData) {
		List<GarminProtocolData> dataItems=new ArrayList<GarminProtocolData>();
		for (int offset=0;offset<paData.length;offset+=12) {
			dataItems.add(new GarminProtocolData((char)paData[offset], Bits.getShortL(paData, offset+1)));
		}
		return dataItems;
	}
	
	@Override
	public String toString() {
		return tag+""+data;
	}
}
