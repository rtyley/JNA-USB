package com.madgag.simpleusb;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

public class LibUsbContext extends Structure {
    public static class ByReference extends LibUsbContext implements
	    Structure.ByReference {
	}
    
//    class LibUsbContextByReference extends ByReference {
//        public LibUsbContextByReference() { super(XID.SIZE); }
//        
//        public LibUsbContext getValue() {
//            NativeLong value = getPointer().getNativeLong(0);
//            return value.longValue() == X11.None
//                ? Window.None : new LibUsbContext(value.longValue());
//        }
//    }
}
