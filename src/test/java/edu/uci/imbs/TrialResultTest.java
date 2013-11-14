package edu.uci.imbs;

import static org.junit.Assert.*;

import org.junit.Test;

public class TrialResultTest
{

	@Test
	public void verifyEqualsComparesCueAndSearchDepth() throws Exception
	{
		TrialResult result1 = buildResult(); 
		TrialResult result2 = buildResult(); 
		assertEquals(result1, result2); 
		result2.choice = 0; 
		assertFalse(result1.equals(result2)); 
	}
	@Test
	public void verifyHashcodeSameIfCueAndSearchDepthSame() throws Exception
	{
		TrialResult result1 = buildResult(); 
		TrialResult result2 = buildResult(); 
		assertEquals(result1.hashCode(), result2.hashCode()); 
		result2.choice = 0; 
		assertNotEquals(result1.hashCode(), result2.hashCode()); 
	}
	private TrialResult buildResult()
	{
		TrialResult result = new TrialResult(); 
		result.choice = 1; 
		result.searchDepth = 2; 
		return result;
	}

}
