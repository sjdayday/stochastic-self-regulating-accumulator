/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

/**
 * Experiment represents a set of {@link Trial}s.  An experiment is initialized with a set of cue validities (see {@link Trial}), 
 * which will be identical for every trial in the experiment.    
 * <p>
 * The initial setup for the experiment is loaded from a Matlab configuration file.  
 * The test class assumes this class is:  data\exp2p1.mat
 * <p>
 * The configuration file is expected to contain two arrays, named and structured as follows:
 * <ul>
 * <li>truth:  one-column array of doubles, where 1.0 means {@link Alternative} A is correct, 
 * 	and 0.0 means {@link Alternative} B is correct.  The number of rows in the array defines the number of Trials to be created.
 * <li>answers: 3-dimensional array, where the first dimension is of the same length as the cue validities.
 * The second dimension is two doubles, representing whether an alternative is positive (1.0) or negative (0.0); one value for Alternative A and B, respectively.    
 * The third dimension is the number of Trials (must be same as the number of rows in the truth array.)
 * </ul>
 * 
 * @author stevedoubleday
 *
 */

public class Experiment {

	private static Logger logger = Logger.getLogger(Experiment.class);
	private static final String TRUTH = "truth";
	private static final String ANSWERS = "answers";
	private static final String DATA = "data";
	private MatFileReader reader;
	private Map<String, MLArray> contents;
	private double[] cueValidities;
	private List<Trial> trials;
	private double[][] subjectData;
	private boolean dataLoaded;
	private double[][] truth;
	private double[][] answers;

	public Experiment(double[] cueValidities) {
		this.cueValidities = cueValidities; 
		dataLoaded = false; 
	}

	public void loadConfigurationDataFromClassLoaderResource(String resource) throws Exception
	{
		URL url = ClassLoader.getSystemResource(resource); 
		logger.debug("Experiment:  attempting to load "+resource+" from: "+url.toString());
		loadConfigurationData(new File(url.toURI()));
	}
	public void loadConfigurationData(File file) throws IOException {
		if (!dataLoaded)
		{
			reader = new MatFileReader(file); 
			contents = reader.getContent(); 
			buildTrials();
			dataLoaded = true; 
		}
	}
	public void loadConfigurationData(String filename) throws IOException {
		loadConfigurationData(new File(filename)); 
	}
	public double[][] getSubjectData()
	{
		return subjectData;
	}

	private void buildTrials() {
		truth = getDoubleArray(TRUTH);  
		answers = getDoubleArray(ANSWERS);  
		subjectData = getDoubleArray(DATA);  
		buildFreshTrials(); 
	}
	public void buildFreshTrials()
	{
		trials = new ArrayList<Trial>(); 
		buildTrialsWithCorrectAlternatives();
		updateTrialsWithCueProfiles();
	}

	private void buildTrialsWithCorrectAlternatives() {
		Trial trial = null; 
		ValidityBuilder builder = new ValidityBuilder(cueValidities); 
		for (int i = 0; i < truth.length; i++) {
			trial = new Trial(builder.getCueValidities()); 
			trial.setCorrectAlternative(convertTruthToAlternative(truth[i][0])); 
			trials.add(trial);
		}
	}
	private void updateTrialsWithCueProfiles() {
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
	private Alternative convertTruthToAlternative(double truth) {
		return (truth == 1d) ? Alternative.A : Alternative.B;
	}
	protected double[][] getDoubleArray(String arrayName) {
		MLArray mlarray = contents.get(arrayName); 
		return ((MLDouble) mlarray).getArray();
	}
	protected Map<String, MLArray> getMatlabFileContents() {
		return contents;
	}
	public List<Trial> getTrials() {
		return trials;
	}
	public double[] getCueValidities() {
		return cueValidities;
	}



}
