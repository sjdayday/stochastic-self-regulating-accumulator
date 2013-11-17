/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class MaximumLikelihoodScoreKeeper extends ScoreKeeper
{
	private static Logger logger = Logger.getLogger(MaximumLikelihoodScoreKeeper.class);
	private List<TrialScoreKeeper> trialScoreKeepers;
	private double totalProduct;
	public MaximumLikelihoodScoreKeeper(double[][] subjectData, double weight)
	{
		super(subjectData, weight); 
		buildTrialScoreKeepers(subjectData);
	}
	protected void initTotals()
	{
		totalScore = 0;
		totalProduct = 1; 
	}
	protected void calculateTotalScore(List<TrialResult> results, int i)
	{
		totalProduct = totalProduct * results.get(i).proportionMatched; 
		totalScore = StrictMath.log(totalProduct); 
		logger.info(i+" product: "+totalProduct+" log: "+totalScore+" matched: "+results.get(i).proportionMatched);
	}
	protected void rebuildBestPoints()
	{
		bestPoint = scorePoints.last(); 
		bestScorePoints = new ArrayList<ScorePoint>(); 
		Iterator<ScorePoint> iterator = ((TreeSet<ScorePoint>) scorePoints).descendingIterator();
		int limit = (scorePoints.size() < SraParameters.BEST_SCORE_POINTS_SIZE) ? scorePoints.size() : SraParameters.BEST_SCORE_POINTS_SIZE; 
		for (int i = 0; i < limit; i++)
		{
			bestScorePoints.add(iterator.next()); 
		}
	}
	protected void buildTrialScoreKeepers(double[][] subjectData)
	{  
		trialScoreKeepers = new ArrayList<TrialScoreKeeper>(); 
//			for (int i = 0; i < 3; i++)
		for (int i = 0; i < subjectData.length; i++)
		{
			trialScoreKeepers.add(new TrialScoreKeeper(subjectData[i][0], subjectData[i][1])); 
		}
	}
	public List<TrialScoreKeeper> getTrialScoreKeepers()
	{
		return trialScoreKeepers;
	}
	public void scoreOnePaganModelRun(List<TrialResult> results)
	{
//			for (int i = 0; i < 3; i++)
		for (int i = 0; i < results.size(); i++)
		{
			trialScoreKeepers.get(i).score(results.get(i)); 
			if (i < 6) logger.debug(i+": "+trialScoreKeepers.get(i).printSummary());
		}
	}
	public List<TrialResult> getConsolidatedTrialResults()
	{
		List<TrialResult> finalResults = new ArrayList<TrialResult>();
		for (TrialScoreKeeper tsk : trialScoreKeepers)
		{
			finalResults.add(tsk.getBestResult()); 
		}
		return finalResults;
	}


}
