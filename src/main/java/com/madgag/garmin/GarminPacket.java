package com.madgag.garmin;

import static com.madgag.garmin.GarminPacket.Type.APPLICATION_LAYER;
import static com.madgag.simpleusb.Bits.getIntL;
import static com.madgag.simpleusb.Bits.getShortL;
import static com.madgag.simpleusb.Bits.putIntL;
import static com.madgag.simpleusb.Bits.putShortL;
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
		putShortL(b, 4, id);
		putIntL(b,8,data.length);
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
		short L000_Pid_Protocol_Array = 0x00fd,
		L000_Pid_Product_Rqst = 0x00fe,
		L000_Pid_Product_Data = 0x00ff,
		L000_Pid_Ext_Product_Data = 0x00f8;
		return new GarminPacket(APPLICATION_LAYER, L000_Pid_Product_Rqst, new byte[0]);
	}
	
	public static GarminPacket getCommandDataPacket() {
		short  L001_Pid_Command_Data         = 0x000a,
		  L001_Pid_Xfer_Cmplt           = 0x000c,
		  L001_Pid_Date_Time_Data       = 0x000e,
		  L001_Pid_Position_Data        = 0x0011,
		  L001_Pid_Prx_Wpt_Data         = 0x0013,
		  L001_Pid_Records              = 0x001b,
		  /* L001_Pid_Undocumented_1    = 0x001c, */
		  L001_Pid_Rte_Hdr              = 0x001d,
		  L001_Pid_Rte_Wpt_Data         = 0x001e,
		  L001_Pid_Almanac_Data         = 0x001f,
		  L001_Pid_Trk_Data             = 0x0022,
		  L001_Pid_Wpt_Data             = 0x0023,
		  L001_Pid_Pvt_Data             = 0x0033,
		  L001_Pid_Rte_Link_Data        = 0x0062,
		  L001_Pid_Trk_Hdr              = 0x0063,
		  L001_Pid_FlightBook_Record    = 0x0086,
		  L001_Pid_Lap                  = 0x0095,
		  L001_Pid_Wpt_Cat              = 0x0098,
		  L001_Pid_Run                  = 0x03de,
		  L001_Pid_Workout              = 0x03df,
		  L001_Pid_Workout_Occurrence   = 0x03e0,
		  L001_Pid_Fitness_User_Profile = 0x03e1,
		  L001_Pid_Workout_Limits       = 0x03e2,
		  L001_Pid_Course               = 0x0425,
		  L001_Pid_Course_Lap           = 0x0426,
		  L001_Pid_Course_Point         = 0x0427,
		  L001_Pid_Course_Trk_Hdr       = 0x0428,
		  L001_Pid_Course_Trk_Data      = 0x0429,
		  L001_Pid_Course_Limits        = 0x042a;
		
		
		short A010_Cmnd_Abort_Transfer                = 0x0000,
		  A010_Cmnd_Transfer_Alm                  = 0x0001,
		  A010_Cmnd_Transfer_Posn                 = 0x0002,
		  A010_Cmnd_Transfer_Prx                  = 0x0003,
		  A010_Cmnd_Transfer_Rte                  = 0x0004,
		  A010_Cmnd_Transfer_Time                 = 0x0005,
		  A010_Cmnd_Transfer_Trk                  = 0x0006,
		  A010_Cmnd_Transfer_Wpt                  = 0x0007,
		  A010_Cmnd_Turn_Off_Pwr                  = 0x0008,
		  A010_Cmnd_Start_Pvt_Data                = 0x0031,
		  A010_Cmnd_Stop_Pvt_Data                 = 0x0032,
		  A010_Cmnd_FlightBook_Transfer           = 0x005c,
		  A010_Cmnd_Transfer_Laps                 = 0x0075,
		  A010_Cmnd_Transfer_Wpt_Cats             = 0x0079,
		  A010_Cmnd_Transfer_Runs                 = 0x01c2,
		  A010_Cmnd_Transfer_Workouts             = 0x01c3,
		  A010_Cmnd_Transfer_Workout_Occurrences  = 0x01c4,
		  A010_Cmnd_Transfer_Fitness_User_Profile = 0x01c5,
		  A010_Cmnd_Transfer_Workout_Limits       = 0x01c6,
		  A010_Cmnd_Transfer_Courses              = 0x0231,
		  A010_Cmnd_Transfer_Course_Laps          = 0x0232,
		  A010_Cmnd_Transfer_Course_Points        = 0x0233,
		  A010_Cmnd_Transfer_Course_Tracks        = 0x0234,
		  A010_Cmnd_Transfer_Course_Limits        = 0x0235;
		
		byte[] data = new byte[2];
		Bits.putShortL(data, 0, A010_Cmnd_Transfer_Runs);
		return new GarminPacket(APPLICATION_LAYER, L001_Pid_Command_Data, data);
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
