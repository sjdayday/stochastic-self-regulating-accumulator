/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */
package edu.uci.imbs;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import org.junit.Test;

public class PaganFilterTest
{

	private static final String SLASH = System.getProperty("file.separator");
	private PaganFilter filter;
	private double[] inputSearchProportions;
	private double[] expectedSearchProportions;

	@Test
	public void verifyFilterParmetersSetup() throws Exception
	{
		filter = new PaganFilter(50, .05); 
		double[] exponentVector = filter.getExponentVector(); 
		assertEquals(50, exponentVector.length); 
		assertEquals(-28.8, exponentVector[0], .001);
		assertEquals(-26.45, exponentVector[1], .001);
		assertEquals(0, exponentVector[24], .001);
		assertEquals(-28.8, exponentVector[48], .001);
		assertEquals(-31.25, exponentVector[49], .001);
	}
	@Test
	public void verifySmoothsSearchDepths() throws Exception
	{
		buildVectorsFromTestData(); 
		filter = new PaganFilter(50, .05); 
		double[] smoothedSearchProportions = filter.filter(inputSearchProportions); 
		for (int i = 0; i < smoothedSearchProportions.length; i++)
		{
			assertEquals("i "+i, expectedSearchProportions[i], smoothedSearchProportions[i], .000000001);
		}
	}
	private void buildVectorsFromTestData() throws Exception
	{
		inputSearchProportions = new double[200]; 
		expectedSearchProportions = new double[200];
		BufferedReader reader = new BufferedReader(new FileReader("data"+SLASH+"filter-test.txt"));
		String line = reader.readLine(); 
		int index = 0;
		while (line != null)
		{
			addFieldsToVectors(line, index); 
			line = reader.readLine(); 
			index++; 
		}
		reader.close(); 
	}
	private void addFieldsToVectors(String line, int index)
	{
		StringTokenizer st = new StringTokenizer(line, "\t"); 
		inputSearchProportions[index] = Double.parseDouble(st.nextToken()); 
		expectedSearchProportions[index] = Double.parseDouble(st.nextToken()); 
	}
}
