package com.madgag.garmin;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class DeviceProtocolsTest {
	
	final ProtocolDataTag protocolA100 = new ProtocolDataTag('A',(short)100),
		dataTypeD100 = new ProtocolDataTag('D',(short)100),
		dataTypeD101 = new ProtocolDataTag('D',(short)101),
		protocolA200 = new ProtocolDataTag('A',(short)200),
		dataTypeD200 = new ProtocolDataTag('D',(short)200),
		dataTypeD201 = new ProtocolDataTag('D',(short)201);
	
	@Test
	public void shouldRecordDataTypesFollowingAppProtocol() throws Exception {

		DeviceProtocols protocols = DeviceProtocols.from(asList(protocolA100,dataTypeD100));
		assertThat(protocols.get(protocolA100).getDeviceSpecificDataTypes().get(0),equalTo(dataTypeD100));
	}
	
	@Test
	public void shouldAddMultipleDataTypesToAppProtocol() throws Exception {
		DeviceProtocols protocols = DeviceProtocols.from(asList(protocolA100,dataTypeD100,dataTypeD101));
		assertThat(protocols.get(protocolA100).getDeviceSpecificDataTypes(),equalTo(asList(dataTypeD100,dataTypeD101)));
	}
	
	@Test
	public void shouldCopeWithEmptyAppProtocols() throws Exception {
		DeviceProtocols protocols = DeviceProtocols.from(asList(protocolA100,protocolA200));
		assertThat(protocols.get(protocolA100).getDeviceSpecificDataTypes().isEmpty(),is(true));
		assertThat(protocols.get(protocolA200).getDeviceSpecificDataTypes().isEmpty(),is(true));
	}
	
	@Test
	public void shouldCopeWithMultiplePopulatedAppProtocols() throws Exception {
		DeviceProtocols protocols = DeviceProtocols.from(asList(protocolA100,dataTypeD100,protocolA200,dataTypeD200,dataTypeD201));
		assertThat(protocols.get(protocolA100).getDeviceSpecificDataTypes(),equalTo(asList(dataTypeD100)));
		assertThat(protocols.get(protocolA200).getDeviceSpecificDataTypes(),equalTo(asList(dataTypeD200,dataTypeD201)));
	}
}
