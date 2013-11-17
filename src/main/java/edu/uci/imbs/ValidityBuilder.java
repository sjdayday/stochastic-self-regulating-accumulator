/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cc.mallet.util.Randoms;

public class ValidityBuilder
{
	private static Logger logger = Logger.getLogger(MaximumLikelihoodScoreKeeper.class);
	private List<BetaParameterPair> betaDistributionParameters;
	private double[] actualValidities;
	private int numberTrialsEachValidityRepresents;
	private Randoms randoms;
	private boolean stochastic;
	private double[] cueValidities;

	public ValidityBuilder(double[] actualValidities)
	{
		this.actualValidities = actualValidities; 
		this.stochastic = SraParameters.STOCHASTIC; 
		this.numberTrialsEachValidityRepresents = SraParameters.NUMBER_TRIALS_EACH_VALIDITY_REPRESENTS; 
		if (stochastic)
		{
			betaDistributionParameters = new ArrayList<BetaParameterPair>();
			randoms = SraParameters.RANDOMS; 
			buildBetaDistributionParameters();
		}
	}
	protected void buildBetaDistributionParameters()
	{
		double alpha = 0; 
		double beta = 0; 
		for (int i = 0; i < actualValidities.length; i++)
		{
			alpha = (actualValidities[i] * numberTrialsEachValidityRepresents) + 1; 
			beta = ((1-actualValidities[i]) * numberTrialsEachValidityRepresents) + 1; 
			betaDistributionParameters.add(new BetaParameterPair(alpha, beta)); 
		}
	}
	public double getNextValidity(int i)
	{
		if (stochastic) 
			return randoms.nextBeta(betaDistributionParameters.get(i).alpha, betaDistributionParameters.get(i).beta); 
//		return randoms.nextUniform(); 
		else return actualValidities[i]; 
	}

	public List<BetaParameterPair> getBetaDistributionParameters()
	{
		return betaDistributionParameters;
	}

	public double[] getCueValidities()
	{
		if (cueValidities == null) 
		{
			cueValidities = new double[actualValidities.length]; 
			for (int i = 0; i < actualValidities.length; i++)
			{
				cueValidities[i] = getNextValidity(i); 
			}
		}
		logger.debug(printValidities()); 
		return cueValidities;
	}
	private String printValidities()
	{
		StringBuffer sb = new StringBuffer(); 
		for (int i = 0; i < cueValidities.length; i++)
		{
			sb.append(cueValidities[i]);
			sb.append(" "); 
		}
		sb.append("\n");
		return sb.toString(); 
	}

}
