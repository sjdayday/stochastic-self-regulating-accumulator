/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.grayleaves.utility.ParameterPoint;

public abstract class ScoreKeeper
{
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ScoreKeeper.class);
	protected double[][] subjectData;
	protected double subjectDataAlternative;
	protected double subjectDataCue;
	protected double trialAlternative;
	protected double trialCue;
	protected double deltaAlternative;
	protected double squaredError;
	protected double deltaCue;
	protected double weight;
	protected double totalScore;
	protected ParameterSource parameterSource;
	protected ScorePoint currentPoint;
	protected ScorePoint bestPoint;
	protected ParameterPoint parameterPoint;
	protected List<ScorePoint> bestScorePoints;
	protected SortedSet<ScorePoint> scorePoints;

	public ScoreKeeper(double[][] subjectData, double weight)
	{
		this.subjectData = subjectData; 
		this.weight = weight; 
		bestScorePoints = new ArrayList<ScorePoint>(); 
		scorePoints = new TreeSet<ScorePoint>(); 
	}

	public void score(List<TrialResult> results, ParameterSource parameterSource, ParameterPoint parameterPoint)
	{
		this.parameterSource = parameterSource; 
		this.parameterPoint = parameterPoint; 
		initTotals(); 
		for (int i = 0; i < SraParameters.TRAINING_TRIALS; i++)
		{
			calculateTotalScore(results, i); 
		}
		comparePoints();
		smoothSearchDepths(results); 
	}

	protected void comparePoints()
	{
		currentPoint = new ScorePoint(totalScore, this.parameterSource, this.parameterPoint);
		scorePoints.add(currentPoint); 
		rebuildBestPoints(); 
	}
	protected abstract void initTotals();
	protected abstract void calculateTotalScore(List<TrialResult> results, int i); 
	protected abstract void rebuildBestPoints(); 
	public abstract void scoreOnePaganModelRun(List<TrialResult> results);
	public abstract List<TrialResult> getConsolidatedTrialResults();
	
	protected void smoothSearchDepths(List<TrialResult> results)
	{
		double[] inputSearchProportions = new double[results.size()]; 
		for (int i = 0; i < inputSearchProportions.length; i++)
		{
			inputSearchProportions[i] = results.get(i).searchProportion; 
		}
		PaganFilter filter = new PaganFilter(SraParameters.PAGAN_FILTER_WINDOW, SraParameters.PAGAN_FILTER_LAMBDA); 
		double[] smoothedSearchProportions = filter.filter(inputSearchProportions); 
		for (int i = 0; i < results.size(); i++)
		{
			results.get(i).smoothedSearchProportion = smoothedSearchProportions[i]; 
		}
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

	public List<ScorePoint> getBestScorePoints()
	{
		return bestScorePoints;
	}

}
