package com.madgag.garmin;

import static java.util.Collections.unmodifiableList;

import java.util.List;

public class Protocol {

	private final ProtocolDataTag protocolId;
	private final List<ProtocolDataTag> deviceSpecificDataTypes;

	public Protocol(ProtocolDataTag protocolId, List<ProtocolDataTag> deviceSpecificDataTypes) {
		this.protocolId = protocolId;
		this.deviceSpecificDataTypes = unmodifiableList(deviceSpecificDataTypes);
	}
	
	public List<ProtocolDataTag> getDeviceSpecificDataTypes() {
		return deviceSpecificDataTypes;
	}
	
	public ProtocolDataTag getProtocolId() {
		return protocolId;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+"["+protocolId+":"+deviceSpecificDataTypes+"]";
	}
	
}
