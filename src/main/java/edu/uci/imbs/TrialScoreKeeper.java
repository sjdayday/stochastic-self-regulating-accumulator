/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TrialScoreKeeper
{

	private double subjectDataAlternative;
	private double subjectDataCue;
	private double total;
	private double matched;
	protected double proportionMatched;
	private Map<TrialResult, Integer> countsMap;
	private int trialsScored;

	public TrialScoreKeeper(double subjectDataAlternative, double subjectDataCue)
	{
		this.subjectDataAlternative = subjectDataAlternative; 
		this.subjectDataCue = subjectDataCue; 
		total = 0;
		matched = 0; 
		trialsScored = 0; 
		countsMap = new HashMap<TrialResult, Integer>(); 
	}

	public void score(TrialResult result)
	{
		addResultToCountsMap(result); 
		calculateProportionMatched(result); 
		trialsScored++; 
	}

	private void addResultToCountsMap(TrialResult result)
	{
		Integer count = countsMap.get(result); 
		if (count == null) countsMap.put(result, 1); 
		else countsMap.put(result, ++count); 
	}

	private void calculateProportionMatched(TrialResult result)
	{
		total++; 
		matched += ((subjectDataAlternative == (double)result.choice) && 
				(subjectDataCue == (double) result.searchDepth)) ? 1 : 0;
		proportionMatched = matched / total; 
	}

	public TrialResult getBestResult()
	{
		TrialResult bestResult = null;
		int highCount = 0; 
		for (Entry<TrialResult, Integer> entry : countsMap.entrySet())
		{
			if (entry.getValue() > highCount) 
			{
				highCount = entry.getValue();
				bestResult = entry.getKey(); 
			}
		}
		bestResult.proportionMatched = proportionMatched; 
		return bestResult;
	}

	protected Map<TrialResult, Integer> getCountsMap()
	{
		return countsMap;
	}

	public String printSummary()
	{
		StringBuffer sb = new StringBuffer(); 
		for (Entry<TrialResult, Integer> entry : countsMap.entrySet())
		{
			sb.append("Result: ");
			sb.append(entry.getKey().toString());
			sb.append(" Count: "); 
			sb.append(entry.getValue()); 
			sb.append("\n"); 
		}
		TrialResult bestResult = getBestResult(); 
		sb.append("Best Result: "); 
		sb.append(bestResult);
		sb.append(" Count: "); 
		sb.append(countsMap.get(bestResult)); 
		return sb.toString();
	}

	public int getTrialsScored()
	{
		return trialsScored;
	}

}
