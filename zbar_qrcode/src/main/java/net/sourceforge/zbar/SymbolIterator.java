package net.sourceforge.zbar;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class SymbolIterator
	  extends Object
	  implements Iterator<Symbol> {
	private Symbol current;
	
	SymbolIterator(Symbol first) {
		this.current = first;
	}
	
	
	public boolean hasNext() {
		return (this.current != null);
	}
	
	
	public Symbol next() {
		if (this.current == null) {
			throw new NoSuchElementException("access past end of SymbolIterator");
		}
		
		Symbol result = this.current;
		long sym = this.current.next();
		if (sym != 0L) {
			this.current = new Symbol(sym);
		} else {
			this.current = null;
		}
		return result;
	}
	
	
	public void remove() {
		throw new UnsupportedOperationException("SymbolIterator is immutable");
	}
}
