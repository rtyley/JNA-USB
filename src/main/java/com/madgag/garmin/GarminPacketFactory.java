package com.madgag.garmin;

import static com.madgag.garmin.GarminPacket.Type.APPLICATION_LAYER;

import com.madgag.simpleusb.Bits;

public class GarminPacketFactory {
	
	public static final short  L001_Pid_Command_Data         = 0x000a,
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
	
	
	public static final short A010_Cmnd_Abort_Transfer                = 0x0000,
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
	
	public static GarminPacket getL001CommandDataPacket(short a010CommandCode) {
		byte[] data = new byte[2];
		Bits.putShortL(data, 0, a010CommandCode);
		return new GarminPacket(APPLICATION_LAYER, L001_Pid_Command_Data, data);
	}
}
