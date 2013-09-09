/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ParameterSourceTest {

	private ParameterSource source;
	@Before
	public void setUp() throws Exception {
		source = new FixedParameterSource(new double[]{0.1, 0.2, 0.3}); 
	}
	@Test
	public void verifyReturnsParametersByIndex() throws Exception {
		assertEquals(0.1, source.getParameter(0), .001);
		assertEquals(0.3, source.getParameter(2), .001);
	}
	@Test(expected=IllegalArgumentException.class)
	public void verifyThrowsIfNonexistentParameterRequested() throws Exception {
		source.getParameter(3); 
	}
	@Test(expected=IllegalArgumentException.class)
	public void verifyThrowsIfNoParametersGiven() throws Exception {
		source = new FixedParameterSource(null); 
	}
	@Test(expected=IllegalArgumentException.class)
	public void verifyThrowsIfParametersAreEmpty() throws Exception {
		source = new FixedParameterSource(new double[]{}); 
	}
}
