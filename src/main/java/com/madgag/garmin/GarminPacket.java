package com.madgag.garmin;

import static com.madgag.simpleusb.Bits.getIntL;
import static com.madgag.simpleusb.Bits.getShortL;
import static java.lang.System.arraycopy;

import com.madgag.simpleusb.Bits;

public class GarminPacket {
	public enum Type {
		USB_PROTOCOL_LAYER((byte) 0), APPLICATION_LAYER((byte) 20);

		private final byte packetValue;

		Type(byte packetValue) {
			this.packetValue = packetValue;
		}

		public byte getPacketValue() {
			return packetValue;
		}

		public static Type from(byte typeByte) {
			return typeByte == 0 ? USB_PROTOCOL_LAYER : APPLICATION_LAYER;
		}
	}

	public GarminPacket(Type type, short id, byte[] data) {
		this.type = type;
		this.id = id;
		this.data = data;
	}

	private Type type;

	private short id;

	private byte[] data;

	public byte[] toBytes() {
		byte[] b = new byte[12 + data.length];
		b[0] = type.getPacketValue();
		b[4] = (byte) id;
		b[5] = (byte) (id >>> 8);
		b[8] = (byte) data.length;
		b[9] = (byte) (data.length >>> 8);
		b[10] = (byte) (data.length >>> 16);
		b[11] = (byte) (data.length >>> 24);
		arraycopy(data, 0, b, 12, data.length);
		return b;
	}

	public static GarminPacket getStartSessionPacket() {
		byte Pid_Data_Available = 0x02, Pid_Start_Session = 0x05, Pid_Session_Started = 0x06;
		return new GarminPacket(Type.USB_PROTOCOL_LAYER, Pid_Start_Session,
				new byte[0]);
	}

	public static GarminPacket from(byte[] b) {
		int dataLen = getIntL(b,8);
		byte[] data = new byte[dataLen];
		arraycopy(b, 12, data, 0, dataLen);
		
		return new GarminPacket(Type.from(b[0]), getShortL(b, 4), data);
	}

	public static GarminPacket getProductRequestPacket() {
		short L000_Pid_Protocol_Array = 0x00fd, L000_Pid_Product_Rqst = 0x00fe, L000_Pid_Product_Data = 0x00ff, L000_Pid_Ext_Product_Data = 0x00f8;
		return new GarminPacket(Type.USB_PROTOCOL_LAYER, L000_Pid_Product_Rqst,
				new byte[0]);
	}

	public Type getType() {
		return type;
	}

	public short getId() {
		return id;
	}

	public int getSize() {
		return data.length;
	}

	public byte[] getData() {
		return data;
	}

}
