package com.madgag.simpleusb;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import libusbone.LibusboneLibrary;

public class Main {
	public static void main(String[] args) {
		LibusboneLibrary lib = LibusboneLibrary.INSTANCE;
		
		PointerByReference ctx = new PointerByReference();
		LibusboneLibrary.libusb_context[] bang=new LibusboneLibrary.libusb_context[0];
		int r=lib.libusb_init(ctx);
		LibusboneLibrary.libusb_context libUsbContext = new LibusboneLibrary.libusb_context(ctx.getValue());
		System.out.println("r="+r);
		
		PointerByReference deviceList = new PointerByReference();
		deviceList.setPointer(null);
		int cnt=lib.libusb_get_device_list(libUsbContext, deviceList );
		System.out.println("choco tomato "+cnt);
		
		Memory mem=(Memory) deviceList.getPointer();
		long size = mem.getSize();
		System.out.println("size="+size);
		//list.getValue();
		
		Pointer[] pointerArray = deviceList.getPointer().getPointerArray(0,cnt);
		System.out.println("repro");
		
		LibusboneLibrary.libusb_device[] realList= new LibusboneLibrary.libusb_device[cnt];
		for (int i=0;i<realList.length;++i) {
			realList[i]=new LibusboneLibrary.libusb_device(pointerArray[i]);
		}
		
		lib.libusb_free_device_list(realList, 1);
		//lib.libusb_free_device_list(deviceList.getPointer().getPointerArray(base), 1);
		System.out.println("sap");
		
		lib.libusb_exit(libUsbContext);
		System.out.println("bye");
	}
}
