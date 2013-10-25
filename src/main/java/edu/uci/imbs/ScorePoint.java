package edu.uci.imbs;

import org.grayleaves.utility.ParameterPoint;

public class ScorePoint implements Comparable<Object>
{
	public double score;
	public ParameterSource parameterSource;
	private ParameterPoint parameterPoint;

	public ScorePoint(double sumSquaredError, ParameterSource parameterSource, ParameterPoint parameterPoint)
	{
		score = sumSquaredError; 
		this.parameterSource = parameterSource; 
		this.parameterPoint = parameterPoint; 
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append("Score="); 
		sb.append(score); 
		return sb.toString(); 
	}
	public ParameterPoint getParameterPoint()
	{
		return parameterPoint;
	}

	@Override
	public int compareTo(Object obj)
	{
		if (obj == null) throw new NullPointerException("ScorePoint cannot be compared to null.");
		if ((this.getClass() != obj.getClass())) throw new ClassCastException("ScorePoint must be compared to another ScorePoint; was"+obj.getClass().getName());
		ScorePoint otherPoint = (ScorePoint) obj; 
		if (score < otherPoint.score) return -1; 
		else if (score > otherPoint.score) return 1; 
		else return parameterSource.compareTo(otherPoint.parameterSource); 
	}
	@Override
	public boolean equals(Object arg)
	{
		if (arg == null) return false;
		if (this.getClass() != arg.getClass()) return false;
		if (this.compareTo(arg) == 0) return true;
		else return false; 
	}
	@Override
	public int hashCode()
	{
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, score);
		double[] doubles = parameterSource.getParameters(); 
		for (int i = 0; i < doubles.length; i++)
		{
			result = HashCodeUtil.hash(result, doubles[i]);
		}
		return result;
	}

}
