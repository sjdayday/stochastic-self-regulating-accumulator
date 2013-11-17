/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


public class SquaredErrorScoreKeeper extends ScoreKeeper
{

	private List<TrialScoreKeeper> trialScoreKeepers;
	private List<TrialResult> consolidatedTrialResults;
	public SquaredErrorScoreKeeper(double[][] subjectData, double weight)
	{
		super(subjectData, weight); 
	}
	protected void initTotals()
	{
		totalScore = 0;
	}
	protected void calculateTotalScore(List<TrialResult> results, int i)
	{
		totalScore += scoreTrial(i, results.get(i));
	}
	protected void rebuildBestPoints()
	{
		bestPoint = scorePoints.first(); 
		bestScorePoints = new ArrayList<ScorePoint>(); 
		Iterator<ScorePoint> iterator = scorePoints.iterator();
		int limit = (scorePoints.size() < SraParameters.BEST_SCORE_POINTS_SIZE) ? scorePoints.size() : SraParameters.BEST_SCORE_POINTS_SIZE; 
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
	public void scoreOnePaganModelRun(List<TrialResult> results) 
	{
		consolidatedTrialResults = results; 
	}
	
	public List<TrialResult> getConsolidatedTrialResults()
	{
		return consolidatedTrialResults;
	}

}
