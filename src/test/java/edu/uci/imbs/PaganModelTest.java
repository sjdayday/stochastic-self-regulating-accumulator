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

	@Before
	public void setUp() throws Exception {
		experiment = new Experiment(new Double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		experiment.loadConfigurationData("data/exp2p1.mat");
	}

	@Test
	public void verifyModelCreated() throws Exception {
		Model model = new PaganModel(new FixedParameterSource(new double[]{.01, .02})); 
		model.participate(experiment); 
		assertEquals(.02, model.getParameterSource().getParameter(1), .001); 
		assertEquals(200, model.getExperiment().getTrials().size()); 
	}
	@Test
	public void verifyModelBehavior() throws Exception {
		Model model = new PaganModel(new FixedParameterSource(new double[]{3.7, 2.67, 1.55, 0.28})); 
		((PaganModel) model).setFeedback(true); 
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
		return results;
	}

	@Test
	public void verifyModelInitialization() throws Exception {
		PaganModel model = new PaganModel(new FixedParameterSource(new double[]{3.7, 2.67, 1.55, 0.28})); 
		model.setFeedback(true); 
		model.participate(experiment); 
		assertEquals(3.7, model.getK(), .001); 
		assertEquals(2.67, model.getTau(), .001); 
		assertEquals(1.55, model.getKappa(), .001); 
		assertEquals(0.28, model.getLambda(), .001); 
		assertEquals(200, model.numberTrials); 
		assertEquals(9, model.numberCues); 
		assertTrue(model.getFeedback());
//		System.out.println("ULP from 1: "+Math.ulp(1.0));
//		System.out.println("ULP from 100: "+Math.ulp(100.0));
//		System.out.println("ULP from -100: "+Math.ulp(-100.0));
	}
}
