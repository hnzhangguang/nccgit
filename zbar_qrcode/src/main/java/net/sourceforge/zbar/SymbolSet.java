package net.sourceforge.zbar;

import java.util.AbstractCollection;
import java.util.Iterator;


public class SymbolSet
	  extends AbstractCollection<Symbol> {
	static {
		System.loadLibrary("zbarjni");
		init();
	}
	
	private long peer;
	
	
	SymbolSet(long peer) {
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
	
	public Iterator<Symbol> iterator() {
		long sym = firstSymbol(this.peer);
		if (sym == 0L) {
			return new SymbolIterator(null);
		}
		return new SymbolIterator(new Symbol(sym));
	}
	
	private native void destroy(long paramLong);
	
	public native int size();
	
	private native long firstSymbol(long paramLong);
}
