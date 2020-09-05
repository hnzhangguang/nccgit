package net.sourceforge.zbar;

import android.graphics.PointF;


public class Symbol {
	public static final int NONE = 0;
	public static final int PARTIAL = 1;
	public static final int EAN8 = 8;
	public static final int UPCE = 9;
	public static final int ISBN10 = 10;
	public static final int UPCA = 12;
	public static final int EAN13 = 13;
	public static final int ISBN13 = 14;
	public static final int I25 = 25;
	public static final int DATABAR = 34;
	public static final int DATABAR_EXP = 35;
	public static final int CODABAR = 38;
	public static final int CODE39 = 39;
	public static final int PDF417 = 57;
	public static final int QRCODE = 64;
	public static final int CODE93 = 93;
	public static final int CODE128 = 128;
	
	static {
		System.loadLibrary("zbarjni");
		init();
	}
	
	private long peer;
	private int type;
	
	
	Symbol(long peer) {
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
	
	public int getType() {
		if (this.type == 0) {
			this.type = getType(this.peer);
		}
		return this.type;
	}
	
	public int getLocationSize() {
		return getLocationSize(this.peer);
	}
	
	public int[] getBounds() {
		int n = getLocationSize(this.peer);
		if (n <= 0) {
			return null;
		}
		
		int[] bounds = new int[4];
		int xmin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE;
		int ymin = Integer.MAX_VALUE;
		int ymax = Integer.MIN_VALUE;
		
		for (int i = 0; i < n; i++) {
			int x = getLocationX(this.peer, i);
			if (xmin > x) xmin = x;
			if (xmax < x) xmax = x;
			
			int y = getLocationY(this.peer, i);
			if (ymin > y) ymin = y;
			if (ymax < y) ymax = y;
		}
		bounds[0] = xmin;
		bounds[1] = ymin;
		bounds[2] = xmax - xmin;
		bounds[3] = ymax - ymin;
		return bounds;
	}
	
	public int[] getLocationPoint(int idx) {
		int[] p = new int[2];
		p[0] = getLocationX(this.peer, idx);
		p[1] = getLocationY(this.peer, idx);
		return p;
	}
	
	public PointF[] getLocationPoints() {
		int locationSize = getLocationSize(this.peer);
		PointF[] pointArr = new PointF[locationSize];
		for (int pointIndex = 0; pointIndex < locationSize; pointIndex++) {
			pointArr[pointIndex] = new PointF(getLocationX(this.peer, pointIndex), getLocationY(this.peer, pointIndex));
		}
		return pointArr;
	}
	
	public SymbolSet getComponents() {
		return new SymbolSet(getComponents(this.peer));
	}
	
	private native void destroy(long paramLong);
	
	private native int getType(long paramLong);
	
	public native int getConfigMask();
	
	public native int getModifierMask();
	
	public native String getData();
	
	public native byte[] getDataBytes();
	
	public native int getQuality();
	
	public native int getCount();
	
	private native int getLocationSize(long paramLong);
	
	private native int getLocationX(long paramLong, int paramInt);
	
	private native int getLocationY(long paramLong, int paramInt);
	
	public native int getOrientation();
	
	private native long getComponents(long paramLong);
	
	native long next();
}
