/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

public class PaganParameterSource extends FixedParameterSource
{

	public static double K;
	public static double TAU;
	public static double KAPPA;
	public static double LAMBDA;

	public PaganParameterSource(double[] parameters)
	{
		super(parameters);
		if (parameters.length != 4) throw new IllegalArgumentException("Pagan Model requires exactly four parameters:  K, Tau, Kappa, Lambda."); 
	}

	public PaganParameterSource()
	{
		super(new double[]{K, TAU, KAPPA, LAMBDA});
	}

	public double getK()
	{
		return super.getParameter(0);
	}

	public double getTau()
	{
		return super.getParameter(1);
	}

	public double getKappa()
	{
		return super.getParameter(2);
	}

	public double getLambda()
	{
		return super.getParameter(3);
	}

}
