package com.madgag.simpleusb;

import static java.lang.System.arraycopy;

public class GarminPacket {
	public enum Type {
		USB_PROTOCOL_LAYER((byte)0),
		APPLICATION_LAYER((byte)20);
		
		private final byte packetValue;
		
		Type(byte packetValue) {
			this.packetValue=packetValue;
		}
		
		public byte getPacketValue() {
			return packetValue;
		}
	}
	
	public GarminPacket(Type type, short id, byte[] data) {
		this.type = type;
		this.id = id;
		this.data = data;
	}
	
	Type type;
	
	short id;
	
	byte[] data;
	
	public byte[] toBytes() {
		byte[] b = new byte[12+data.length];
		b[0]=type.getPacketValue();
		b[4]=(byte) id;
		b[5]=(byte) (id>>>8);
		b[8]=(byte) data.length;
		b[9]=(byte) (data.length>>>8);
		b[10]=(byte) (data.length>>>16);
		b[11]=(byte) (data.length>>>24);
		arraycopy(data, 0, b, 12, data.length);
		return b;
	}
	
	
	public static GarminPacket getStartSessionPacket() {
		byte Pid_Data_Available       = 0x02,
		  Pid_Start_Session        = 0x05,
		  Pid_Session_Started      = 0x06;
		return new GarminPacket(Type.USB_PROTOCOL_LAYER, Pid_Start_Session, new byte[0]);
	}
	
}
