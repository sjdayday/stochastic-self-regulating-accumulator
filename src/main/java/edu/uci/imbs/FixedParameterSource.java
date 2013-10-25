/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

public class FixedParameterSource implements ParameterSource {

	private double[] parameters;

	public FixedParameterSource(double[] parameters) {
		if ((parameters == null) || (parameters.length == 0)) throw new IllegalArgumentException("FixedParameterSource must be initialized with an array of at least one parameter(s) of type double."); 
		this.parameters = parameters; 
	}

	public double getParameter(int index) {
		if ((index < 0 ) || (index >= parameters.length)) throw new IllegalArgumentException("FixedParameterSource.getParameter: argument must be from 0 to "+(parameters.length-1)+"; was "+index); 
		return parameters[index];
	}
	@Override
	public boolean equals(Object arg)
	{
		if (arg == null) return false;
		if (this.getClass() != arg.getClass()) return false;
		if (this.compareTo(arg) == 0) return true;
		else return false; 
//		FixedParameterSource otherSource = (FixedParameterSource) arg;  
//		if (parameters.length != otherSource.parameters.length) return false; 
//		for (int i = 0; i < parameters.length; i++)
//		{
//			if (parameters[i] != otherSource.parameters[i]) return false;
//		}
//		return true; 
	}
	@Override
	public int hashCode()
	{
		return HashCodeUtil.hash(27, parameters); 
	}

	@Override
	public int compareTo(Object obj)
	{
		if (obj == null) throw new NullPointerException("FixedParameterSource cannot be compared to null.");
		if ((this.getClass() != obj.getClass())) throw new ClassCastException("FixedParameterSource must be compared to another FixedParameterSource; was"+obj.getClass().getName());
		FixedParameterSource otherSource = (FixedParameterSource) obj;  
		if (parameters.length < otherSource.parameters.length) return -1; 
		if (parameters.length > otherSource.parameters.length) return 1; 
		int compare = 0; 
		for (int i = 0; i < parameters.length; i++)
		{
			compare = ((Double) parameters[i]).compareTo(otherSource.parameters[i]);
			if (!(compare == 0)) return compare; 
		}
		return compare;
	}

	@Override
	public double[] getParameters()
	{
		return parameters;
	}
}
