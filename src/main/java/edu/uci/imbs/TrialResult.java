/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

public class TrialResult {
	public int choice; 
	public int searchDepth; 
	public double k_l_store; 
	public double k_r_store;
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append(choice);
		sb.append('\t');
		sb.append(searchDepth);
		sb.append('\n');
		
		return sb.toString();
	}
}
