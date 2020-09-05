package net.sourceforge.zbar;


public class Image {
	static {
		//System.loadLibrary("zbarjni");
		init();
	}
	
	private long peer;
	private Object data;
	
	
	public Image() {
		this.peer = create();
	}
	
	
	public Image(int width, int height) {
		this();
		setSize(width, height);
	}
	
	
	public Image(int width, int height, String format) {
		this();
		setSize(width, height);
		setFormat(format);
	}
	
	
	public Image(String format) {
		this();
		setFormat(format);
	}
	
	
	Image(long peer) {
		this.peer = peer;
	}
	
	private static native void init();
	
	protected void finalize() {
		destroy();
	}
	
	public void destroy() {
		if (this.peer != 0L) {
			destroy(this.peer);
			this.peer = 0L;
		}
	}
	
	public Image convert(String format) {
		long newpeer = convert(this.peer, format);
		if (newpeer == 0L)
			return null;
		return new Image(newpeer);
	}
	
	public SymbolSet getSymbols() {
		return new SymbolSet(getSymbols(this.peer));
	}
	
	private native long create();
	
	private native void destroy(long paramLong);
	
	private native long convert(long paramLong, String paramString);
	
	public native String getFormat();
	
	public native void setFormat(String paramString);
	
	public native int getSequence();
	
	public native void setSequence(int paramInt);
	
	public native int getWidth();
	
	public native int getHeight();
	
	public native int[] getSize();
	
	public native void setSize(int[] paramArrayOfInt);
	
	public native void setSize(int paramInt1, int paramInt2);
	
	public native int[] getCrop();
	
	public native void setCrop(int[] paramArrayOfInt);
	
	public native void setCrop(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
	
	public native byte[] getData();
	
	public native void setData(byte[] paramArrayOfByte);
	
	public native void setData(int[] paramArrayOfInt);
	
	private native long getSymbols(long paramLong);
}
