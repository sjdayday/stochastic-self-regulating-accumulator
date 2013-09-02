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
 * Utilization:  Setting up a trial for use requires the following steps.  In general, all trials in an experiment will share
 * the same cue validities.  Each trial will have a separate cue profile:  which alternatives are positive and negative for each cue.    
 * <ol>
 * <li> Trial.setCueValidities:  static method called before any Trial is instantiated
 * <li> addCueProfile:  An array of Boolean 2-element arrays, where the element pair indicates which of the alternatives has positive evidence:
 * <pre>   trial.addCueProfile(Trial.BOTH_POSITIVE,
 *				Trial.A_POSITIVE,
 *				Trial.BOTH_NEGATIVE,
 *				Trial.B_POSITIVE);
 * </pre>
 *    In this example, the first and third cues do not add evidence.  The second cue adds evidence for alternative A; 
 *    the fourth cue adds evidence for alternative B.  The cue profile array must be of the same length as the cue validities.
 * <li> setCorrectAlternative:  identify which alternative is correct, given the cue validities and profiles.  Note that this is given, not calculated.
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
	private static boolean INITIALIZED = false;
	private static Double[] CUE_VALIDITIES;
	private static Double[] CUE_LOG_ODDS;
	private Boolean[][] cueProfile;
	private Double[] oddsPerCueForAlternativeA;
	private Double[] oddsPerCueForAlternativeB;
	private Double[] cumulativeOddsForAlternativeA;
	private Double[] cumulativeOddsForAlternativeB;
	private Alternative correctAlternative;

	public static void setCueValidities(Double[] cueValidities) {
		validate(cueValidities); 
		CUE_VALIDITIES = cueValidities; 
		buildLogOddsForCueValidities(); 
		INITIALIZED = true; 
	}
	private static void validate(Double[] cueValidities) {
		if ((cueValidities == null) || (cueValidities.length == 0))
			throw new IllegalArgumentException("Trial.setCueValidities:  cue validities must be not-null and contain at least one element.");  
	}
	private static void buildLogOddsForCueValidities() {
		CUE_LOG_ODDS = new Double[CUE_VALIDITIES.length]; 
		for (int i = 0; i < CUE_VALIDITIES.length; i++) {
			CUE_LOG_ODDS[i] = StrictMath.log(CUE_VALIDITIES[i]/(1-CUE_VALIDITIES[i])); 
		}
	}
	public static double logOddsForCue(int i) {
		return CUE_LOG_ODDS[i];
	}
	public static void resetForTesting() {
		CUE_VALIDITIES = null;
		CUE_LOG_ODDS = null;
		INITIALIZED = false; 
	}
	public Trial() {
		if (!INITIALIZED) throw new IllegalStateException("Trial: setCueValidities must be invoked before a Trial can be created.");
	}
	public void addCueProfile(Boolean[]... cueProfile) {
		if (cueProfile.length != CUE_VALIDITIES.length) throw new IllegalArgumentException("Trial.addCueProfile:  the number of cue profiles must equal the number of cue validities.");
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
		Double[] cumulativeOdds = new Double[CUE_VALIDITIES.length];
		Double total = 0d;
		Double[] tempOdds = (alternative.equals(Alternative.A)) ? oddsPerCueForAlternativeA : oddsPerCueForAlternativeB; 
		for (int i = 0; i < tempOdds.length; i++) {
			total+= tempOdds[i]; 
			cumulativeOdds[i] = total; 
		}
		return cumulativeOdds;
	}
	private Double[] buildOddsPerCueForAlternative(Alternative alternative) {
		Double[] odds = new Double[CUE_VALIDITIES.length];
		for (int i = 0; i < odds.length; i++) {
			odds[i] = (onlyThisAlternativePositive(i, alternative)) ? CUE_LOG_ODDS[i] : 0;
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
}
