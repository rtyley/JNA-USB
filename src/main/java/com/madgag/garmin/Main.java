package com.madgag.garmin;

import static com.madgag.simpleusb.UsbEndpointDirection.IN;
import static com.madgag.simpleusb.UsbEndpointType.BULK;
import static com.madgag.simpleusb.UsbEndpointType.INTERRUPT;
import static java.lang.Integer.toHexString;

import java.util.EnumMap;
import java.util.Map;

import libusbone.LibusboneLibrary;
import libusbone.libusb_config_descriptor;
import libusbone.libusb_device_descriptor;
import libusbone.libusb_endpoint_descriptor;
import libusbone.libusb_interface_descriptor;
import libusbone.LibusboneLibrary.libusb_device_handle;
import libusbone.libusb_interface.ByReference;

import com.madgag.garmin.GarminUsbDevice.ReadResult;
import com.madgag.simpleusb.Bits;
import com.madgag.simpleusb.UsbEndpointDirection;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class Main {
	
	private static int GARMIN_USB_VID =  0x091e;
	private static int GARMIN_USB_PID =  0x0003;
	
	
	public static void main(String[] args) {
		LibusboneLibrary lib = LibusboneLibrary.INSTANCE;
		
		PointerByReference ctx = new PointerByReference();
		LibusboneLibrary.libusb_context[] bang=new LibusboneLibrary.libusb_context[0];
		int r=lib.libusb_init(ctx);
		LibusboneLibrary.libusb_context libUsbContext = new LibusboneLibrary.libusb_context(ctx.getValue());
		System.out.println("r="+r);
		
		PointerByReference deviceList = new PointerByReference();
//		deviceList.setPointer(null);
		int cnt=lib.libusb_get_device_list(libUsbContext, deviceList );
		System.out.println("choco tomato "+cnt);
		
//		Memory mem=(Memory) deviceList.getPointer();
//		long size = mem.getSize();
//		System.out.println("size="+size);
		//list.getValue();
		
		Pointer[] pointerArray = deviceList.getValue().getPointerArray(0,cnt);
		System.out.println("repro");
		
		LibusboneLibrary.libusb_device[] realList= new LibusboneLibrary.libusb_device[cnt];
		for (int i=0;i<realList.length;++i) {
			LibusboneLibrary.libusb_device libusbDevice = new LibusboneLibrary.libusb_device(pointerArray[i]);
			System.out.println("Found "+libusbDevice);
			realList[i]=libusbDevice;
			
			libusb_device_descriptor desc = new libusb_device_descriptor();
			lib.libusb_get_device_descriptor(libusbDevice, desc);
			System.out.println( toHexString(desc.idVendor)+" "+toHexString(desc.idProduct)+" num conf="+desc.bNumConfigurations);
			if (isGarmin(desc)) {
				doSomethingWithGarmin(lib, libusbDevice);
			}
		}
		
		lib.libusb_free_device_list(realList, 0);
		//lib.libusb_free_device_list(deviceList.getPointer().getPointerArray(base), 1);
		System.out.println("sap");
		
		lib.libusb_exit(libUsbContext);
		System.out.println("bye");
	}


	private static void doSomethingWithGarmin(LibusboneLibrary lib,	LibusboneLibrary.libusb_device libusbDevice) {
		System.out.println("Found the garmin!");
		PointerByReference deviceHandleRef = new PointerByReference();
		int ret=lib.libusb_open(libusbDevice, deviceHandleRef);
		System.out.println("bangles "+ret);
		libusb_device_handle deviceHandle = new libusb_device_handle(deviceHandleRef.getValue());
		
		IntByReference config = new IntByReference();
		int retGC=lib.libusb_get_configuration(deviceHandle, config);
		System.out.println("retGC="+retGC+ " conf=" +config.getValue());
		
		
		int retConf = lib.libusb_set_configuration(deviceHandle, 1);
		System.out.println("retConf="+retConf);
		int retClaim =lib.libusb_claim_interface(deviceHandle, 0);
		System.out.println("retClaim="+retClaim);
		
		PointerByReference configDescriptorRef = new PointerByReference();
		
		libusb_config_descriptor.ByReference[] t= new libusb_config_descriptor.ByReference[1];
		int retCD=lib.libusb_get_config_descriptor(libusbDevice, (byte)0, t);
		System.out.println("retCD="+retCD);
		System.out.println("t[0]"+t[0]);
		libusb_config_descriptor configDescriptor = t[0];
		System.out.println("configDescriptor.bNumInterfaces="+configDescriptor.bNumInterfaces);
		System.out.println("configDescriptor.interface_.num_altsetting="+configDescriptor.interface_.num_altsetting);
		
		ByReference cdInterface = configDescriptor.interface_;
		libusbone.libusb_interface_descriptor.ByReference altsettingRef = cdInterface.altsetting;
		
		libusb_interface_descriptor[] array = new libusb_interface_descriptor[cdInterface.num_altsetting];
		altsettingRef.toArray(array);
		Map<UsbEndpointDirection,libusb_endpoint_descriptor> bulkEndpoints=new EnumMap<UsbEndpointDirection,libusb_endpoint_descriptor>(UsbEndpointDirection.class);
		libusb_endpoint_descriptor interruptInEndpoint=null;
		
		
		for (libusb_interface_descriptor interfaceDes : array) {
			libusb_endpoint_descriptor[] endpoints = new libusb_endpoint_descriptor[interfaceDes.bNumEndpoints];
			interfaceDes.endpoint.toArray(endpoints);
			
			for (libusb_endpoint_descriptor endpoint : endpoints) {
				System.out.println(endpoint+" type="+endpoint.getType()+" direction="+endpoint.getDirection());
				if (endpoint.getType()==BULK) {
					bulkEndpoints.put(endpoint.getDirection(), endpoint);
				} else if (endpoint.getType()==INTERRUPT && endpoint.getDirection()==IN) {
					interruptInEndpoint=endpoint;
				}
			}
			
		}
		
		GarminUsbDevice garminDevice = new GarminUsbDevice(lib, deviceHandle, bulkEndpoints, interruptInEndpoint);
		startGarminSession(garminDevice);
		
		
		lib.libusb_close(deviceHandle);
	}


	private static void startGarminSession(GarminUsbDevice garminDevice) {
		for (int i=0;i<3;++i) {
			garminDevice.write(GarminPacket.getStartSessionPacket());
		}
		GarminPacket gp = garminDevice.read().getPacket();
		if (gp.getId()!=6) {
			throw new IllegalStateException("Pid_Session_Started      = 0x06 !");
		}
		int unitId=Bits.getIntL(gp.getData(), 0);
		System.out.println("Got device id="+unitId +" "+Integer.toHexString(unitId));
		// should equal 'c50f1700' according to garmin_get_info - not what it says on the back of my watch
		garminDevice.write(GarminPacket.getProductRequestPacket());
		ReadResult read;

		final short
  L000_Pid_Protocol_Array       = 0x00fd,
  L000_Pid_Product_Rqst         = 0x00fe,
  L000_Pid_Product_Data         = 0x00ff,
  L000_Pid_Ext_Product_Data     = 0x00f8;
		
		while ((read=garminDevice.read()).getStatus().getTransferred()>0) {
			System.out.println("Reading...");
			GarminPacket packet = read.getPacket();
			byte[] data = packet.getData();
			switch (packet.getId()) {
				case L000_Pid_Product_Data:
					short productId = Bits.getShortL(data, 0); // unsigned
					short softwareVersion = Bits.getShortL(data, 2); // signed...
					String productDescription = new String(data, 4,data.length-4);
					System.out.println("productId="+productId+" softwareVersion="+softwareVersion+" productDescription="+productDescription);
			}
		}
		
	}






	private static boolean isGarmin(libusb_device_descriptor desc) {
		return desc.idVendor==GARMIN_USB_VID && desc.idProduct==GARMIN_USB_PID;
	}
}
