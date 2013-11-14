/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;

public class PaganModelTest {

	private Experiment experiment;
	private static final String SLASH = System.getProperty("file.separator");
	private PaganModel paganModel; 
	@Before
	public void setUp() throws Exception {
		experiment = new Experiment(new double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
		paganModel = new PaganModel(true, new FixedParameterSource(new double[]{3.7, 2.67, 1.55, 0.28})); 
	}

	@Test
	public void verifyModelCreated() throws Exception {
		Model model = new PaganModel(true, new FixedParameterSource(new double[]{.01, .02})); 
		model.participate(experiment); 
		assertEquals(.02, model.getParameterSource().getParameter(1), .001); 
		assertEquals(200, model.getExperiment().getTrials().size()); 
	}
	@Test
	public void verifyModelBehavior() throws Exception {
		Model model = new PaganModel(true, new FixedParameterSource(new double[]{3.7, 2.67, 1.55, 0.28})); 
		model.participate(experiment); 
		model.run(); 
		
		
		List<TrialResult> results = model.getResults(); 
//		for (TrialResult trialResult : results) {
//			System.out.println(trialResult.searchDepth);  //"\t"+trialResult.searchDepth
//		}
		assertEquals(200, results.size());
		assertEquals(0, results.get(0).choice);
		assertEquals(3, results.get(0).searchDepth);
		compare(readExpectedResults(), results ); 
	}
	
	private void compare(List<TrialResult> expectedResults,
			List<TrialResult> results) {
		TrialResult result = null;
		TrialResult expectedResult = null;
		for (int i = 0; i < results.size(); i++) {
			result = results.get(i);
			expectedResult = expectedResults.get(i);
			assertEquals("comparing choice: "+i, expectedResult.choice, result.choice );
			assertEquals("comparing search depth: "+i, expectedResult.searchDepth, result.searchDepth );
		}
	}

	private List<TrialResult> readExpectedResults() throws Exception {
		List<TrialResult> results = new ArrayList<TrialResult>();
		BufferedReader reader = new BufferedReader(new FileReader("data"+System.getProperty("file.separator")+"pagan_output.txt")); 
		TrialResult result = null; 
		String line = reader.readLine(); 
		StringTokenizer st = null; 
		while (line != null)
		{
			st = new StringTokenizer(line, "\t"); 
			result = new TrialResult(); 
			result.choice = new Integer(st.nextToken()); 
			result.searchDepth = new Integer(st.nextToken()); 
			results.add(result); 
			line = reader.readLine(); 
		}
		reader.close(); 
		return results;
	}

	@Test
	public void verifyModelInitialization() throws Exception {
		paganModel.participate(experiment); 
		assertEquals(3.7, paganModel.getK(), .001); 
		assertEquals(2.67, paganModel.getTau(), .001); 
		assertEquals(1.55, paganModel.getKappa(), .001); 
		assertEquals(0.28, paganModel.getLambda(), .001); 
		assertEquals(200, paganModel.numberTrials); 
		assertEquals(9, paganModel.numberCues); 
		assertTrue(paganModel.getFeedback());
//		System.out.println("ULP from 1: "+Math.ulp(1.0));
//		System.out.println("ULP from 100: "+Math.ulp(100.0));
//		System.out.println("ULP from -100: "+Math.ulp(-100.0));
	}
	@Test
	public void verifySearchProportionCalulatedForCueChosenOneIndexed() throws Exception
	{
		TrialResult result = new TrialResult(); 
		Trial trial = new Trial(new double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		trial.setCueProfile(Trial.BOTH_POSITIVE,
				Trial.BOTH_NEGATIVE,
				Trial.A_POSITIVE,
				Trial.B_POSITIVE,
				Trial.A_POSITIVE,
				Trial.BOTH_NEGATIVE,
				Trial.B_POSITIVE,
				Trial.BOTH_POSITIVE,
				Trial.B_POSITIVE); 
		result.searchDepth = 3;
		paganModel.calculateSearchProportion(trial, result);
		assertEquals(0, result.searchProportion, .001);
		result.searchDepth = 4;
		paganModel.calculateSearchProportion(trial, result);
		assertEquals(.166, result.searchProportion, .001);
		result.searchDepth = 5;
		paganModel.calculateSearchProportion(trial, result);
		assertEquals(.333, result.searchProportion, .001);
		result.searchDepth = 6;
		paganModel.calculateSearchProportion(trial, result);
		assertEquals(.5, result.searchProportion, .001);
		result.searchDepth = 7;
		paganModel.calculateSearchProportion(trial, result);
		assertEquals(.666, result.searchProportion, .001);
		result.searchDepth = 8;
		paganModel.calculateSearchProportion(trial, result);
		assertEquals(.833, result.searchProportion, .001);
	}
	@Test
	public void verifySearchProportionCalculatedWhenFirstDiscriminatingCueIsLastCue() throws Exception
	{
		TrialResult result = new TrialResult(); 
		Trial trial = new Trial(new double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		trial.setCueProfile(Trial.BOTH_POSITIVE,
				Trial.BOTH_NEGATIVE,
				Trial.BOTH_NEGATIVE,
				Trial.BOTH_NEGATIVE,
				Trial.BOTH_NEGATIVE,
				Trial.BOTH_NEGATIVE,
				Trial.BOTH_NEGATIVE,
				Trial.BOTH_NEGATIVE,
				Trial.B_POSITIVE); 
		result.searchDepth = 9;
		paganModel.calculateSearchProportion(trial, result);
		assertEquals(0, result.searchProportion, .001);
	}
}
