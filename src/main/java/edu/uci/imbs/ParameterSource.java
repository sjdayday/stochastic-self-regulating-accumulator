/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

public interface ParameterSource extends Comparable<Object>
{

	double getParameter(int index);

	double[] getParameters(); 
}
