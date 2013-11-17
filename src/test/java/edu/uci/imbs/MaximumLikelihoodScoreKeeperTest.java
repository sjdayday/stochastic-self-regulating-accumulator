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
//		SraParameters.STOCHASTIC_RUNS_PER_PARAMETER_POINT = 5; 
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
		List<TrialResult> results = new ArrayList<TrialResult>();  
		results.add(buildResult(.5)); 
		results.add(buildResult(.4)); 
		results.add(buildResult(.3)); 
		scoreKeeper.score(results, null, parameterPoint); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(-2.8134107, point.score, .0000001);
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
		List<TrialResult> results = buildResultList(1, 2, 1, 3, 0, 4);
		scoreKeeper.scoreOnePaganModelRun(results); 
		checkProportions(1d, 0d, 0d); 
		results = buildResultList(1, 2, 0, 3, 0, 4);
		scoreKeeper.scoreOnePaganModelRun(results); 
		checkProportions(1d, .5d, 0d); 
		results = buildResultList(1, 2, 0, 3, 1, 4);
		scoreKeeper.scoreOnePaganModelRun(results); 
		checkProportions(1d, .666d, .333d); 
		List<TrialResult> finalResults = scoreKeeper.getConsolidatedTrialResults(); 
		checkResult(finalResults, 0, 1d, 1, 2); 
		checkResult(finalResults, 1, .666d, 0, 3); 
		checkResult(finalResults, 2, .333d, 0, 4); 
	}
	private void checkResult(List<TrialResult> finalResults, int i, double d, int j, int k)
	{
		assertEquals(d, finalResults.get(i).proportionMatched, .001);
		assertEquals(j, finalResults.get(i).choice);
		assertEquals(k, finalResults.get(i).searchDepth);
	}
	private void checkProportions(double d, double e, double f)
	{
		assertEquals(d, scoreKeeper.getTrialScoreKeepers().get(0).proportionMatched, .001); 
		assertEquals(e, scoreKeeper.getTrialScoreKeepers().get(1).proportionMatched, .001); 
		assertEquals(f, scoreKeeper.getTrialScoreKeepers().get(2).proportionMatched, .001); 
	}
	private List<TrialResult> buildResultList(int i, int j, int k, int l,
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
//		for (int i = 0; i < 3; i++)
//		{
//				System.out.println(test[i][0]+" "+test[i][1]);
//		}
		return test;
//		subjectDataAlternative = subjectData[i][0]; 
//		subjectDataCue = subjectData[i][1]; 

	}
	@Test
	public void verifyPrintsSummaryOfTrialResultCounts() throws Exception
	{
//		buildAndScoreTrialResult(0, 2); 
//		buildAndScoreTrialResult(1, 3); 
//		buildAndScoreTrialResult(0, 1); 
//		buildAndScoreTrialResult(1, 3); 
//		buildAndScoreTrialResult(0, 2); 
//		assertEquals(
//				"Result: 0\t2\t0.0\t0.0\t0.0 Count: 2\n" +
//				"Result: 0\t1\t0.0\t0.0\t0.0 Count: 1\n" +
//				"Result: 1\t3\t0.0\t0.0\t0.0 Count: 2\n" +
//				"Best Result: 0\t2\t0.0\t0.0\t0.4 Count: 2", 
//				tsk.printSummary()); 
	}
//	protected void checkProportion(String comment, double proportionMatched)
//	{
//		assertEquals(comment, proportionMatched, newResult.proportionMatched, .001);
//	}
//	protected void checkResultFields(String comment, int choice, int cue, int count)
//	{
//		assertEquals(comment, choice, newResult.choice); 
//		assertEquals(comment, cue, newResult.searchDepth); 
//		assertEquals(count, (int) tsk.getCountsMap().get(newResult)); 
//	}
//	protected void buildAndScoreAndGetBestTrialResult(int choice, int cue)
//	{
//		buildAndScoreTrialResult(choice, cue); 
//		newResult = tsk.getBestResult(); 
//		
//	}
//	@Test
	public void verifyModelWithBaselineParametersScoresCorrectlyAgainstFirst100CasesOfSubjectData() throws Exception
	{
		List<TrialResult> results = runModel(PAGAN_PARAMETER_SOURCE); 
		scoreKeeper.score(results, model.getParameterSource(), parameterPoint); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(168, point.score, .001);
		assertEquals(new PaganParameterSource(new double[]{3.7, 2.67, 1.55, 0.28}), 
				scoreKeeper.getCurrentParameterSource()); 
		assertFalse("different point won't match",(new PaganParameterSource(new double[]{3.6, 2.67, 1.55, 0.28})).equals( 
				scoreKeeper.getCurrentParameterSource())); 
	}
