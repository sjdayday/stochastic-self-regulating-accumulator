/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

public class Experiment {

	private static final String TRUTH = "truth";
	private static final String ANSWERS = "answers";
	private MatFileReader reader;
	private Map<String, MLArray> contents;
	private Double[] cueValidities;
	private List<Trial> trials;

	public Experiment(Double[] cueValidities) {
		this.cueValidities = cueValidities; 
	}

	public void loadConfigurationData(String filename) throws IOException {
		reader = new MatFileReader(new File(filename)); 
		contents = reader.getContent(); 
		buildTrials();
	}

	private void buildTrials() {
		double[][] truth = getDoubleArray(TRUTH);  
		double[][] answers = getDoubleArray(ANSWERS);  
		buildTrialsWithCorrectAlternatives(truth);
		updateTrialsWithCueProfiles(answers);
	}

	private void updateTrialsWithCueProfiles(double[][] answers) {
		Boolean[][] cueProfiles = null; 
		for (int i = 0; i < trials.size(); i++) {
			int index = i*2; 
			cueProfiles = new Boolean[answers.length][2]; 
			for (int j = 0; j < answers.length; j++) {
				cueProfiles[j] = convertAnswersToCueProfile(answers[j][index],answers[j][index+1]); 
			}
			trials.get(i).setCueProfile(cueProfiles);
		}
	}
	private Boolean[] convertAnswersToCueProfile(double a, double b) {
		return new Boolean[]{convertDoubleToBoolean(a), convertDoubleToBoolean(b) };
	}
	private Boolean convertDoubleToBoolean(double answer) {
		return (answer == 1d) ? true : false;
	}
	private void buildTrialsWithCorrectAlternatives(double[][] truth) {
		trials = new ArrayList<Trial>(); 
		Trial trial = null; 
		for (int i = 0; i < truth.length; i++) {
			trial = new Trial(cueValidities); 
			trial.setCorrectAlternative(convertTruthToAlternative(truth[i][0])); 
			trials.add(trial);
		}
	}

//private void print3dArray(MLArray data) {
//double[][] darray = ((MLDouble) data).getArray(); 
//System.out.println("1: "+darray.length);
//for (int i = 0; i < darray.length; i++) {
//	for (int j = 0; j < 400; j = j+2) {
//			System.out.println(data.getName()+" i "+i+" j "+j+": "+darray[i][j]+" + "+darray[i][j+1]);
	private Alternative convertTruthToAlternative(double truth) {
		return (truth == 1d) ? Alternative.A : Alternative.B;
	}

	protected double[][] getDoubleArray(String arrayName) {
		MLArray mlarray = contents.get(arrayName); 
		return ((MLDouble) mlarray).getArray();
//private void printArray(MLArray data, int lim) {
//double[][] darray = ((MLDouble) data).getArray(); 
//System.out.println("1: "+darray.length);
//for (int i = 0; i < darray.length; i++) {
//	for (int j = 0; j < lim; j++) {
//		System.out.println(data.getName()+" i "+i+" j "+j+": "+darray[i][j]);
//	}
	}

	protected Map<String, MLArray> getMatlabFileContents() {
		return contents;
	}

	public List<Trial> getTrials() {
		return trials;
	}

	public Double[] getCueValidities() {
		return cueValidities;
	}

}

////printStuff(answers);
//print3dArray(answers);
//MLArray truth = contents.get("truth"); 
//System.out.println("truth....");
////printStuff(truth);
////printArray(truth, 1);
//MLArray data = contents.get("data"); 
//System.out.println("data....");
////printStuff(data);
////for (String entry : contents.keySet()) {
////	System.out.println(entry);
////}
////printArray(data, 2);
////System.out.println(contents.get("truth").contentToString());
////System.out.println(contents.get("data").contentToString());
////System.out.println(contents.get("answers").contentToString());
//}
//
//	}
//}
//}
//}
//}
