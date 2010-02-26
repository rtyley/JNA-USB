package libusbone;
/**
 * Structure crudely representing a timezone.<br>
 * This is obsolete and should never be used.<br>
 * <i>native declaration : /usr/include/sys/time.h:54</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class timezone extends com.sun.jna.Structure {
	/// Minutes west of GMT.
	public int tz_minuteswest;
	/// Nonzero if DST is ever in effect.
	public int tz_dsttime;
	public timezone() {
		super();
	}
	/**
	 * @param tz_minuteswest Minutes west of GMT.<br>
	 * @param tz_dsttime Nonzero if DST is ever in effect.
	 */
	public timezone(int tz_minuteswest, int tz_dsttime) {
		super();
		this.tz_minuteswest = tz_minuteswest;
		this.tz_dsttime = tz_dsttime;
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
	protected timezone newInstance() {
		timezone s = new timezone();
		s.useMemory(getPointer());
		write();
		s.read();
		return s;
	}
	public static class ByReference extends timezone implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends timezone implements com.sun.jna.Structure.ByValue {}
}