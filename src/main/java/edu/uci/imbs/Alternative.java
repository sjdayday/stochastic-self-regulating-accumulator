/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

public enum Alternative {
	A { public String toString() { return "A";}
		public int arrayPosition() { return 0; }
		public int value() { return 1; }
		public Alternative other() { return Alternative.B;} },
	B { public String toString() { return "B";}
		public int arrayPosition() { return 1; } 
		public int value() { return 0; }
		public Alternative other() { return Alternative.A;} };

	public abstract int arrayPosition();
	public abstract int value();
	public abstract Alternative other(); 
}
