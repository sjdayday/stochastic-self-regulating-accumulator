/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import org.apache.log4j.Logger;

public class ScoreKeeperFactory
{

	private static Logger logger = Logger.getLogger(ScoreKeeperFactory.class);
	public static ScoreKeeper buildScoreKeeper(double[][] subjectData, double weight)
	{
		if ((SraParameters.STOCHASTIC) && (SraParameters.STOCHASTIC_RUNS_PER_PARAMETER_POINT > 1)) 
		{	
			System.out.println("ScoreKeeperFactory:  score represents likelihood.");
			logger.info("ScoreKeeperFactory:  score represents likelihood.");
			return new MaximumLikelihoodScoreKeeper(subjectData, weight);
		}
		else
		{
			System.out.println("ScoreKeeperFactory:  score represents squared error.");
			logger.info("ScoreKeeperFactory:  score represents squared error.");
			return new SquaredErrorScoreKeeper(subjectData, weight);
		}
	}
}
