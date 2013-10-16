package edu.uci.imbs;

import java.util.List;

public class ScoreKeeper
{

	private double[][] subjectData;
	private double subjectDataAlternative;
	private double subjectDataCue;
	private double trialAlternative;
	private double trialCue;
	private double deltaAlternative;
	private double squaredError;
	private double deltaCue;
	private double weight;
	private double sumSquaredError;
	private ParameterSource parameterSource;
	private ScorePoint currentPoint;
	private ScorePoint bestPoint;

	public ScoreKeeper(double[][] subjectData, double weight)
	{
		this.subjectData = subjectData; 
		this.weight = weight; 
	}

	public double score(List<TrialResult> results, ParameterSource parameterSource)
	{
		this.parameterSource = parameterSource; 
		sumSquaredError = 0; 
		for (int i = 0; i < subjectData.length; i++)
		{
			sumSquaredError += scoreTrial(i, results.get(i)); 
		}
		comparePoints(); 
		return sumSquaredError;
	}

	protected void comparePoints()
	{
		currentPoint = new ScorePoint(sumSquaredError, this.parameterSource);
		if (bestPoint == null) bestPoint = currentPoint; 
		bestPoint = (currentPoint.score < bestPoint.score) ? currentPoint : bestPoint; 
	}
	private double scoreTrial(int i, TrialResult trialResult)
	{
		squaredError = 0; 
		subjectDataAlternative = subjectData[i][0]; 
		subjectDataCue = subjectData[i][1]; 
		trialAlternative = (double) trialResult.choice; 
		trialCue = (double) trialResult.searchDepth;
		deltaAlternative = trialAlternative - subjectDataAlternative; 
		deltaCue = trialCue - subjectDataCue; 
		squaredError = (weight*(deltaAlternative*deltaAlternative))+(deltaCue*deltaCue); 
		return squaredError;
	}

	public ParameterSource getCurrentParameterSource()
	{
		return parameterSource;
	}

	public ScorePoint getCurrentPoint()
	{
		return currentPoint;
	}

	public ScorePoint getBestPoint()
	{
		return bestPoint;
	}

}
