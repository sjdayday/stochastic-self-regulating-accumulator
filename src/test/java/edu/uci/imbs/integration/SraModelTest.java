package edu.uci.imbs.integration;

import static org.junit.Assert.assertEquals;

import org.grayleaves.utility.Result;
import org.junit.Before;
import org.junit.Test;

import edu.uci.imbs.PaganParameterSource;

public class SraModelTest
{
	private SraModel<String> model;

	@Before
	public void setUp() throws Exception
	{
		PaganParameterSource.K = 3.7; 
		PaganParameterSource.TAU = 2.67; 
		PaganParameterSource.KAPPA = 1.55; 
		PaganParameterSource.LAMBDA = 0.28; 
	}
	
	@Test
	public void verifyModel() throws Exception
	{
		model = new SraModel<String>(); 
		Result<String> result = model.run(); 
		assertEquals(200, result.getList().size());
		assertEquals("0\t3\t0.14285714285714285\t5.599264031476239E-15\t0.0", result.getList().get(0)); 
		assertEquals("Score=168.0", result.getSummaryData()); 
	}
}
