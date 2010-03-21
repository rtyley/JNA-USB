package com.madgag.garmin;

import static com.google.common.collect.Maps.newEnumMap;
import static com.madgag.garmin.GarminPacketFactory.A010_Cmnd_Transfer_Laps;
import static com.madgag.garmin.GarminPacketFactory.A010_Cmnd_Transfer_Runs;
import static com.madgag.garmin.GarminPacketFactory.A010_Cmnd_Transfer_Trk;
import static com.madgag.simpleusb.UsbEndpointDirection.IN;
import static com.madgag.simpleusb.UsbEndpointType.BULK;
import static com.madgag.simpleusb.UsbEndpointType.INTERRUPT;
import static java.lang.Integer.toHexString;
import static java.nio.ByteBuffer.wrap;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import libusbone.LibusboneLibrary;
import libusbone.libusb_config_descriptor;
import libusbone.libusb_device_descriptor;
import libusbone.libusb_endpoint_descriptor;
import libusbone.libusb_interface_descriptor;
import libusbone.LibusboneLibrary.libusb_device_handle;
import libusbone.libusb_interface.ByReference;

import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;

import com.google.common.collect.ListMultimap;
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

		LibusboneLibrary.libusb_context libUsbContext = getContext(lib);

		LibusboneLibrary.libusb_device[] devices = getDeviceList(lib, libUsbContext);
		
		for (LibusboneLibrary.libusb_device libusbDevice : devices) {
			libusb_device_descriptor desc = getDeviceDescriptor(lib, libusbDevice);
			if (isGarmin(desc)) {
				doSomethingWithGarmin(lib, libusbDevice);
			}
		}

		lib.libusb_free_device_list(devices, 0); //should be 1, except this mysteriously causes JVM heapdump
		System.out.println("About to exit context");
		lib.libusb_exit(libUsbContext);
		System.out.println("bye");
	}

	private static libusb_device_descriptor getDeviceDescriptor(
			LibusboneLibrary lib, LibusboneLibrary.libusb_device libusbDevice) {
		libusb_device_descriptor desc = new libusb_device_descriptor();
		lib.libusb_get_device_descriptor(libusbDevice, desc);
		System.out.println(toHexString(desc.idVendor) + " "
				+ toHexString(desc.idProduct) + " num conf="
				+ desc.bNumConfigurations);
		return desc;
	}

	private static LibusboneLibrary.libusb_context getContext(LibusboneLibrary lib) {
		PointerByReference ctx = new PointerByReference();
		LibusboneLibrary.libusb_context[] bang = new LibusboneLibrary.libusb_context[0];
		int r = lib.libusb_init(ctx);
		System.out.println("r=" + r);
		LibusboneLibrary.libusb_context libUsbContext = new LibusboneLibrary.libusb_context(ctx.getValue());
		return libUsbContext;
	}

	private static LibusboneLibrary.libusb_device[] getDeviceList(LibusboneLibrary lib, LibusboneLibrary.libusb_context libUsbContext) {
		PointerByReference deviceList = new PointerByReference();
		int cnt = lib.libusb_get_device_list(libUsbContext, deviceList);
		Pointer[] pointerArray = deviceList.getValue().getPointerArray(0, cnt);
		System.out.println("repro");
		LibusboneLibrary.libusb_device[] realList = new LibusboneLibrary.libusb_device[cnt];
		for (int i = 0; i < cnt; ++i) {
			realList[i] = new LibusboneLibrary.libusb_device(pointerArray[i]);
		}
		return realList;
	}

	private static void doSomethingWithGarmin(LibusboneLibrary lib, LibusboneLibrary.libusb_device libusbDevice) throws Exception {
		System.out.println("Found the garmin!");
		libusb_device_handle deviceHandle = getDeviceHandle(lib, libusbDevice);

		libusb_config_descriptor configDescriptor = getConfigDescriptor(lib, libusbDevice, deviceHandle);

		GarminUsbDevice garminUsbDevice = establishGarminUsbDeviceWithConfig(lib, deviceHandle, configDescriptor.interface_);
		startGarminSession(garminUsbDevice);

		exerciseGarmin(garminUsbDevice);
		
		lib.libusb_close(deviceHandle);
	}

	private static libusb_config_descriptor getConfigDescriptor(
			LibusboneLibrary lib, LibusboneLibrary.libusb_device libusbDevice,
			libusb_device_handle deviceHandle) {
		IntByReference config = new IntByReference();
		int retGC = lib.libusb_get_configuration(deviceHandle, config);
		System.out.println("retGC=" + retGC + " conf=" + config.getValue());

		int retConf = lib.libusb_set_configuration(deviceHandle, 1);
		System.out.println("retConf=" + retConf);
		int retClaim = lib.libusb_claim_interface(deviceHandle, 0);
		System.out.println("retClaim=" + retClaim);

		libusb_config_descriptor.ByReference[] t = new libusb_config_descriptor.ByReference[1];
		int retCD = lib.libusb_get_config_descriptor(libusbDevice, (byte) 0, t);
		System.out.println("retCD=" + retCD);
		System.out.println("t[0]" + t[0]);
		libusb_config_descriptor configDescriptor = t[0];
		System.out.println("configDescriptor.bNumInterfaces="
				+ configDescriptor.bNumInterfaces);
		System.out.println("configDescriptor.interface_.num_altsetting="
				+ configDescriptor.interface_.num_altsetting);
		return configDescriptor;
	}

	private static libusb_device_handle getDeviceHandle(LibusboneLibrary lib,
			LibusboneLibrary.libusb_device libusbDevice) {
		PointerByReference deviceHandleRef = new PointerByReference();
		int ret = lib.libusb_open(libusbDevice, deviceHandleRef);
		System.out.println("bangles " + ret);
		libusb_device_handle deviceHandle = new libusb_device_handle(
				deviceHandleRef.getValue());
		return deviceHandle;
	}

	private static GarminUsbDevice establishGarminUsbDeviceWithConfig(
			LibusboneLibrary lib, libusb_device_handle deviceHandle,
			ByReference cdInterface) {
		libusbone.libusb_interface_descriptor.ByReference altsettingRef = cdInterface.altsetting;

		libusb_interface_descriptor[] array = (libusb_interface_descriptor[]) altsettingRef.toArray(new libusb_interface_descriptor[cdInterface.num_altsetting]);
		
		
		Map<UsbEndpointDirection, libusb_endpoint_descriptor> bulkEndpoints = newEnumMap(UsbEndpointDirection.class);
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

		return new GarminUsbDevice(lib, deviceHandle, bulkEndpoints, interruptInEndpoint);
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
		new ProductRequestProtocol().getA000andA001(garminDevice);
	}

	private static void exerciseGarmin(GarminUsbDevice garminDevice)
			throws Exception {
		new PositionVelocityTimeProtocol().getPVT(garminDevice);
		getRuns(garminDevice);
		getLaps(garminDevice);
		//getTracks(garminDevice);
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
									public GarminTrackDataDescripter unpack(GarminPacket packet) {// D304
										ByteBuffer buffer = wrap(packet.getData()).order(LITTLE_ENDIAN);
										BasicDataTypes.Coord posn = BasicDataTypes.Coord.fromPositionType(buffer);
										ReadableInstant time = BasicDataTypes.readTimeType(buffer);
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

	private static GarminLapDescripter unpackD1015(byte[] data)
			throws IOException {
		ByteBuffer buffer = wrap(data).order(LITTLE_ENDIAN);
		int lapIndex = buffer.getShort();
		buffer.position(buffer.position() + 2);
		 
		ReadableInstant start_time = BasicDataTypes.readTimeType(buffer);
		Duration total_time = BasicDataTypes.readDurationInCentiseconds(buffer);
		Interval interval = total_time.toIntervalFrom(start_time);
		
		float total_dist = buffer.getFloat();
		float max_speed = buffer.getFloat();
		BasicDataTypes.Coord begin = BasicDataTypes.Coord.fromPositionType(buffer), end = BasicDataTypes.Coord
				.fromPositionType(buffer);
		int calories = buffer.getShort();
		byte avg_heart_rate = buffer.get();
		byte max_heart_rate = buffer.get();
		byte intensity = buffer.get();
		byte avg_cadence = buffer.get();
		byte trigger_method = buffer.get();

		System.out.println("total_dist=" + total_dist + " begin=" + begin
				+ " end=" + end+" interval="+interval);
		return new GarminLapDescripter(interval, begin, end);
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

	

	private static boolean isGarmin(libusb_device_descriptor desc) {
		return desc.idVendor == GARMIN_USB_VID
				&& desc.idProduct == GARMIN_USB_PID;
	}

}
