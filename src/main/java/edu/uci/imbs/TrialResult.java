/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

public class TrialResult {
	public int choice; 
	public int searchDepth; 
	public double k_l_store; 
	public double k_r_store;
	public double searchProportion;
	public double smoothedSearchProportion;
	public double proportionMatched;
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append(choice);
		sb.append('\t');
		sb.append(searchDepth);
		sb.append('\t');
		sb.append(searchProportion);
		sb.append('\t');
		sb.append(smoothedSearchProportion);
		sb.append('\t');
		sb.append(proportionMatched);
		
		return sb.toString();
	}
	@Override
	public boolean equals(Object arg)
	{
		if (arg == null) return false;
		if (this.getClass() != arg.getClass()) return false;
		TrialResult result = (TrialResult) arg;
		if ((this.choice == result.choice ) && (this.searchDepth == result.searchDepth)) return true;
		else return false; 
	}
	@Override
	public int hashCode()
	{
		int hash = HashCodeUtil.hash(27, choice); 
		hash = HashCodeUtil.hash(hash, searchDepth); 
		return hash; 
	}
}
