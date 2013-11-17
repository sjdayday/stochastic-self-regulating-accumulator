/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ScoreKeeperFactoryTest
{

	@Before
	public void setUp() throws Exception
	{
		SraParameters.resetForTesting();
	}
	@Test
	public void verifyFactoryReturnsMaxLikelihoodScoreKeeperIfStochasticWithRunsGreaterThanOneAndSquaredErrorScoreKeeperOtherwise() throws Exception
	{
		SraParameters.STOCHASTIC = true;
		SraParameters.STOCHASTIC_RUNS_PER_PARAMETER_POINT = 2;
		ScoreKeeper scoreKeeper = ScoreKeeperFactory.buildScoreKeeper(new double[][]{}, 1d);
		assertTrue(scoreKeeper instanceof MaximumLikelihoodScoreKeeper); 
		SraParameters.STOCHASTIC_RUNS_PER_PARAMETER_POINT = 1;
		scoreKeeper = ScoreKeeperFactory.buildScoreKeeper(new double[][]{}, 1d);
		assertTrue(scoreKeeper instanceof SquaredErrorScoreKeeper); 
		SraParameters.STOCHASTIC = false;
		SraParameters.STOCHASTIC_RUNS_PER_PARAMETER_POINT = 2;
		scoreKeeper = ScoreKeeperFactory.buildScoreKeeper(new double[][]{}, 1d);
		assertTrue(scoreKeeper instanceof SquaredErrorScoreKeeper); 
	}
}
