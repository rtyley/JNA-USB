package com.madgag.garmin;

import static com.madgag.garmin.GarminPacketFactory.A010_Cmnd_Start_Pvt_Data;
import static com.madgag.garmin.GarminPacketFactory.A010_Cmnd_Transfer_Laps;
import static com.madgag.garmin.GarminPacketFactory.A010_Cmnd_Transfer_Runs;
import static com.madgag.garmin.GarminPacketFactory.A010_Cmnd_Transfer_Trk;
import static com.madgag.simpleusb.UsbEndpointDirection.IN;
import static com.madgag.simpleusb.UsbEndpointType.BULK;
import static com.madgag.simpleusb.UsbEndpointType.INTERRUPT;
import static java.lang.Integer.toHexString;
import static java.lang.Math.PI;
import static java.nio.ByteBuffer.wrap;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static org.joda.time.DateTimeZone.UTC;
import static org.joda.time.Duration.standardSeconds;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import libusbone.LibusboneLibrary;
import libusbone.libusb_config_descriptor;
import libusbone.libusb_device_descriptor;
import libusbone.libusb_endpoint_descriptor;
import libusbone.libusb_interface_descriptor;
import libusbone.LibusboneLibrary.libusb_device_handle;
import libusbone.libusb_interface.ByReference;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.google.common.collect.ListMultimap;
import com.madgag.garmin.GarminUsbDevice.ReadResult;
import com.madgag.garmin.GarminUsbDevice.TransferResultStatus;
import com.madgag.simpleusb.Bits;
import com.madgag.simpleusb.UsbEndpointDirection;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class Main {

	private static int GARMIN_USB_VID = 0x091e;
	private static int GARMIN_USB_PID = 0x0003;

	private static short L001_Pid_Command_Data = 0x000a,
			L001_Pid_Xfer_Cmplt = 0x000c, L001_Pid_Date_Time_Data = 0x000e,
			L001_Pid_Position_Data = 0x0011, L001_Pid_Prx_Wpt_Data = 0x0013,
			L001_Pid_Records = 0x001b,
			/* L001_Pid_Undocumented_1 = 0x001c, */
			L001_Pid_Rte_Hdr = 0x001d, L001_Pid_Rte_Wpt_Data = 0x001e,
			L001_Pid_Almanac_Data = 0x001f, L001_Pid_Trk_Data = 0x0022,
			L001_Pid_Wpt_Data = 0x0023, L001_Pid_Pvt_Data = 0x0033,
			L001_Pid_Rte_Link_Data = 0x0062, L001_Pid_Trk_Hdr = 0x0063,
			L001_Pid_FlightBook_Record = 0x0086, L001_Pid_Lap = 0x0095,
			L001_Pid_Wpt_Cat = 0x0098, L001_Pid_Run = 0x03de,
			L001_Pid_Workout = 0x03df, L001_Pid_Workout_Occurrence = 0x03e0,
			L001_Pid_Fitness_User_Profile = 0x03e1,
			L001_Pid_Workout_Limits = 0x03e2, L001_Pid_Course = 0x0425,
			L001_Pid_Course_Lap = 0x0426, L001_Pid_Course_Point = 0x0427,
			L001_Pid_Course_Trk_Hdr = 0x0428,
			L001_Pid_Course_Trk_Data = 0x0429, L001_Pid_Course_Limits = 0x042a;

	public static void main(String[] args) throws Exception {
		LibusboneLibrary lib = LibusboneLibrary.INSTANCE;

		PointerByReference ctx = new PointerByReference();
		LibusboneLibrary.libusb_context[] bang = new LibusboneLibrary.libusb_context[0];
		int r = lib.libusb_init(ctx);
		LibusboneLibrary.libusb_context libUsbContext = new LibusboneLibrary.libusb_context(
				ctx.getValue());
		System.out.println("r=" + r);

		PointerByReference deviceList = new PointerByReference();
		// deviceList.setPointer(null);
		int cnt = lib.libusb_get_device_list(libUsbContext, deviceList);
		System.out.println("choco tomato " + cnt);

		// Memory mem=(Memory) deviceList.getPointer();
		// long size = mem.getSize();
		// System.out.println("size="+size);
		// list.getValue();

		Pointer[] pointerArray = deviceList.getValue().getPointerArray(0, cnt);
		System.out.println("repro");

		LibusboneLibrary.libusb_device[] realList = new LibusboneLibrary.libusb_device[cnt];
		for (int i = 0; i < realList.length; ++i) {
			LibusboneLibrary.libusb_device libusbDevice = new LibusboneLibrary.libusb_device(
					pointerArray[i]);
			System.out.println("Found " + libusbDevice);
			realList[i] = libusbDevice;

			libusb_device_descriptor desc = new libusb_device_descriptor();
			lib.libusb_get_device_descriptor(libusbDevice, desc);
			System.out.println(toHexString(desc.idVendor) + " "
					+ toHexString(desc.idProduct) + " num conf="
					+ desc.bNumConfigurations);
			if (isGarmin(desc)) {
				doSomethingWithGarmin(lib, libusbDevice);
			}
		}

		lib.libusb_free_device_list(realList, 0);

		lib.libusb_exit(libUsbContext);
		System.out.println("bye");
	}

	private static void doSomethingWithGarmin(LibusboneLibrary lib,
			LibusboneLibrary.libusb_device libusbDevice) throws Exception {
		System.out.println("Found the garmin!");
		PointerByReference deviceHandleRef = new PointerByReference();
		int ret = lib.libusb_open(libusbDevice, deviceHandleRef);
		System.out.println("bangles " + ret);
		libusb_device_handle deviceHandle = new libusb_device_handle(
				deviceHandleRef.getValue());

		IntByReference config = new IntByReference();
		int retGC = lib.libusb_get_configuration(deviceHandle, config);
		System.out.println("retGC=" + retGC + " conf=" + config.getValue());

		int retConf = lib.libusb_set_configuration(deviceHandle, 1);
		System.out.println("retConf=" + retConf);
		int retClaim = lib.libusb_claim_interface(deviceHandle, 0);
		System.out.println("retClaim=" + retClaim);

		PointerByReference configDescriptorRef = new PointerByReference();

		libusb_config_descriptor.ByReference[] t = new libusb_config_descriptor.ByReference[1];
		int retCD = lib.libusb_get_config_descriptor(libusbDevice, (byte) 0, t);
		System.out.println("retCD=" + retCD);
		System.out.println("t[0]" + t[0]);
		libusb_config_descriptor configDescriptor = t[0];
		System.out.println("configDescriptor.bNumInterfaces="
				+ configDescriptor.bNumInterfaces);
		System.out.println("configDescriptor.interface_.num_altsetting="
				+ configDescriptor.interface_.num_altsetting);

		ByReference cdInterface = configDescriptor.interface_;
		libusbone.libusb_interface_descriptor.ByReference altsettingRef = cdInterface.altsetting;

		libusb_interface_descriptor[] array = new libusb_interface_descriptor[cdInterface.num_altsetting];
		altsettingRef.toArray(array);
		Map<UsbEndpointDirection, libusb_endpoint_descriptor> bulkEndpoints = new EnumMap<UsbEndpointDirection, libusb_endpoint_descriptor>(
				UsbEndpointDirection.class);
		libusb_endpoint_descriptor interruptInEndpoint = null;

		for (libusb_interface_descriptor interfaceDes : array) {
			libusb_endpoint_descriptor[] endpoints = new libusb_endpoint_descriptor[interfaceDes.bNumEndpoints];
			interfaceDes.endpoint.toArray(endpoints);

			for (libusb_endpoint_descriptor endpoint : endpoints) {
				System.out.println(endpoint + " type=" + endpoint.getType()
						+ " direction=" + endpoint.getDirection());
				if (endpoint.getType() == BULK) {
					bulkEndpoints.put(endpoint.getDirection(), endpoint);
				} else if (endpoint.getType() == INTERRUPT
						&& endpoint.getDirection() == IN) {
					interruptInEndpoint = endpoint;
				}
			}

		}

		GarminUsbDevice garminDevice = new GarminUsbDevice(lib, deviceHandle,
				bulkEndpoints, interruptInEndpoint);
		startGarminSession(garminDevice);

		lib.libusb_close(deviceHandle);
	}

	private static void startGarminSession(GarminUsbDevice garminDevice)
			throws Exception {
		for (int i = 0; i < 3; ++i) {
			garminDevice.write(GarminPacket.getStartSessionPacket());
		}
		GarminPacket gp = garminDevice.read().getPacket();
		if (gp.getId() != 6) {
			throw new IllegalStateException("Pid_Session_Started      = 0x06 !");
		}
		int unitId = Bits.getIntL(gp.getData(), 0);
		System.out.println("Got device id=" + unitId + " "
				+ Integer.toHexString(unitId));
		// should equal 'c50f1700' according to garmin_get_info - not what it
		// says on the back of my watch
		getA000andA001(garminDevice);
		// getPVT(garminDevice);
		getRuns(garminDevice);
		getLaps(garminDevice);
		getTracks(garminDevice);
	}

	private static void getTracks(GarminUsbDevice garminDevice)
			throws Exception {
		SequenceProtocol<ListMultimap<Integer, GarminTrackDataDescripter>> protocol = new SequenceProtocol<ListMultimap<Integer, GarminTrackDataDescripter>>() {
					public short getCommandId() {
						return A010_Cmnd_Transfer_Trk;
					}

					@Override
					public PacketSequenceInterpreter<ListMultimap<Integer, GarminTrackDataDescripter>> createSequenceInterpreter(
							int numRecords) {
						return new TrackTransferSequenceInterpreter(
								new Unpacker<Integer>() {
									public Integer unpack(
											GarminPacket packet) {// D311
										return (int) Bits.getShortL(
												packet.getData(), 0);
									}
								},
								new Unpacker<GarminTrackDataDescripter>() {
									public GarminTrackDataDescripter unpack(
											GarminPacket packet) {// D304
										ByteBuffer buffer = wrap(packet.getData()).order(LITTLE_ENDIAN);
										Coord posn = Coord.fromPositionType(buffer);
										// 12:00 am December 31, 1989 UTC.
										Instant EPOCH = new DateTime(1989, 12, 31, 0, 0, 0, 0, UTC).toInstant();
										int seconds = buffer.getInt();
										Instant time = EPOCH.plus(standardSeconds(seconds));
										return new GarminTrackDataDescripter(posn, time);
									}
								});
					}
				};
		ListMultimap<Integer, GarminTrackDataDescripter> v = new TransferSequenceReader().getSequence(protocol, garminDevice);
		for (int trackIndex : v.keySet()) { 
			List<GarminTrackDataDescripter> list = v.get(trackIndex);
			System.out.println(trackIndex+""+list.subList(0, 3));
		}
	}

	private static void getRuns(GarminUsbDevice garminDevice) throws Exception {
		new TransferSequenceReader().getSequence(
				new SequenceProtocol<List<GarminRunDescripter>>() {
					public short getCommandId() {
						return A010_Cmnd_Transfer_Runs;
					}

					@Override
					public PacketSequenceInterpreter<List<GarminRunDescripter>> createSequenceInterpreter(
							int numRecords) {
						return new HomogeneousSequenceInterpreter<GarminRunDescripter>(
								numRecords,
								new Unpacker<GarminRunDescripter>() {
									public GarminRunDescripter unpack(
											GarminPacket packet) {
										try {
											return unpackD1009(packet.getData());
										} catch (IOException e) {
											throw new RuntimeException(e);
										}
									}
								});
					}
				}, garminDevice);
	}

	private static void getLaps(GarminUsbDevice garminDevice) throws Exception {
		new TransferSequenceReader().getSequence(
				new SequenceProtocol<List<GarminLapDescripter>>() {
					public short getCommandId() {
						return A010_Cmnd_Transfer_Laps;
					}

					@Override
					public PacketSequenceInterpreter<List<GarminLapDescripter>> createSequenceInterpreter(
							int numRecords) {
						return new HomogeneousSequenceInterpreter<GarminLapDescripter>(
								numRecords,
								new Unpacker<GarminLapDescripter>() {
									public GarminLapDescripter unpack(
											GarminPacket packet) {
										try {
											return unpackD1015(packet.getData());
										} catch (IOException e) {
											throw new RuntimeException(e);
										}
									}
								});
					}
				}, garminDevice);
	}

	private static void getPVT(GarminUsbDevice garminDevice) throws Exception {

		TransferResultStatus status = garminDevice.write(GarminPacketFactory
				.getL001CommandDataPacket(A010_Cmnd_Start_Pvt_Data));
		while (true) {
			ReadResult read = garminDevice.read();
			GarminPacket packet = read.getPacket();
			if (packet.getId() == L001_Pid_Pvt_Data) {
				unpackD800(packet.getData());
			}
		}

	}

	/*
	 * typedef struct { float32 float32 float32 float32 uint16 float64
	 * radian_position_type float32 float32 float32 float32 sint16 uint32 }
	 * D800_Pvt_Data_Type;
	 */

	private static void unpackD800(byte[] data) throws IOException {
		ByteBuffer buffer = wrap(data).order(LITTLE_ENDIAN);
		float alt = buffer.getFloat();
		float epe = buffer.getFloat();
		float eph = buffer.getFloat();
		float epv = buffer.getFloat();
		int fix = buffer.getShort();
		double tow = buffer.getDouble();
		double lat = buffer.getDouble() * (180 / PI);
		double lon = buffer.getDouble() * (180 / PI);
		System.out.println("lat=" + lat + " lon=" + lon);
	}

	public static class Coord {
		private final double lon;
		private final double lat;

		Coord(double lon, double lat) {
			this.lon = lon;
			this.lat = lat;
		}

		public static Coord fromPositionType(ByteBuffer buffer) {
			double lat = buffer.getInt() * (180D / (1l << 31));
			double lon = buffer.getInt() * (180D / (1l << 31));
			return new Coord(lon, lat);
		}

		@Override
		public String toString() {
			return lat + "," + lon;
		}
	}

	/*
	 * garmin_unpack_d1015 ( D1015 * lap, uint8 ** pos ) { GETU16(lap->index);
	 * SKIP(2); GETU32(lap->start_time); GETU32(lap->total_time);
	 * GETF32(lap->total_dist); GETF32(lap->max_speed); GETPOS(lap->begin);
	 * GETPOS(lap->end); GETU16(lap->calories); GETU8(lap->avg_heart_rate);
	 * GETU8(lap->max_heart_rate); GETU8(lap->intensity);
	 * GETU8(lap->avg_cadence); GETU8(lap->trigger_method);
	 * 
	 * GETU8(lap->unknown[0]); GETU8(lap->unknown[1]); GETU8(lap->unknown[2]);
	 * GETU8(lap->unknown[3]); GETU8(lap->unknown[4]); }
	 */

	private static GarminLapDescripter unpackD1015(byte[] data)
			throws IOException {
		ByteBuffer buffer = wrap(data).order(LITTLE_ENDIAN);
		int lapIndex = buffer.getShort();
		buffer.position(buffer.position() + 2);
		int start_time = buffer.getInt();
		int total_time = buffer.getInt();
		float total_dist = buffer.getFloat();
		float max_speed = buffer.getFloat();
		Coord begin = Coord.fromPositionType(buffer), end = Coord
				.fromPositionType(buffer);
		int calories = buffer.getShort();
		byte avg_heart_rate = buffer.get();
		byte max_heart_rate = buffer.get();
		byte intensity = buffer.get();
		byte avg_cadence = buffer.get();
		byte trigger_method = buffer.get();

		System.out.println("total_dist=" + total_dist + " begin=" + begin
				+ " end=" + end);
		return new GarminLapDescripter(begin, end);
	}

	private static GarminRunDescripter unpackD1009(byte[] data)
			throws IOException {
		ByteBuffer buffer = wrap(data).order(LITTLE_ENDIAN);
		int trackIndex = buffer.getShort();
		int first_lap_index = buffer.getShort();
		int last_lap_index = buffer.getShort();
		int sport_type = buffer.get();
		int program_type = buffer.get();
		int multisport = buffer.get();
		buffer.position(buffer.position() + 3);
		System.out.println("trackIndex=" + trackIndex + " first_lap_index="
				+ first_lap_index + " last_lap_index=" + last_lap_index);

		int quick_workout_time = buffer.getInt();
		int quick_workout_distance = buffer.getInt();
		System.out.println("quick_workout_time=" + quick_workout_time);
		return new GarminRunDescripter(trackIndex, first_lap_index,
				last_lap_index);
	}

	private static void unpackD1002(DataInputStream dataInputStream)
			throws IOException {
		int num_valid_steps = dataInputStream.readInt();
		for (int i = 0; i < 20; ++i) {
			String workOutName = dataInputStream.readUTF();
			// all of this stuff is rubbish I'm not really interested in...!
		}
	}

	/*
	 * static void garmin_unpack_d1002 ( D1002 * wkt, uint8 ** pos ) { int i;
	 * 
	 * GETU32(wkt->num_valid_steps); for ( i = 0; i < 20; i++ ) {
	 * GETSTR(wkt->steps[i].custom_name);
	 * GETF32(wkt->steps[i].target_custom_zone_low);
	 * GETF32(wkt->steps[i].target_custom_zone_high);
	 * GETU16(wkt->steps[i].duration_value); GETU8(wkt->steps[i].intensity);
	 * GETU8(wkt->steps[i].duration_type); GETU8(wkt->steps[i].target_type);
	 * GETU8(wkt->steps[i].target_value); SKIP(2); } GETSTR(wkt->name);
	 * GETU8(wkt->sport_type); }
	 */

	/*
	 * static void garmin_unpack_d1009 ( D1009 * run, uint8 ** pos ) {
	 * GETU16(run->track_index); GETU16(run->first_lap_index);
	 * GETU16(run->last_lap_index); GETU8(run->sport_type);
	 * GETU8(run->program_type); GETU8(run->multisport); SKIP(3);
	 * GETU32(run->quick_workout.time); GETF32(run->quick_workout.distance);
	 * garmin_unpack_d1008(&run->workout,pos); }
	 */

	private static void getA000andA001(GarminUsbDevice garminDevice) {
		garminDevice.write(GarminPacket.getProductRequestPacket());

		final short L000_Pid_Protocol_Array = 0x00fd, L000_Pid_Product_Rqst = 0x00fe, L000_Pid_Product_Data = 0x00ff, L000_Pid_Ext_Product_Data = 0x00f8;

		ReadResult read;
		boolean done = false;
		while (!done
				&& (read = garminDevice.read()).getStatus().getTransferred() > 0) {
			System.out.println("Reading...");
			GarminPacket packet = read.getPacket();
			byte[] data = packet.getData();
			switch (packet.getId()) {
			case L000_Pid_Product_Data:
				short productId = Bits.getShortL(data, 0); // unsigned
				short softwareVersion = Bits.getShortL(data, 2); // signed...
				String productDescription = new String(data, 4, data.length - 4);
				System.out.println("productId=" + productId
						+ " softwareVersion=" + softwareVersion
						+ " productDescription=" + productDescription);
				break;
			case L000_Pid_Ext_Product_Data:
				String extData = new String(data);
				System.out.println("Extra prod data - ignore! : " + extData);
				break;
			case L000_Pid_Protocol_Array:
				List<ProtocolDataTag> pds = ProtocolDataTag
						.fromProtocolArrayData(data);
				DeviceProtocols deviceProtocols = DeviceProtocols.from(pds);
				done = true;
				System.out.println(pds);
				System.out.println(deviceProtocols);
				break;
			default:
				System.out.println("Ignoring: " + packet);
			}
		}
	}

	private static boolean isGarmin(libusb_device_descriptor desc) {
		return desc.idVendor == GARMIN_USB_VID
				&& desc.idProduct == GARMIN_USB_PID;
	}
}
