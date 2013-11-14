package edu.uci.imbs;

import cc.mallet.util.Randoms;

public class SraParameters
{
	public static int NUMBER_TRIALS_EACH_VALIDITY_REPRESENTS = 100;
	public static long SEED = 1234567890123456789l; 
	public static boolean STOCHASTIC = false; 
	public static Randoms RANDOMS = new Randoms((int) SEED); 
	
	public static void resetForTesting()
	{
		SEED = 1234567890123456789l;
		STOCHASTIC = false; 
		NUMBER_TRIALS_EACH_VALIDITY_REPRESENTS = 100; 
		RANDOMS = new Randoms((int) SEED); 
		
	}
}
