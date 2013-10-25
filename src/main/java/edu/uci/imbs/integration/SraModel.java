package edu.uci.imbs.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.grayleaves.utility.FitnessTracker;
import org.grayleaves.utility.ListResult;
import org.grayleaves.utility.ModelException;
import org.grayleaves.utility.ParameterPoint;
import org.grayleaves.utility.PersistentModel;
import org.grayleaves.utility.Result;

import edu.uci.imbs.Experiment;
import edu.uci.imbs.PaganModel;
import edu.uci.imbs.PaganParameterSource;
import edu.uci.imbs.ScoreKeeper;
import edu.uci.imbs.ScorePoint;
import edu.uci.imbs.TrialResult;

public class SraModel<R> extends PersistentModel<R> implements FitnessTracker
{

	private Experiment experiment;
	private double[][] subjectData;
	private ScoreKeeper scoreKeeper;
	private Result<R> result;
	private PaganModel paganModel;
	private PaganParameterSource paganParameterSource;
	private ParameterPoint point;
	private static final String SLASH = System.getProperty("file.separator"); 
	public SraModel()
	{
		build();
	}
	private void build()
	{
		experiment = new Experiment(new Double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		try
		{
			//FIXME 
//			experiment.loadConfigurationData("src"+SLASH+"main"+SLASH+"resources"+SLASH+"exp2p1.mat");
			experiment.loadConfigurationData("/Users/stevedoubleday/git/stochastic-self-regulating-accumulator/target/classes/exp2p1.mat");
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		subjectData = experiment.getSubjectData(); 
		scoreKeeper = new ScoreKeeper(subjectData, 1); 
	}
	@SuppressWarnings("unchecked")
	@Override
	public Result<R> run() throws ModelException
	{
		result = new ListResult<R>(); 
		List<TrialResult> results = runPaganModel();
		scoreKeeper.score(results, paganParameterSource, point); 
		for (TrialResult trialResult : results)
		{
			result.add((R) trialResult.toString()); 
		}
		result.setSummaryData(scoreKeeper.getCurrentPoint().toString());
		return result;
	}
	protected List<TrialResult> runPaganModel()
	{
		paganParameterSource = new PaganParameterSource(); 
		paganModel = new PaganModel(true, paganParameterSource); 
		paganModel.participate(experiment); 
		paganModel.run(); 
		List<TrialResult> results = paganModel.getResults();
		return results;
	}
	public Experiment getExperiment()
	{
		return experiment;
	}
	@Override
	public List<ParameterPoint> getBestParameterPoints()
	{
		List<ParameterPoint> points = new ArrayList<ParameterPoint>(); 
		for (ScorePoint scorePoint : scoreKeeper.getBestScorePoints())
		{
			points.add(scorePoint.getParameterPoint()); 
		}
		return points;
	}
	@Override
	public void setCurrentParameterPoint(ParameterPoint point)
	{
		this.point = point; 
	}

}
