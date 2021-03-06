package libusbone;
/**
 * <i>native declaration : /usr/include/bits/pthreadtypes.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class pthread_rwlock_t extends com.sun.jna.Union {
	/// C type : __data_struct
	public __data_struct __data;
	/// C type : char[32]
	public byte[] __size = new byte[(32)];
	public int __align;
	/// <i>native declaration : /usr/include/bits/pthreadtypes.h:169</i>
	public static class __data_struct extends com.sun.jna.Structure {
		public int __lock;
		public int __nr_readers;
		public int __readers_wakeup;
		public int __writer_wakeup;
		public int __nr_readers_queued;
		public int __nr_writers_queued;
		/**
		 * FLAGS must stay at this position in the structure to maintain<br>
		 * binary compatibility.
		 */
		public byte __flags;
		public byte __shared;
		public byte __pad1;
		public byte __pad2;
		public int __writer;
		public __data_struct() {
			super();
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
		protected __data_struct newInstance() {
			__data_struct s = new __data_struct();
			s.useMemory(getPointer());
			write();
			s.read();
			return s;
		}
		public static class ByReference extends __data_struct implements com.sun.jna.Structure.ByReference {}
		public static class ByValue extends __data_struct implements com.sun.jna.Structure.ByValue {}
	}
	public pthread_rwlock_t() {
		super();
	}
	public pthread_rwlock_t(int __align) {
		super();
		this.__align = __align;
		setType(java.lang.Integer.TYPE);
	}
	/// @param __data C type : __data_struct
	public pthread_rwlock_t(__data_struct __data) {
		super();
		this.__data = __data;
		setType(__data_struct.class);
	}
	/// @param __size C type : char[32]
	public pthread_rwlock_t(byte __size[]) {
		super();
		if (__size.length != this.__size.length) 
			throw new java.lang.IllegalArgumentException("Wrong array size !");
		this.__size = __size;
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
	protected pthread_rwlock_t newInstance() {
		pthread_rwlock_t s = new pthread_rwlock_t();
		s.useMemory(getPointer());
		write();
		s.read();
		return s;
	}
	public static class ByReference extends pthread_rwlock_t implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends pthread_rwlock_t implements com.sun.jna.Structure.ByValue {}
}
