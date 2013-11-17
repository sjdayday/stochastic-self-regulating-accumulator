/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs.fit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.grayleaves.utility.Constants;
import org.grayleaves.utility.DummyInput;
import org.grayleaves.utility.Environment;
import org.grayleaves.utility.MockClock;
import org.grayleaves.utility.MockHibernateScenarioSet;
import org.grayleaves.utility.NameValuePair;
import org.grayleaves.utility.NameValuePairBuilder;
import org.grayleaves.utility.ParameterIterator;
import org.grayleaves.utility.ParameterSpace;
import org.grayleaves.utility.ParameterSpacePersister;
import org.grayleaves.utility.ScenarioException;
import org.grayleaves.utility.ScenarioSet;
import org.grayleaves.utility.fit.ParameterSpaceFixture;

import edu.uci.imbs.integration.SraModel;
import fitlibrary.DoFixture;

public class SraFixture extends DoFixture
{
	private ParameterSpaceFixture protectionParameterSpaceFixture;
	private ScenarioSet<String, DummyInput> scenarioSet;

	public void sraParameterSpace()
	{
		protectionParameterSpaceFixture = new ParameterSpaceFixture(); 
		setSystemUnderTest(protectionParameterSpaceFixture); 
	}
	public String showStuff()
	{
		return "where "+System.getProperty("SRATARGET")+System.getProperty("SCENARIO_ROOT"); 
	}
	public void runScenarios() throws ScenarioException
	{
		scenarioSet = new MockHibernateScenarioSet<String, DummyInput>(false);		
		scenarioSet.setName("SRA"); // perhaps a different name  
		SraModel<String> model = new SraModel<String>();
		scenarioSet.setModel(model);
		scenarioSet.setInput(new DummyInput());
		ParameterSpace space = protectionParameterSpaceFixture.getParameterSpace(); 
		ParameterIterator iterator = space.iterator(); 
		iterator.setFitnessTracker(model); 
		
		scenarioSet.setParameterSpace(space); 
		scenarioSet.setCalendar(MockClock.getCalendar());
		scenarioSet.setId(buildMockId());
		buildSummaryHeader(scenarioSet);
		scenarioSet.batchRun(); 
		saveParameterSpace(scenarioSet); 
	}

	protected void buildSummaryHeader(
			ScenarioSet<String, DummyInput> scenarioSet)
	{
		NameValuePairBuilder b = new NameValuePairBuilder(); 
		List<NameValuePair<?>> pairs = new ArrayList<NameValuePair<?>>(); 
		pairs.add(b.doublePair("Score"));
		scenarioSet.buildSummaryHeader(pairs.toArray(new NameValuePair<?>[pairs.size()])); 
	}
	private void saveParameterSpace(ScenarioSet<String, DummyInput> scenarioSet) throws ScenarioException
	{
		try
		{
			String name = "parameterSpaceScenario_"+scenarioSet.getId(); 
			String filename = scenarioSet.getOutputFileBuilder().getRootDirectoryFullPathName()+Constants.SLASH+name+".xml"; 
			scenarioSet.getParameterSpace().setFilename(filename); 
			ParameterSpacePersister<ParameterSpace> spacePersister = new ParameterSpacePersister<ParameterSpace>();
			scenarioSet.getParameterSpace().loadProperties("org/grayleaves/utility/testing.properties");  // kinda dumb
			spacePersister.save(scenarioSet.getParameterSpace(), filename);  //
		}
		catch (Exception e)
		{
			throw new ScenarioException("ProtectionFixture.saveParameterSpace: "+e.getMessage());
		}
	}
	private int buildMockId() throws ScenarioException
	{
		String pathname = "";
		String envPath = Environment.getEnv("SCENARIO_ROOT");
		if (envPath != null) pathname = envPath+Constants.SLASH;
		int id = 1;
		File file = null; 
		boolean idNotAvailable = true;
		while (idNotAvailable)
		{
			file = new File(pathname+"ScenarioSet_"+id);
			if (!file.exists())
			{
				idNotAvailable = false; 
			}
			else id++; 
		}
		return id;
	}

}
