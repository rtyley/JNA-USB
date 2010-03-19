package com.madgag.garmin;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.madgag.simpleusb.Bits.getShortL;
import static java.util.Collections.emptyList;

import java.util.List;

import com.madgag.garmin.GarminUsbDevice.ReadResult;
import com.madgag.garmin.GarminUsbDevice.TransferResultStatus;

public class TransferSequenceReader {
	
	private static short   L001_Pid_Command_Data         = 0x000a,
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
	
	public <T> T getSequence(SequenceProtocol<T> protocol, GarminUsbDevice garminDevice) throws Exception {
		TransferResultStatus status = garminDevice.write(GarminPacketFactory.getL001CommandDataPacket(protocol.getCommandId()));
		ReadResult read = garminDevice.read();
		if (read.getStatus().getTransferred()>0) {
			int id=read.getPacket().getId();
			System.out.println("Got packet id="+id +" hex:"+Integer.toHexString(id));
			if (id==L001_Pid_Records) {
				int numRecords=getShortL(read.getPacket().getData(), 0);
				System.out.println("numRecords="+numRecords);
				
				PacketSequenceInterpreter<T> sequenceInterpreter = protocol.createSequenceInterpreter(numRecords);
				
				for (int i=0;i<numRecords;++i) {
					GarminPacket packet = garminDevice.read().getPacket();
					sequenceInterpreter.process(packet);
				}
				GarminPacket completeness = garminDevice.read().getPacket();
				
				if (completeness.getId()==L001_Pid_Xfer_Cmplt) {
					System.out.println("Completed "+completeness);
				}
				return sequenceInterpreter.getResult();
			}	
		}
		return null;
	}
}
