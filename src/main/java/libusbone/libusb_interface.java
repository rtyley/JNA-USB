package libusbone;
/**
 * \ingroup desc<br>
 * A collection of alternate settings for a particular USB interface.<br>
 * <i>native declaration : /usr/include/limits.h:528</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class libusb_interface extends com.sun.jna.Structure {
	/**
	 * Array of interface descriptors. The length of this array is determined<br>
	 * by the num_altsetting field.<br>
	 * C type : libusb_interface_descriptor*
	 */
	public libusbone.libusb_interface_descriptor.ByReference altsetting;
	/// The number of alternate settings that belong to this interface
	public int num_altsetting;
	public libusb_interface() {
		super();
	}
	/**
	 * @param altsetting Array of interface descriptors. The length of this array is determined<br>
	 * by the num_altsetting field.<br>
	 * C type : libusb_interface_descriptor*<br>
	 * @param num_altsetting The number of alternate settings that belong to this interface
	 */
	public libusb_interface(libusbone.libusb_interface_descriptor.ByReference altsetting, int num_altsetting) {
		super();
		this.altsetting = altsetting;
		this.num_altsetting = num_altsetting;
	}
	protected ByReference newByReference() {
		ByReference s = new ByReference();
		s.useMemory(getPointer());
		write();
		s.read();
		return s;
	}
	protected ByValue newByValue() {
		ByValue s = new ByValue();
		s.useMemory(getPointer());
		write();
		s.read();
		return s;
	}
	protected libusb_interface newInstance() {
		libusb_interface s = new libusb_interface();
		s.useMemory(getPointer());
		write();
		s.read();
		return s;
	}
	public static class ByReference extends libusb_interface implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends libusb_interface implements com.sun.jna.Structure.ByValue {}
}
