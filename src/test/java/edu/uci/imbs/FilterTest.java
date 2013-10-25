package edu.uci.imbs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FilterTest
{

	private double[] b;
	private double[] a;
	private double[] x;
	private double[] expected;
	private Filter filter;

	@Before
	public void setUp() throws Exception
	{
		b = new double[]{0.1,0.09,0.08,0.07,0.06};  
		a = new double[]{1,1,1,1,1};  
//		b = new double[]{0.1,0.09,0.08,0.07,0.06,0.05,0.04,0.03,0.02,0.01};  
//		a = new double[]{1,1,1,1,1,1,1,1,1,1};  
		x = new double[]{0,0,0,0.33,0.66,0.57,0.83,1,0.66,0.77}; 
//		expected = new double[]{0,0,0,0.033,0.0957,0.1428,0.2102,0.2863,0.3184,0.3549};
		expected = new double[]{0,0,0,0.033,0.0957,0.1428,0.2102,0.2863,0.3019,0.3087};
	}
	@Test
	public void verifyFilter() throws Exception
	{
		filter = new Filter(); 
		double[] output = filter.filter(b,a,x);  
		for (int i = 0; i < output.length; i++)
		{
			assertEquals("i: "+i,expected[i],output[i], .0001);
		}
	}

}
