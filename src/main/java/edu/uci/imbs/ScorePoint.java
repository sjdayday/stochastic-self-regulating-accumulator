package edu.uci.imbs;

public class ScorePoint
{
	public double score;
	public ParameterSource parameterSource;

	public ScorePoint(double sumSquaredError, ParameterSource parameterSource)
	{
		score = sumSquaredError; 
		this.parameterSource = parameterSource; 
	}
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append("Score="); 
		sb.append(score); 
//		sb.append(", K="); 
//		sb.append(parameterSource.getParameter(0)); 
//		sb.append(", Tau="); 
//		sb.append(parameterSource.getParameter(1)); 
//		sb.append(", Kappa="); 
//		sb.append(parameterSource.getParameter(2)); 
//		sb.append(", Lambda="); 
//		sb.append(parameterSource.getParameter(3)); 
		return sb.toString(); 
	}
}
