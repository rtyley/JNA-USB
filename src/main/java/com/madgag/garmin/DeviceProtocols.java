package com.madgag.garmin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DeviceProtocols {
	
	Map<ProtocolDataTag,Protocol> protocolMap = new LinkedHashMap<ProtocolDataTag,Protocol>();
	
	private DeviceProtocols(List<Protocol> protocols) {
		for (Protocol protocol : protocols) {
			protocolMap.put(protocol.getProtocolId(), protocol);
		}
	}
	
	public static DeviceProtocols from(List<ProtocolDataTag> protocolDataTags) {
		List<Protocol> protocols = new ArrayList<Protocol>(protocolDataTags.size());
		List<ProtocolDataTag> dataTypesForProtocol = new ArrayList<ProtocolDataTag>();
		ProtocolDataTag protocolTag=null;
		for (ProtocolDataTag dataTag : protocolDataTags) {
			if (dataTag.isDataType()) {
				dataTypesForProtocol.add(dataTag);
			} else {
				if (protocolTag!=null) {
					protocols.add(new Protocol(protocolTag, dataTypesForProtocol));
				}
				dataTypesForProtocol = new ArrayList<ProtocolDataTag>();
				protocolTag=dataTag;
			}
		}
		protocols.add(new Protocol(protocolTag, dataTypesForProtocol));
		return new DeviceProtocols(protocols);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+"["+protocolMap+"]";
	}

	public Protocol get(ProtocolDataTag protocolDataTag) {
		return protocolMap.get(protocolDataTag);
	}
}
