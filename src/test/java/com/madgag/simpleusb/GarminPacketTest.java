package com.madgag.simpleusb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

import com.madgag.garmin.GarminPacket;


public class GarminPacketTest {
	@Test
	public void shouldHaveDataRunningFrom12thByte() throws Exception {
		GarminPacket packet = new GarminPacket(GarminPacket.Type.USB_PROTOCOL_LAYER, (short)123, new byte[] {4,5,6});
		byte[] bytes = packet.toBytes();
		assertThat(bytes[12],equalTo((byte)4));
		assertThat(bytes[13],equalTo((byte)5));
		assertThat(bytes[14],equalTo((byte)6));
	}
	
	@Test
	public void shouldHaveDataSizeWrittenAtAt8thByte() throws Exception {
		GarminPacket packet = new GarminPacket(GarminPacket.Type.USB_PROTOCOL_LAYER, (short)123, new byte[7654321]);
		byte[] bytes = packet.toBytes();
		
		assertThat((bytes[8] & 0xFF)+((bytes[9] & 0xFF)<<8)+((bytes[10]& 0xFF)<<16), equalTo(7654321) );
	}
	
	@Test
	public void shouldGiveCorrectPacketTypeByteValue() throws Exception {
		assertThat(new GarminPacket(GarminPacket.Type.USB_PROTOCOL_LAYER, (short)0, new byte[0]).toBytes()[0], equalTo((byte) 0) );
		assertThat(new GarminPacket(GarminPacket.Type.APPLICATION_LAYER, (short)0, new byte[0]).toBytes()[0], equalTo((byte) 20) );
	}
	
	@Test
	public void shouldHavePacketIdAsShortAt4thByte() throws Exception {
		byte[] bytes = new GarminPacket(GarminPacket.Type.USB_PROTOCOL_LAYER, (short)55677, new byte[0]).toBytes();
		
		assertThat((bytes[4] & 0xFF)+((bytes[5] & 0xFF)<<8), equalTo(55677) );
	}
	
}
