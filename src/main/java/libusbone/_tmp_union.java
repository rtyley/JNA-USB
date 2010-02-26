package libusbone;
/**
 * <i>native declaration : /usr/include/limits.h:852</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class _tmp_union extends com.sun.jna.Union {
	/// C type : uint8_t[2]
	public byte[] b8 = new byte[(2)];
	public short b16;
	public _tmp_union() {
		super();
	}
	public _tmp_union(short b16) {
		super();
		this.b16 = b16;
		setType(java.lang.Short.TYPE);
	}
	/// @param b8 C type : uint8_t[2]
	public _tmp_union(byte b8[]) {
		super();
		if (b8.length != this.b8.length) 
			throw new java.lang.IllegalArgumentException("Wrong array size !");
		this.b8 = b8;
		setType(byte[].class);
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
	protected _tmp_union newInstance() {
		_tmp_union s = new _tmp_union();
		s.useMemory(getPointer());
		write();
		s.read();
		return s;
	}
	public static class ByReference extends _tmp_union implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends _tmp_union implements com.sun.jna.Structure.ByValue {}
}
