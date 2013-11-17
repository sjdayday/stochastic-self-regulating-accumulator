/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs.integration;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.grayleaves.utility.Result;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.imbs.PaganParameterSource;
import edu.uci.imbs.SraParameters;

public class SraModelTest
{
	private SraModel<String> model;
	@BeforeClass
	public static void setUpLog4J() throws Exception
	{
		BasicConfigurator.configure();
//		Logger.getRootLogger().setLevel(Level.DEBUG);
		Logger.getRootLogger().setLevel(Level.ERROR);
//		Logger.getRootLogger().setLevel(Level.INFO);
	}
	@Before
	public void setUp() throws Exception
	{
		PaganParameterSource.K = 3.7; 
		PaganParameterSource.TAU = 2.67; 
		PaganParameterSource.KAPPA = 1.55; 
		PaganParameterSource.LAMBDA = 0.28; 
//		PaganParameterSource.K = 2.9; 
//		PaganParameterSource.TAU = 2.7; 
//		PaganParameterSource.KAPPA = 1.6; 
//		PaganParameterSource.LAMBDA = 0.18; 
		SraParameters.resetForTesting();
	}
	@Test
	public void verifyNonStochasticModelGeneratesSquaredError() throws Exception
	{
		SraParameters.STOCHASTIC = false; 
		SraParameters.STOCHASTIC_RUNS_PER_PARAMETER_POINT = 1; 
		model = new SraModel<String>(); 
		Result<String> result = model.run(); 
		assertEquals(200, result.getList().size());
		assertEquals("0\t3\t0.14285714285714285\t5.599264031476239E-15\t0.0", result.getList().get(0)); 
		assertEquals("Score=168.0", result.getSummaryData()); 
	}
	@Test
	public void verifyStochasticModelGeneratesLikelihood() throws Exception
	{
		SraParameters.STOCHASTIC = true; 
//		SraParameters.TRAINING_TRIALS = 100; 
		SraParameters.TRAINING_TRIALS = 3; 
//		SraParameters.STOCHASTIC_RUNS_PER_PARAMETER_POINT = 10000; 
		SraParameters.STOCHASTIC_RUNS_PER_PARAMETER_POINT = 100; 
//		SraParameters.NUMBER_TRIALS_EACH_VALIDITY_REPRESENTS = 10; 
		SraParameters.NUMBER_TRIALS_EACH_VALIDITY_REPRESENTS = 100; 
		model = new SraModel<String>(); 
		Result<String> result = model.run(); 
		assertEquals(200, result.getList().size());
		assertEquals("0\t3\t0.14285714285714285\t5.599264031476239E-15\t0.22", result.getList().get(0)); 
		assertEquals("Score=-5.674612097356421", result.getSummaryData()); 
	}
}
