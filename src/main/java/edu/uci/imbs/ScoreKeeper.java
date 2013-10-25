package edu.uci.imbs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.grayleaves.utility.ParameterPoint;

public class ScoreKeeper
{

	private static final int TRAINING_TRIALS = 100;
	public static int BEST_SCORE_POINTS_SIZE = 50;
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
	private ParameterPoint parameterPoint;
	private List<ScorePoint> bestScorePoints;
	private SortedSet<ScorePoint> scorePoints;


	public static void resetForTesting()
	{
		BEST_SCORE_POINTS_SIZE = 1; 
	}
	public ScoreKeeper(double[][] subjectData, double weight)
	{
		this.subjectData = subjectData; 
		this.weight = weight; 
		bestScorePoints = new ArrayList<ScorePoint>(); 
		scorePoints = new TreeSet<ScorePoint>(); 
	}

	public double score(List<TrialResult> results, ParameterSource parameterSource, ParameterPoint parameterPoint)
	{
		this.parameterSource = parameterSource; 
		this.parameterPoint = parameterPoint; 
		sumSquaredError = 0; 
		for (int i = 0; i < TRAINING_TRIALS; i++)
		{
			sumSquaredError += scoreTrial(i, results.get(i)); 
		}
		comparePoints();
		smoothSearchDepths(results); 
		return sumSquaredError;
	}

	private void smoothSearchDepths(List<TrialResult> results)
	{
		double[] inputSearchProportions = new double[results.size()]; 
		for (int i = 0; i < inputSearchProportions.length; i++)
		{
			inputSearchProportions[i] = results.get(i).searchProportion; 
		}
		PaganFilter filter = new PaganFilter(50, .05); //TODO make window and lambda parameters
		double[] smoothedSearchProportions = filter.filter(inputSearchProportions); 
		for (int i = 0; i < results.size(); i++)
		{
			results.get(i).smoothedSearchProportion = smoothedSearchProportions[i]; 
		}
	}
	protected void comparePoints()
	{
		currentPoint = new ScorePoint(sumSquaredError, this.parameterSource, this.parameterPoint);
		scorePoints.add(currentPoint); 
		rebuildBestPoints(); 
	}
	private void rebuildBestPoints()
	{
		bestPoint = scorePoints.first(); 
		bestScorePoints = new ArrayList<ScorePoint>(); 
		Iterator<ScorePoint> iterator = scorePoints.iterator();
		int limit = (scorePoints.size() < BEST_SCORE_POINTS_SIZE) ? scorePoints.size() : BEST_SCORE_POINTS_SIZE; 
		for (int i = 0; i < limit; i++)
		{
			bestScorePoints.add(iterator.next()); 
		}
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

	public List<ScorePoint> getBestScorePoints()
	{
		return bestScorePoints;
	}


}
