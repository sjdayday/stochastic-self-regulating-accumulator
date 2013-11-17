/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import cc.mallet.util.Randoms;

public class SraParameters
{
	public static int NUMBER_TRIALS_EACH_VALIDITY_REPRESENTS = 100;
	public static long SEED = 1234567890123456789l; 
	public static boolean STOCHASTIC = false; 
	public static Randoms RANDOMS = new Randoms((int) SEED);
	public static int STOCHASTIC_RUNS_PER_PARAMETER_POINT = 1; 
	public static int TRAINING_TRIALS = 100;
	public static int BEST_SCORE_POINTS_SIZE = 50;
	
	public static void resetForTesting()
	{
		SEED = 1234567890123456789l;
		STOCHASTIC = false; 
		NUMBER_TRIALS_EACH_VALIDITY_REPRESENTS = 100; 
		STOCHASTIC_RUNS_PER_PARAMETER_POINT = 1; 
		TRAINING_TRIALS = 100; 
		BEST_SCORE_POINTS_SIZE = 50; 
		RANDOMS = new Randoms((int) SEED); 
		
	}

}
