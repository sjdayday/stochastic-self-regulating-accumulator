/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import static org.junit.Assert.*;

import org.junit.Test;

public class PaganParameterSourceTest
{

	private PaganParameterSource source;

	@Test
	public void verifyLoadsPaganParametersAsExpected() throws Exception
	{
		source = new PaganParameterSource(new double[]{0.1, 0.2, 0.3, 0.4}); 
		assertEquals(0.1, source.getK(), .001); 
		assertEquals(0.2, source.getTau(), .001); 
		assertEquals(0.3, source.getKappa(), .001); 
		assertEquals(0.4, source.getLambda(), .001); 
	}
	@Test
	public void verifyParametersCanBeLoadedFromStaticVariables() throws Exception
	{
		PaganParameterSource.K = 0.5d; 
		PaganParameterSource.TAU = 0.6d; 
		PaganParameterSource.KAPPA = 0.7d; 
		PaganParameterSource.LAMBDA = 0.8d; 
		source = new PaganParameterSource(); 
		assertEquals(0.5, source.getK(), .001); 
		assertEquals(0.6, source.getTau(), .001); 
		assertEquals(0.7, source.getKappa(), .001); 
		assertEquals(0.8, source.getLambda(), .001); 
	}
	@Test
	public void verifyParametersRetainValuesAfterStaticVariablesChange() throws Exception
	{
		PaganParameterSource.K = 0.5d; 
		source = new PaganParameterSource(); 
		assertEquals(0.5, source.getK(), .001); 
		PaganParameterSource.K = 0.6d; 
		assertEquals(0.6, PaganParameterSource.K, .001); 
		assertEquals("parameters set at construction and not changed after",
				0.5, source.getK(), .001); 
	}
	@Test(expected=IllegalArgumentException.class)
	public void verifyThrowsIfParametersDontNumberExactlyFour() throws Exception {
		source = new PaganParameterSource(new double[]{0.1, 0.2, 0.3}); 
	}

}
