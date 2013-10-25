package edu.uci.imbs;

import static org.junit.Assert.*;

import java.util.List;

import org.grayleaves.utility.ParameterPoint;
import org.junit.Before;
import org.junit.Test;

public class ScoreKeeperTest
{

	private static final PaganParameterSource PAGAN_PARAMETER_SOURCE = new PaganParameterSource(new double[]{3.7, 2.67, 1.55, 0.28});
	private Experiment experiment;
	private ScoreKeeper scoreKeeper;
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
		experiment = new Experiment(new Double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
		subjectData = experiment.getSubjectData(); 
		scoreKeeper = new ScoreKeeper(subjectData, 1); 
		parameterPoint = new TestingParameterPoint(1); 
		ScoreKeeper.resetForTesting(); 
	}
	@Test
	public void verifyAssociatesParameterPointWithScorePoint() throws Exception
	{
		List<TrialResult> results = runModel(PAGAN_PARAMETER_SOURCE); 
		scoreKeeper.score(results, model.getParameterSource(), parameterPoint); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals("1", point.getParameterPoint().toString());
	}
	@Test
	public void verifyModelWithBaselineParametersScoresCorrectlyAgainstFirst100CasesOfSubjectData() throws Exception
	{
		List<TrialResult> results = runModel(PAGAN_PARAMETER_SOURCE); 
		assertEquals(168, scoreKeeper.score(results, model.getParameterSource(), parameterPoint), .001); 
		assertEquals(new PaganParameterSource(new double[]{3.7, 2.67, 1.55, 0.28}), 
				scoreKeeper.getCurrentParameterSource()); 
		assertFalse("different point won't match",(new PaganParameterSource(new double[]{3.6, 2.67, 1.55, 0.28})).equals( 
				scoreKeeper.getCurrentParameterSource())); 
	}
	@Test
	public void verifyResultsCollectedInCurrentPoint() throws Exception
	{
		List<TrialResult> results = runModel(PAGAN_PARAMETER_SOURCE); 
		scoreKeeper.score(results, model.getParameterSource(), parameterPoint); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(168, point.score, .001);
		assertEquals(PAGAN_PARAMETER_SOURCE, point.parameterSource);
	}
	@Test
	public void verifyTracksPointsInDescendingOrderUpToConfigurableLimit() throws Exception
	{
		ScoreKeeper.BEST_SCORE_POINTS_SIZE = 3; 
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
	@Test
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
}