/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ScorePointTest
{

	private ScorePoint scorePoint;
	private ScorePoint scorePoint2;
	private FixedParameterSource source;
	@Before
	public void setUp() throws Exception
	{
		source = new FixedParameterSource(new double[]{1.0}); 
		scorePoint = new ScorePoint(2, source, null); 
		
	}
	@Test
	public void verifyScorePointImplementsEqualsOverItsScoreAndParametersInEventOfTies() throws Exception
	{
		scorePoint2 = new ScorePoint(3, source, null); 
		assertEquals(-1, scorePoint.compareTo(scorePoint2)); 
		scorePoint2 = new ScorePoint(1, source, null); 
		assertEquals(1, scorePoint.compareTo(scorePoint2)); 
		scorePoint2 = new ScorePoint(2, new FixedParameterSource(new double[]{2.0}), null); 
		assertEquals(-1, scorePoint.compareTo(scorePoint2)); 
		scorePoint2 = new ScorePoint(2, source, null); 
		assertEquals(0, scorePoint.compareTo(scorePoint2)); 
	}
	@Test(expected=NullPointerException.class)
	public void verifyThrowsIfComparedToNull() throws Exception
	{
		scorePoint.compareTo(null);
	}
	@Test(expected=ClassCastException.class)
	public void verifyOnlyScorePointsAreCompared() throws Exception
	{
		scorePoint.compareTo("");
	}
	
}
