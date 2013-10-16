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
	@Test
	public void verifyEquals() throws Exception
	{
		FixedParameterSource source2 = new FixedParameterSource(new double[]{0.1, 0.2, 0.3}); 
		assertEquals(source, source2);
		assertFalse(source.equals(null));
		assertFalse(source.equals(new StringBuffer()));
		FixedParameterSource sourceBiggerArray = new FixedParameterSource(new double[]{0.1, 0.2, 0.3, 0.4}); 
		assertFalse(source.equals(sourceBiggerArray));
	}
	@Test
	public void verifyHashCode() throws Exception
	{
		assertEquals(2007297737, source.hashCode()); 
		FixedParameterSource source2 = new FixedParameterSource(new double[]{0.1, 0.2, 0.3}); 
		assertEquals(source.hashCode(), source2.hashCode()); 
		FixedParameterSource sourceDifferent = new FixedParameterSource(new double[]{0.1, 0.2, 0.2}); 
		assertFalse(source.hashCode() == sourceDifferent.hashCode()); 
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
