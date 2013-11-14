/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

/**
 * Represents a single Trial where a subject will be sequentially presented with one or more cues.
 * The subject is asked to determine, based on the evidence of the cues, which of two {@link Alternative}s (termed A and B) is correct.
 * Each cue has a validity, the percentage of the time that this cue, if positive for only one of the alternatives, 
 * indicates that that alternative is correct.  Evidence is accumulated for each alternative separately, as follows:
 * If both alternatives are both positive or both negative, the evidence is unchanged.
 * For any cue where one alternative is positive and the other is negative, the log-odds of the cue validity for that cue 
 * is added to the cumulative evidence.  The cumulative evidence an alternative is thus the sum of the log-odds for all the cues where 
 * only that alternative has a positive cue.  The log-odds is defined as follows: 
 * <sl>
 * <li>   ln(cue_validity / (1-cue_validity))
 * </sl>   
 * <p>
 * Utilization:  Trials are set up by an {@link Experiment}. Setting up a trial for use requires the following information  
 * <ul>
 * <li> cueValidities:  set in the initializer; all trials in an experiment will share the same cue validities.
 * <li> cueProfile:  An array of Boolean 2-element arrays, where the element pair indicates which of the alternatives has positive evidence:
 * <pre>   trial.addCueProfile(Trial.BOTH_POSITIVE,
 *				Trial.A_POSITIVE,
 *				Trial.BOTH_NEGATIVE,
 *				Trial.B_POSITIVE);
 * </pre>
 *    In this example, the first and third cues do not add evidence.  The second cue adds evidence for alternative A; 
 *    the fourth cue adds evidence for alternative B.  The cue profile array must be of the same length as the cue validities.
 * <li> correctAlternative:  identifies which alternative is correct, given the cue validities and cue profiles.  Note that this is given, not calculated.
 * </ul>
 *  
 * @author stevedoubleday
 * @see    https://webfiles.uci.edu/mdlee/LeeNewellVandekerckhove2013.pdf 
 */
public class Trial {

	public static Boolean[] BOTH_POSITIVE = new Boolean[]{true, true};
	public static Boolean[] BOTH_NEGATIVE = new Boolean[]{false, false};
	public static Boolean[] A_POSITIVE = new Boolean[]{true, false};
	public static Boolean[] B_POSITIVE = new Boolean[]{false, true};
	private Boolean[][] cueProfile;
	private Double[] oddsPerCueForAlternativeA;
	private Double[] oddsPerCueForAlternativeB;
	private Double[] cumulativeOddsForAlternativeA;
	private Double[] cumulativeOddsForAlternativeB;
	private Alternative correctAlternative;
	private Double[] cueValidities;
	private Double[] cueLogOdds;

	public Trial(Double[] cueValidities) {
		validate(cueValidities); 
		buildLogOddsForCueValidities(); 
	}
	private void validate(Double[] cueValidities) {
		if ((cueValidities == null) || (cueValidities.length == 0))
			throw new IllegalArgumentException("Trial.setCueValidities:  cue validities must be not-null and contain at least one element.");  
		this.cueValidities = cueValidities; 
	}
	private void buildLogOddsForCueValidities() {
		cueLogOdds = new Double[cueValidities.length]; 
		for (int i = 0; i < cueValidities.length; i++) {
			cueLogOdds[i] = StrictMath.log(cueValidities[i]/(1-cueValidities[i])); 
		}
	}
	public double logOddsForCue(int i) {
		return cueLogOdds[i];
	}
	public void setCueProfile(Boolean[]... cueProfile) {
		if (cueProfile.length != cueValidities.length) throw new IllegalArgumentException("Trial.addCueProfile:  the number of cue profiles must equal the number of cue validities.");
		this.cueProfile = cueProfile;
		accumulateOdds(); 
	}
	private void accumulateOdds() {
		this.oddsPerCueForAlternativeA = buildOddsPerCueForAlternative(Alternative.A); 
		this.oddsPerCueForAlternativeB = buildOddsPerCueForAlternative(Alternative.B); 
		this.cumulativeOddsForAlternativeA = accumulateOddsForAlternative(Alternative.A); 
		this.cumulativeOddsForAlternativeB = accumulateOddsForAlternative(Alternative.B); 
	}
	private Double[] accumulateOddsForAlternative(Alternative alternative) {
		Double[] cumulativeOdds = new Double[cueValidities.length];
		Double total = 0d;
		Double[] tempOdds = (alternative.equals(Alternative.A)) ? oddsPerCueForAlternativeA : oddsPerCueForAlternativeB; 
		for (int i = 0; i < tempOdds.length; i++) {
			total+= tempOdds[i]; 
			cumulativeOdds[i] = total; 
		}
		return cumulativeOdds;
	}
	private Double[] buildOddsPerCueForAlternative(Alternative alternative) {
		Double[] odds = new Double[cueValidities.length];
		for (int i = 0; i < odds.length; i++) {
			odds[i] = (onlyThisAlternativePositive(i, alternative)) ? cueLogOdds[i] : 0;
		}
		return odds;
	} 
	private boolean onlyThisAlternativePositive(int i, Alternative alternative) {
		Alternative otherAlternative = alternative.other(); 
		return ((cueProfile[i][alternative.arrayPosition()] == true) && (cueProfile[i][otherAlternative.arrayPosition()] == false));
	}
	public double accumulatedOddsForAlternativeAfterCue(Alternative alternative, int cue) {
		return (alternative.equals(Alternative.A)) ? cumulativeOddsForAlternativeA[cue] : cumulativeOddsForAlternativeB[cue];
	}
	public void setCorrectAlternative(Alternative correctAlternative) {
		this.correctAlternative = correctAlternative;
	}
	public Alternative getCorrectAlternative() {
		if (correctAlternative == null) throw new IllegalStateException("Trial.getCorrectAlternative:  setCorrectAlternative must be invoked before the correct alternative can be retrieved.");
		return correctAlternative;
	}
	public Boolean[][] getCueProfile() {
		return cueProfile;
	}
	public int getResponseForCue(Alternative alternative, int requestedCue) {
		if ((requestedCue < 0) || (requestedCue >= cueProfile.length)) throw new IllegalArgumentException("Trial.getResponseForAlternativeForCue:  the cue requested must not be less than 0 or greater than: "+(cueProfile.length-1)+" was "+requestedCue);
		Boolean[] cue = cueProfile[requestedCue];  
		return (cue[alternative.arrayPosition()]) ? 1 : 0;
	}
	public int firstDiscriminatingCue()
	{
		int cue = 0;
		for (; cue < cueProfile.length; cue++)
		{
			if (cueProfile[cue][0] != cueProfile[cue][1]) break;
		}
		if (cue == cueProfile.length) cue--; 
		return cue;
	}
	protected Double[] getCueValidities()
	{
		return cueValidities;
	}
}
