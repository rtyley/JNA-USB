package com.madgag.garmin;

import static com.madgag.simpleusb.UsbEndpointDirection.OUT;

import java.util.Map;

import libusbone.LibusboneLibrary;
import libusbone.libusb_endpoint_descriptor;
import libusbone.LibusboneLibrary.libusb_device_handle;

import com.madgag.simpleusb.UsbEndpointDirection;
import com.sun.jna.ptr.IntByReference;

public class GarminUsbDevice {
	
	public static class ReadResult {
		private final TransferResultStatus status;
		private final GarminPacket packet;
		public ReadResult(TransferResultStatus status, GarminPacket packet) {
			this.status = status;
			this.packet = packet;
		}
		
		public GarminPacket getPacket() {
			return packet;
		}
		
		public TransferResultStatus getStatus() {
			return status;
		}
	}
	
	public static class TransferResultStatus {
		private final int returnCode;
		private final int transferred;

		public TransferResultStatus(int returnCode,int transferred) {
			this.returnCode = returnCode;
			this.transferred = transferred;
		}
		
		public int getTransferred() {
			return transferred;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+"[returnCode="+returnCode+",transferred="+transferred+"]";
		}
	}
	
	private final LibusboneLibrary lib;
	private final libusb_device_handle deviceHandle;
	private final Map<UsbEndpointDirection, libusb_endpoint_descriptor> bulkEndpoints;
	private final libusb_endpoint_descriptor interruptInEndpoint;
	
	public GarminUsbDevice(LibusboneLibrary lib, libusb_device_handle deviceHandle, Map<UsbEndpointDirection, libusb_endpoint_descriptor> bulkEndpoints, libusb_endpoint_descriptor interruptInEndpoint) {
		this.lib = lib;
		this.deviceHandle = deviceHandle;
		this.bulkEndpoints = bulkEndpoints;
		this.interruptInEndpoint = interruptInEndpoint;
	}
	
	public ReadResult read() {		
		byte[] packetBytes = new byte[1024];
		TransferResultStatus transferResult = interruptTransfer(packetBytes);
		GarminPacket gp = GarminPacket.from(packetBytes);
		System.out.println(packetIOSummary("read",gp));
		return new ReadResult(transferResult, gp);
	}


	private TransferResultStatus interruptTransfer(byte[] packetBytes) {
		IntByReference transferred = new IntByReference();
		int returnCode=lib.libusb_interrupt_transfer(deviceHandle, interruptInEndpoint.bEndpointAddress, packetBytes, packetBytes.length, transferred, 3000);
		TransferResultStatus transferResultStatus = new TransferResultStatus(returnCode, transferred.getValue());
		System.out.println(transferResultStatus);
		return transferResultStatus;
	}

	public TransferResultStatus write(GarminPacket packet) {
		byte[] packetBytes = packet.toBytes();
		System.out.println(packetIOSummary("write",packet));
		return bulkTransfer(packetBytes, bulkEndpoints.get(OUT).bEndpointAddress);
	}

	private TransferResultStatus bulkTransfer(byte[] packetBytes, byte endpoint) {
		IntByReference transferred = new IntByReference();
		int returnCode=lib.libusb_bulk_transfer(deviceHandle,  endpoint, packetBytes, packetBytes.length, transferred, 3000);
		TransferResultStatus transferResultStatus = new TransferResultStatus(returnCode, transferred.getValue());
		System.out.println(transferResultStatus);
		return transferResultStatus;
	}
	


	private static String packetIOSummary(String io,GarminPacket gp) {
		String xmlOpen = "<"+io+" type=\""+gp.getType()+"\" id=\"0x"+Integer.toHexString(gp.getId())+"\" size=\""+gp.getSize()+"\"/>\n";
		for (int i=0;i<gp.getSize();i+=16) {
			
		}
		return xmlOpen+"</"+io+">";
	}
}
