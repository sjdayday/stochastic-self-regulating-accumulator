package edu.uci.imbs;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ValidityBuilderTest
{

	private ValidityBuilder builder;
	@Before
	public void setUp() throws Exception
	{
		SraParameters.resetForTesting(); 
		SraParameters.NUMBER_TRIALS_EACH_VALIDITY_REPRESENTS = 100; 
	}
	@Test
	public void verifyBuildsBetaDistributionWithCorrectAlphaBeta() throws Exception
	{
		SraParameters.STOCHASTIC = true; 
		builder = new ValidityBuilder(new double[]{0.91, 0.85});
		assertEquals(2, builder.getBetaDistributionParameters().size()); 
		assertEquals(92, builder.getBetaDistributionParameters().get(0).alpha, .001);
		assertEquals(10, builder.getBetaDistributionParameters().get(0).beta, .001);
		assertEquals(86, builder.getBetaDistributionParameters().get(1).alpha, .001);
		assertEquals(16, builder.getBetaDistributionParameters().get(1).beta, .001);
//		printDraw(0); 
	}
	@Test
	public void verifyDrawNextBetaFallsInExpectedRange() throws Exception
	{
		SraParameters.STOCHASTIC = true; 
		SraParameters.NUMBER_TRIALS_EACH_VALIDITY_REPRESENTS = 1000; 
		builder = new ValidityBuilder(new double[]{0.91, 0.85});
		assertEquals(.90077, builder.getNextValidity(0), .00001);
	}
	@SuppressWarnings("unused")
	private void printDraw(int i)
	{
		for (int j = 0; j < 1000; j++)
		{
			System.out.println(builder.getNextValidity(i));
		}
	}
	@Test
	public void verifyReturnsFixedValidityIfNotFlaggedStochastic() throws Exception
	{
		SraParameters.STOCHASTIC = false; 
		builder = new ValidityBuilder(new double[]{0.91, 0.85});
		assertEquals(.91, builder.getNextValidity(0), .001); 
		assertEquals("constant",.91, builder.getNextValidity(0), .001); 
		assertEquals(.85, builder.getNextValidity(1), .001); 
		assertEquals(.85, builder.getNextValidity(1), .001); 
	}
	@Test
	public void verifyReturnsVectorOfCueValiditiesIdenticalToOriginalIfNotStochastic() throws Exception
	{
		SraParameters.STOCHASTIC = false; 
		double[] inputValidities = new double[]{0.91, 0.85, 0.77};
		builder = new ValidityBuilder(inputValidities);
		double[] validities = builder.getCueValidities(); 
		assertEquals(3, validities.length); 
		for (int i = 0; i < validities.length; i++)
		{
			assertEquals(validities[i], inputValidities[i], .001); 
		}
	}
	@Test
	public void verifyReturnsVectorOfCueValidities() throws Exception
	{
		SraParameters.STOCHASTIC = true; 
		double[] inputValidities = new double[]{0.91, 0.85, 0.77};
		builder = new ValidityBuilder(inputValidities);
		double[] validities = builder.getCueValidities(); 
		assertEquals(3, validities.length); 
		assertEquals(0.7971570122332308, validities[0], .000001); 
		assertEquals(0.8208316036800458, validities[1], .000001); 
		assertEquals(0.6902311763666126, validities[2], .000001); 
	}
}
