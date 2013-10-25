package edu.uci.imbs;

public class PaganFilter extends Filter
{

	private int windowSize;
	private double lambdaSmoothing;
	private double[] exponentVector;
	private double[] filterVector;

	public PaganFilter(int windowSmoothing, double lambdaSmoothing)
	{
		super(); 
		this.windowSize = windowSmoothing; 
		this.lambdaSmoothing = lambdaSmoothing;
		buildExponentVector();
		buildFilter(); 
	}

	private void buildFilter()
	{
		double filterSum = 0; 
		filterVector = new double[windowSize]; 
		double filterElement = 0; 
		for (int i = 0; i < exponentVector.length; i++)
		{
			filterElement = Math.exp(exponentVector[i]); 
			filterVector[i] = filterElement; 
			filterSum += filterElement; 
		}
		for (int i = 0; i < filterVector.length; i++)
		{
			filterVector[i] = filterVector[i]/filterSum; 
		}
	}

	protected void buildExponentVector()
	{
		exponentVector = new double[windowSize];
		double windowElement = 0;
		for (int i = 0; i < exponentVector.length; i++)
		{
			windowElement = (i+1) - (((double) windowSize)/2); 
			exponentVector[i] = (windowElement*windowElement)*(-1)*lambdaSmoothing; 
		}
	}

	public double[] getExponentVector()
	{
		return exponentVector;
	}

	public double[] filter(double[] inputSearchDepths)
	{
		return filter(filterVector, vectorOfOnes(), inputSearchDepths);
	}

	private double[] vectorOfOnes()
	{
		double[] ones = new double[windowSize]; 
		for (int i = 0; i < ones.length; i++)
		{
			ones[i] = 1; 
		}
		return ones;
	}

}
