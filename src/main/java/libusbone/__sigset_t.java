package libusbone;
/**
 * <i>native declaration : /usr/include/bits/sigset.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class __sigset_t extends com.sun.jna.Structure {
	/// C type : unsigned long int[(1024 / (8 * sizeof(unsigned long int)))]
	public int[] __val = new int[(1024 / (8 * 4))];
	public __sigset_t() {
		super();
	}
	/// @param __val C type : unsigned long int[(1024 / (8 * sizeof(unsigned long int)))]
	public __sigset_t(int __val[]) {
		super();
		if (__val.length != this.__val.length) 
			throw new java.lang.IllegalArgumentException("Wrong array size !");
		this.__val = __val;
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
	protected __sigset_t newInstance() {
		__sigset_t s = new __sigset_t();
		s.useMemory(getPointer());
		write();
		s.read();
		return s;
	}
	public static class ByReference extends __sigset_t implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends __sigset_t implements com.sun.jna.Structure.ByValue {}
}
