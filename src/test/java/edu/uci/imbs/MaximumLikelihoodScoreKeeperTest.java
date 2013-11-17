/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */
package edu.uci.imbs;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.grayleaves.utility.ParameterPoint;
import org.junit.Before;
import org.junit.Test;

public class MaximumLikelihoodScoreKeeperTest
{

	private static final PaganParameterSource PAGAN_PARAMETER_SOURCE = new PaganParameterSource(new double[]{3.7, 2.67, 1.55, 0.28});
	private Experiment experiment;
	private MaximumLikelihoodScoreKeeper scoreKeeper;
	private Model model;
	private double[][] subjectData;
	private List<TrialResult> results;
	private ScorePoint point;
	private ScorePoint bestPoint;
	private ParameterPoint parameterPoint;
	private static final String SLASH = System.getProperty("file.separator"); 
	@Before
	public void setUp() throws Exception
	{
		experiment = new Experiment(new double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
		subjectData = experiment.getSubjectData(); 
		scoreKeeper = new MaximumLikelihoodScoreKeeper(subjectData, 1); 
		parameterPoint = new TestingParameterPoint(1); 
		SraParameters.resetForTesting(); 
		SraParameters.TRAINING_TRIALS = 3; 
	}
	@Test
	public void verifyMultipleRunsAccumulatedForAllTrials() throws Exception
	{
		int size = scoreKeeper.getTrialScoreKeepers().size();
		assertEquals(0, scoreKeeper.getTrialScoreKeepers().get(0).getTrialsScored());
		assertEquals(200, size); 
		scoreKeeper.scoreOnePaganModelRun(runModel(PAGAN_PARAMETER_SOURCE));
		assertEquals(1, scoreKeeper.getTrialScoreKeepers().get(0).getTrialsScored());
		scoreKeeper.scoreOnePaganModelRun(runModel(PAGAN_PARAMETER_SOURCE));
		assertEquals(2, scoreKeeper.getTrialScoreKeepers().get(0).getTrialsScored());
		assertEquals("all have scored 2 runs",2, scoreKeeper.getTrialScoreKeepers().get(size-1).getTrialsScored());
	}
	@Test
	public void verifyScoresLogLikelihoodAsLogOfProductOfAllTrialProportionsMatched() throws Exception
	{
		scoreKeeper = new TestingMaximumLikelihoodScoreKeeper(subjectData, 1); 
		List<TrialResult> results = buildResultListWithProportionMatched(.5, .4, .3); 
		scoreKeeper.score(results, null, parameterPoint); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(-2.8134107, point.score, .0000001);
	}
	protected List<TrialResult> buildResultListWithProportionMatched(double d, double e, double f)
	{
		List<TrialResult> results = new ArrayList<TrialResult>();  
		results.add(buildResult(d)); 
		results.add(buildResult(e)); 
		results.add(buildResult(f));
		return results;
	}
	private TrialResult buildResult(double proportionMatched)
	{
		TrialResult result = new TrialResult(); 
		result.proportionMatched = proportionMatched; 
		return result;
	}
	@Test
	public void verifyConsolidatesMultipleResultsListsToOneList() throws Exception
	{
		subjectData = buildSubjectData(); 
		scoreKeeper = new MaximumLikelihoodScoreKeeper(subjectData, 1); 
		List<TrialResult> results = buildResultListWithCuesAndSearchDepths(1, 2, 1, 3, 0, 4);
		scoreKeeper.scoreOnePaganModelRun(results); 
		checkProportions(1d, 0d, 0d); 
		results = buildResultListWithCuesAndSearchDepths(1, 2, 0, 3, 0, 4);
		scoreKeeper.scoreOnePaganModelRun(results); 
		checkProportions(1d, .5d, 0d); 
		results = buildResultListWithCuesAndSearchDepths(1, 2, 0, 3, 1, 4);
		scoreKeeper.scoreOnePaganModelRun(results); 
		checkProportions(1d, .666d, .333d); 
		List<TrialResult> finalResults = scoreKeeper.getConsolidatedTrialResults(); 
		checkResult(finalResults, 0, 1d, 1, 2); 
		checkResult(finalResults, 1, .666d, 0, 3); 
		checkResult(finalResults, 2, .333d, 0, 4); 
	}
	
	@Test
	public void verifyTracksHighestScoreAndBestScoresAcrossMultipleResults() throws Exception
	{
		subjectData = buildSubjectData(); 
		scoreKeeper = new TestingMaximumLikelihoodScoreKeeper(subjectData, 1); 
		results = buildResultListWithProportionMatched(.5, .4, .3); 
		scoreKeeper.score(results, null, parameterPoint); 
		bestPoint = scoreKeeper.getBestPoint(); 
		assertEquals(-2.8134107, bestPoint.score, .0000001);
		results = buildResultListWithProportionMatched(.5, .4, .1); 
		scoreKeeper.score(results, null, parameterPoint); 
		bestPoint = scoreKeeper.getBestPoint(); 
		assertEquals("point score is -3.9120230, but not best", 
				-2.8134107, bestPoint.score, .0000001);
		results = buildResultListWithProportionMatched(.5, .4, .4); 
		scoreKeeper.score(results, null, parameterPoint); 
		bestPoint = scoreKeeper.getBestPoint(); 
		assertEquals(-2.5257286, bestPoint.score, .0000001);
		assertEquals(3, scoreKeeper.getBestScorePoints().size()); 
		assertEquals(-2.5257286, scoreKeeper.getBestScorePoints().get(0).score, .0000001); 
		assertEquals(-2.8134107, scoreKeeper.getBestScorePoints().get(1).score, .0000001); 
		assertEquals(-3.9120230, scoreKeeper.getBestScorePoints().get(2).score, .0000001); 
	}
	private void checkResult(List<TrialResult> finalResults, int i, double proportionMatched, int choice, int searchDepth)
	{
		assertEquals(proportionMatched, finalResults.get(i).proportionMatched, .001);
		assertEquals(choice, finalResults.get(i).choice);
		assertEquals(searchDepth, finalResults.get(i).searchDepth);
	}
	private void checkProportions(double d, double e, double f)
	{
		assertEquals(d, scoreKeeper.getTrialScoreKeepers().get(0).proportionMatched, .001); 
		assertEquals(e, scoreKeeper.getTrialScoreKeepers().get(1).proportionMatched, .001); 
		assertEquals(f, scoreKeeper.getTrialScoreKeepers().get(2).proportionMatched, .001); 
	}
	private List<TrialResult> buildResultListWithCuesAndSearchDepths(int i, int j, int k, int l,
			int m, int n)
	{
		List<TrialResult> results = new ArrayList<TrialResult>(); 
		results.add(buildOneResult(i, j)); 
		results.add(buildOneResult(k, l)); 
		results.add(buildOneResult(m, n)); 
		return results; 
	}
	protected TrialResult buildOneResult(int choice, int cue)
	{
		TrialResult result = new TrialResult(); 
		result.choice = choice;
		result.searchDepth = cue; 
		return result; 
	}
	private double[][] buildSubjectData()
	{
		double[][] test = new double[][]{new double[]{1d, 2d}, new double[]{0d, 3d}, new double[]{1d, 4d}};
		return test;

	}
	protected List<TrialResult> runModel(PaganParameterSource paganParameterSource)
	{
		model = new PaganModel(true, paganParameterSource); 
		model.participate(experiment); 
		model.run(); 
		List<TrialResult> results = model.getResults();
		return results;
	}
	private class TestingParameterPoint extends ParameterPoint
	{
		private int point;

		public TestingParameterPoint(int point)
		{
			this.point = point; 
		}
		@Override
		public String toString()
		{
			return point+"";
		}
	}
	private class TestingMaximumLikelihoodScoreKeeper extends MaximumLikelihoodScoreKeeper
	{
		public TestingMaximumLikelihoodScoreKeeper(double[][] subjectData, double weight)
		{
			super(subjectData, weight);
		}
		@Override
		protected void smoothSearchDepths(List<TrialResult> results)
		{
		}
		
		
	}
}