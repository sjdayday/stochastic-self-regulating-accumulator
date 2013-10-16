package edu.uci.imbs;

import static org.junit.Assert.*;

import java.util.List;

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
	private static final String SLASH = System.getProperty("file.separator"); 
	@Before
	public void setUp() throws Exception
	{
		experiment = new Experiment(new Double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
		subjectData = experiment.getSubjectData(); 
		scoreKeeper = new ScoreKeeper(subjectData, 1); 
	}
	
	@Test
	public void verifyModelWithBaselineParametersScoresCorrectlyAgainstSubjectData() throws Exception
	{
		List<TrialResult> results = runModel(PAGAN_PARAMETER_SOURCE); 
		assertEquals(463, scoreKeeper.score(results, model.getParameterSource()), .001); 
		assertEquals(new PaganParameterSource(new double[]{3.7, 2.67, 1.55, 0.28}), 
				scoreKeeper.getCurrentParameterSource()); 
		assertFalse("different point won't match",(new PaganParameterSource(new double[]{3.6, 2.67, 1.55, 0.28})).equals( 
				scoreKeeper.getCurrentParameterSource())); 
	}
	@Test
	public void verifyResultsCollectedInCurrentPoint() throws Exception
	{
		List<TrialResult> results = runModel(PAGAN_PARAMETER_SOURCE); 
		scoreKeeper.score(results, model.getParameterSource()); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(463, point.score, .001);
		assertEquals(PAGAN_PARAMETER_SOURCE, point.parameterSource);
	}
	
	@Test
	public void verifyTracksHighestScoreAcrossMultipleResults() throws Exception
	{
		PaganParameterSource source1 = new PaganParameterSource(new double[]{2.0, 2.67, 1.55, 0.28}); 
		results = runModel(source1); 
		scoreKeeper.score(results, model.getParameterSource()); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(615, point.score, .001);
		assertEquals(source1, point.parameterSource);
		bestPoint = scoreKeeper.getBestPoint(); 
		assertEquals(615, bestPoint.score, .001); 
		assertEquals(source1, bestPoint.parameterSource);
		results = runModel(PAGAN_PARAMETER_SOURCE); 
		scoreKeeper.score(results, model.getParameterSource()); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(463, point.score, .001);
		assertEquals(PAGAN_PARAMETER_SOURCE, point.parameterSource);
		bestPoint = scoreKeeper.getBestPoint(); 
		assertEquals(463, bestPoint.score, .001); 
		assertEquals(PAGAN_PARAMETER_SOURCE, bestPoint.parameterSource);
		PaganParameterSource source2 = new PaganParameterSource(new double[]{4.0, 2.67, 1.55, 0.28}); 
		results = runModel(source2); 
		scoreKeeper.score(results, model.getParameterSource()); 
		point = scoreKeeper.getCurrentPoint(); 
		assertEquals(534, point.score, .001);
		assertEquals(source2, point.parameterSource);
		bestPoint = scoreKeeper.getBestPoint(); 
		assertEquals(463, bestPoint.score, .001); 
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
}
