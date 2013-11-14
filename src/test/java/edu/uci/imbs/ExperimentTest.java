/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import static org.junit.Assert.*;

import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.jmatio.types.MLArray;

public class ExperimentTest {

	private Experiment experiment;
	private MLArray matlabArray;
	private double[][] data;
	private static final String SLASH = System.getProperty("file.separator"); 
	@Before
	public void setUp() throws Exception {
		experiment = new Experiment(new Double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		SraParameters.resetForTesting(); 
		SraParameters.STOCHASTIC = false; 
		experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
	}
	@Test
	public void verifyDataLoadedOnlyOnceAtInitialization() throws Exception
	{
		assertEquals(200, experiment.getTrials().size()); 
		experiment.getTrials().clear();
		assertEquals(0, experiment.getTrials().size()); 
		experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
		assertEquals("although trials is now empty, its not reloaded",
				0, experiment.getTrials().size()); 
	}
	@Test
	public void verifyArraysCorrectlyReadFromMatlabFile() throws Exception {
		Map<String, MLArray> contents = experiment.getMatlabFileContents(); 
		matlabArray = contents.get("truth"); 
		assertEquals(200, matlabArray.getM()); 
//		printArrayDetails(matlabArray); 
	}
	@Test
	public void verifyDataForOneSubjectReadCorrectly() throws Exception
	{
		data = experiment.getSubjectData(); 
		assertEquals(200, data.length); 
		checkData(0, Alternative.B, 4); 
		checkData(1, Alternative.B, 6); 
		checkData(2, Alternative.A, 3); 
		checkData(3, Alternative.A, 9); 
		checkData(197, Alternative.A, 6); 
		checkData(198, Alternative.A, 9); 
		checkData(199, Alternative.B, 9); 
	}
	public void buildSmoothedDataFile() throws Exception
	{
		data = experiment.getSubjectData(); 
		PaganModel paganModel = new PaganModel(true, new FixedParameterSource(new double[]{3.7, 2.67, 1.55, 0.28})); 
		double[] inputSearchProportions = new double[200];
		TrialResult result = null;
		for (int i = 0; i < data.length; i++)
		{
			result = new TrialResult();
			result.searchDepth = (int) data[i][1];
			paganModel.calculateSearchProportion(experiment.getTrials().get(i), result);
			inputSearchProportions[i] = result.searchProportion; 
		}
		PaganFilter filter = new PaganFilter(50, .05); 
		double[] smoothedSearchProportions = filter.filter(inputSearchProportions); 
		FileWriter writer = new FileWriter("smoothedData.txt");
		StringBuffer sb = null; 
		for (int i = 0; i < smoothedSearchProportions.length; i++)
		{
//			System.out.println(i+" "+data[i][0]+" "+data[i][1]+"  "+inputSearchProportions[i]+"  "+smoothedSearchProportions[i]);
			sb = new StringBuffer(); 
			sb.append(inputSearchProportions[i]); 
			sb.append("\t");
			sb.append(smoothedSearchProportions[i]); 
			sb.append("\n");
			writer.write(sb.toString());
		}
		writer.close(); 
	}
	private void checkData(int trial, Alternative alternative, int cue)
	{
		assertEquals(alternative.value(), data[trial][0], .001); 
		assertEquals(cue, data[trial][1], .001); 
	}
	@Test
	public void verifyCueValiditiesSetAtInitiation() throws Exception {
		experiment = new Experiment(new Double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		assertEquals(9, experiment.getCueValidities().length); 
	}
	@Test
	public void verifyTrialsBuilt() throws Exception {
		List<Trial> trials = experiment.getTrials(); 
		assertEquals(200, trials.size()); 
		checkOneTrial(trials.get(0), Alternative.B, new Boolean[][]{Trial.BOTH_NEGATIVE, Trial.B_POSITIVE, Trial.B_POSITIVE, Trial.B_POSITIVE,
				Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.B_POSITIVE, Trial.B_POSITIVE, Trial.BOTH_NEGATIVE}); 
		checkOneTrial(trials.get(3), Alternative.A, new Boolean[][]{Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.A_POSITIVE,
			Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.A_POSITIVE, Trial.BOTH_NEGATIVE}); 
		checkOneTrial(trials.get(199), Alternative.B, new Boolean[][]{Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.BOTH_POSITIVE,
			Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.BOTH_POSITIVE, Trial.B_POSITIVE, Trial.BOTH_NEGATIVE}); 
	}
	@Test
	public void verifyFreshTrialsBuilt() throws Exception
	{
		List<Trial> trials = experiment.getTrials(); 
		assertEquals(200, trials.size()); 
		Trial firstTrial = trials.get(0); 
		assertEquals(firstTrial, experiment.getTrials().get(0)); 
		experiment.buildFreshTrials(); 
		assertNotEquals(firstTrial, experiment.getTrials().get(0)); 
	}
	@Test
	public void verifyValidityBuilderLoadsStochasticOrFixedValiditiesToTrials() throws Exception
	{
		Trial trial = experiment.getTrials().get(0); 
		assertEquals("fixed validity",.99, trial.getCueValidities()[0], .0001); 
		SraParameters.STOCHASTIC = true;
		experiment.buildFreshTrials(); 
		trial = experiment.getTrials().get(0); 
//		assertEquals("stochastic validity",.98, trial.getCueValidities()[0], .0001); 
		
	}
	private void checkOneTrial(Trial trial, Alternative alternative, Boolean[][] cueProfiles) {
		assertEquals(alternative, trial.getCorrectAlternative()); 
		for (int i = 0; i < cueProfiles.length; i++) {
			assertEquals(cueProfiles[i][0], trial.getCueProfile()[i][0]); 
		}
	}

	@SuppressWarnings("unused")
	private void printArrayDetails(MLArray matlabArray) {
		System.out.println(matlabArray.getFlags());
		System.out.println(matlabArray.getM());
		System.out.println(matlabArray.getN());
		System.out.println(matlabArray.getNDimensions());
		System.out.println(matlabArray.getName());
		System.out.println(matlabArray.getSize());
		System.out.println(matlabArray.getType());
		System.out.println(matlabArray.isDouble());
		System.out.println(matlabArray.isInt8());
		System.out.println(matlabArray.isInt16());
		System.out.println(matlabArray.isInt32());
		System.out.println(matlabArray.isInt64());
		System.out.println(matlabArray.isUint8());
		System.out.println(matlabArray.isUint16());
		System.out.println(matlabArray.isUint32());
		System.out.println(matlabArray.isUint64());
		System.out.println(matlabArray.isFunctionObject());
		System.out.println(matlabArray.isStruct());
		System.out.println(matlabArray.isChar());
		System.out.println(matlabArray.isCell());
		System.out.println(matlabArray.isComplex());
		System.out.println(matlabArray.isObject());
		System.out.println(matlabArray.isEmpty());
		System.out.println(matlabArray.isLogical());
	}

}
