package com.madgag.garmin;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

public class TrackTransferSequenceInterpreter implements PacketSequenceInterpreter<ListMultimap<Integer,GarminTrackDataDescripter>> {

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
	  L001_Pid_Trk_Hdr              = 0x0063;
	
	Unpacker<Integer> trackHeaderUnpacker;
	Unpacker<GarminTrackDataDescripter> trackItemUnpacker;
	
	public TrackTransferSequenceInterpreter(Unpacker<Integer> trackHeaderUnpacker, Unpacker<GarminTrackDataDescripter> trackItemUnpacker) {
		this.trackHeaderUnpacker = trackHeaderUnpacker;
		this.trackItemUnpacker = trackItemUnpacker;
		
	}
	
	
	int currentTrackIndex=-1;
	List<GarminTrackDataDescripter> currentTrackData;
	ListMultimap<Integer, GarminTrackDataDescripter> fondoo= ArrayListMultimap.create();

	@Override
	public void process(GarminPacket packet) {
		if (packet.getId()==L001_Pid_Trk_Hdr) {
			currentTrackIndex=trackHeaderUnpacker.unpack(packet);
		} else {
			GarminTrackDataDescripter trackPoint = trackItemUnpacker.unpack(packet);
			fondoo.put(currentTrackIndex, trackPoint);
		}
	}
	
	@Override
	public ListMultimap<Integer, GarminTrackDataDescripter> getResult() {
		return fondoo;
	}



}
