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

}
