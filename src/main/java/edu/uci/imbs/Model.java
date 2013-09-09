/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import java.util.List;

public interface Model {

	public ParameterSource getParameterSource();

	public void participate(Experiment experiment);

	public Experiment getExperiment();

	public List<TrialResult> getResults();

	public void run(); 
}