//	@Test
	public void verifyResultsCollectedInCurrentPoint() throws Exception
	{
		List<TrialResult> results = runModel(PAGAN_PARAMETER_SOURCE); 
		scoreKeeper.score(results, model.getParameterSource(), parameterPoint); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(168, point.score, .001);
		assertEquals(PAGAN_PARAMETER_SOURCE, point.parameterSource);
	}
//	@Test
	public void verifyTracksPointsInDescendingOrderUpToConfigurableLimit() throws Exception
	{
		SraParameters.BEST_SCORE_POINTS_SIZE = 3; 
		assertEquals(0, scoreKeeper.getBestScorePoints().size());
		runAndScorePoint(new double[]{2.0, 2.67, 1.55, 0.28}, 270); 
		assertEquals(1, scoreKeeper.getBestScorePoints().size());
		runAndScorePoint(new double[]{3.7, 2.67, 1.55, 0.28}, 168); 
		assertEquals(2, scoreKeeper.getBestScorePoints().size());
		assertEquals(168, scoreKeeper.getBestScorePoints().get(0).score, .001);
		assertEquals(270, scoreKeeper.getBestScorePoints().get(1).score, .001);
		runAndScorePoint(new double[]{2.5, 2.67, 1.55, 0.28}, 243); 
		assertEquals(3, scoreKeeper.getBestScorePoints().size());
		runAndScorePoint(new double[]{4.0, 2.67, 1.55, 0.28}, 237); 
		assertEquals(3, scoreKeeper.getBestScorePoints().size());
		assertEquals(168, scoreKeeper.getBestScorePoints().get(0).score, .001);
		assertEquals(237, scoreKeeper.getBestScorePoints().get(1).score, .001);
		assertEquals(243, scoreKeeper.getBestScorePoints().get(2).score, .001);
	}
	@Test
	public void verifyGeneratesSmoothedSearchProportions() throws Exception
	{
		List<TrialResult> results = runModel(PAGAN_PARAMETER_SOURCE); 
		assertEquals(0, results.get(100).smoothedSearchProportion, .001); 
		scoreKeeper.score(results, model.getParameterSource(), parameterPoint);
		assertEquals("some non-zero number...",.849, results.get(100).smoothedSearchProportion, .001); 
	}
	private void runAndScorePoint(double[] parameters, int score)
	{
		PaganParameterSource source = new PaganParameterSource(parameters); 
		results = runModel(source); 
		scoreKeeper.score(results, model.getParameterSource(), parameterPoint); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(score, point.score, .001);
	}
//	@Test
	public void verifyTracksHighestScoreAcrossMultipleResults() throws Exception
	{
		PaganParameterSource source1 = new PaganParameterSource(new double[]{2.0, 2.67, 1.55, 0.28}); 
		results = runModel(source1); 
		scoreKeeper.score(results, model.getParameterSource(), parameterPoint); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(270, point.score, .001);
		assertEquals(source1, point.parameterSource);
		bestPoint = scoreKeeper.getBestPoint(); 
		assertEquals(270, bestPoint.score, .001); 
		assertEquals(source1, bestPoint.parameterSource);
		results = runModel(PAGAN_PARAMETER_SOURCE); 
		scoreKeeper.score(results, model.getParameterSource(), parameterPoint); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(168, point.score, .001);
		assertEquals(PAGAN_PARAMETER_SOURCE, point.parameterSource);
		bestPoint = scoreKeeper.getBestPoint(); 
		assertEquals(168, bestPoint.score, .001); 
		assertEquals(PAGAN_PARAMETER_SOURCE, bestPoint.parameterSource);
		PaganParameterSource source2 = new PaganParameterSource(new double[]{4.0, 2.67, 1.55, 0.28}); 
		results = runModel(source2); 
		scoreKeeper.score(results, model.getParameterSource(), parameterPoint); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(237, point.score, .001);
		assertEquals(source2, point.parameterSource);
		bestPoint = scoreKeeper.getBestPoint(); 
		assertEquals(168, bestPoint.score, .001); 
		assertEquals(PAGAN_PARAMETER_SOURCE, bestPoint.parameterSource);
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