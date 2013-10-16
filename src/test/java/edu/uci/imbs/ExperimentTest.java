/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

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
	}
	@Test
	public void verifyDataLoadedOnlyOnceAtInitialization() throws Exception
	{
		assertEquals(0, experiment.getTrials().size()); 
		experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
		assertEquals(200, experiment.getTrials().size()); 
		experiment.getTrials().clear();
		assertEquals(0, experiment.getTrials().size()); 
		experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
		assertEquals("although trials is now empty, its not reloaded",
				0, experiment.getTrials().size()); 
	}
	@Test
	public void verifyArraysCorrectlyReadFromMatlabFile() throws Exception {
		experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
		Map<String, MLArray> contents = experiment.getMatlabFileContents(); 
		matlabArray = contents.get("truth"); 
		assertEquals(200, matlabArray.getM()); 
//		printArrayDetails(matlabArray); 
	}
	@Test
	public void verifyDataForOneSubjectReadCorrectly() throws Exception
	{
		experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
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
		experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
		List<Trial> trials = experiment.getTrials(); 
		assertEquals(200, trials.size()); 
		checkOneTrial(trials.get(0), Alternative.B, new Boolean[][]{Trial.BOTH_NEGATIVE, Trial.B_POSITIVE, Trial.B_POSITIVE, Trial.B_POSITIVE,
				Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.B_POSITIVE, Trial.B_POSITIVE, Trial.BOTH_NEGATIVE}); 
		checkOneTrial(trials.get(3), Alternative.A, new Boolean[][]{Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.A_POSITIVE,
			Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.A_POSITIVE, Trial.BOTH_NEGATIVE}); 
		checkOneTrial(trials.get(199), Alternative.B, new Boolean[][]{Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.BOTH_POSITIVE,
			Trial.BOTH_NEGATIVE, Trial.BOTH_NEGATIVE, Trial.BOTH_POSITIVE, Trial.B_POSITIVE, Trial.BOTH_NEGATIVE}); 
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
