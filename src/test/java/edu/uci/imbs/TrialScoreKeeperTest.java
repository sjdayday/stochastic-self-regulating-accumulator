package edu.uci.imbs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TrialScoreKeeperTest
{

	private TrialScoreKeeper tsk;
	private TrialResult result;
	private TrialResult newResult;

	@Before
	public void setUp() throws Exception
	{
		tsk = new TrialScoreKeeper(1.0, 3.0); 
	}
	@Test
	public void verifyAccumulatesProportionOfDataModelMatches() throws Exception
	{
		buildAndScoreAndGetBestTrialResult(0, 2); 
		checkProportion("", 0d); 
		buildAndScoreAndGetBestTrialResult(1, 3); 
		checkProportion("",0.5); 
		buildAndScoreAndGetBestTrialResult(1, 3); 
		checkProportion("", 0.666); 
		buildAndScoreAndGetBestTrialResult(1, 3); 
		checkProportion("", 0.75); 
		buildAndScoreAndGetBestTrialResult(1, 0); 
		checkProportion("", 0.6); 
	}
	@Test
	public void verifyReturnsResultWithMostCommonCombinationOfChoiceAndSearchDepth() throws Exception
	{
		assertEquals(0,tsk.getCountsMap().size()); 
		buildAndScoreAndGetBestTrialResult(0, 2); 
		checkResultFields("", 0, 2, 1); 
		assertEquals(1,tsk.getCountsMap().size()); 
		buildAndScoreAndGetBestTrialResult(1, 3); 
		checkResultFields("whatever is first in the Map EntrySet will win in case of ties", 0, 2, 1); 
		buildAndScoreAndGetBestTrialResult(1, 3); 
		checkResultFields("", 1, 3, 2); 
		buildAndScoreAndGetBestTrialResult(0, 2); 
		checkResultFields("tied again", 0, 2, 2); 
		assertEquals(2,tsk.getCountsMap().size()); 
	}
	@Test
	public void verifyPrintsSummaryOfTrialResultCounts() throws Exception
	{
		buildAndScoreTrialResult(0, 2); 
		buildAndScoreTrialResult(1, 3); 
		buildAndScoreTrialResult(0, 1); 
		buildAndScoreTrialResult(1, 3); 
		buildAndScoreTrialResult(0, 2); 
		assertEquals(
				"Result: 0\t2\t0.0\t0.0\t0.0 Count: 2\n" +
				"Result: 0\t1\t0.0\t0.0\t0.0 Count: 1\n" +
				"Result: 1\t3\t0.0\t0.0\t0.0 Count: 2\n" +
				"Best Result: 0\t2\t0.0\t0.0\t0.4 Count: 2", 
				tsk.printSummary()); 
	}
	protected void checkProportion(String comment, double proportionMatched)
	{
		assertEquals(comment, proportionMatched, newResult.proportionMatched, .001);
	}
	protected void checkResultFields(String comment, int choice, int cue, int count)
	{
		assertEquals(comment, choice, newResult.choice); 
		assertEquals(comment, cue, newResult.searchDepth); 
		assertEquals(count, (int) tsk.getCountsMap().get(newResult)); 
	}
	protected void buildAndScoreAndGetBestTrialResult(int choice, int cue)
	{
		buildAndScoreTrialResult(choice, cue); 
		newResult = tsk.getBestResult(); 
		
	}
	protected void buildAndScoreTrialResult(int choice, int cue)
	{
		result = new TrialResult(); 
		result.choice = choice;
		result.searchDepth = cue; 
		tsk.score(result);
	}
}
