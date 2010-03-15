package com.madgag.garmin;

import static com.madgag.simpleusb.Bits.getShortL;

import java.util.ArrayList;
import java.util.List;

public class ProtocolDataTag {
	private final char tag;
	private final short data;

	public ProtocolDataTag(char tag, short data) {
		this.tag = tag;
		this.data = data;
	}
	
	public static List<ProtocolDataTag> fromProtocolArrayData(byte[] paData) {
		List<ProtocolDataTag> dataItems=new ArrayList<ProtocolDataTag>();
		for (int offset=0;offset<paData.length;offset+=3) {
			dataItems.add(new ProtocolDataTag((char)paData[offset], getShortL(paData, offset+1)));
		}
		return dataItems;
	}
	
	@Override
	public String toString() {
		return tag+""+data;
	}

	public boolean isDataType() {
		return tag=='D';
	}
}
