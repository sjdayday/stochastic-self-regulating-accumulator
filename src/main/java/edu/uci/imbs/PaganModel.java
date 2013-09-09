/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import java.util.ArrayList;
import java.util.List;

public class PaganModel implements Model {

	private ParameterSource parameterSource;
	private Experiment experiment;
	protected int numberTrials;
	protected int numberCues;
	private boolean feedback;
	private ArrayList<TrialResult> trialResults;

	public PaganModel(ParameterSource parameterSource) {
		this.parameterSource = parameterSource; 
	}

	public void participate(Experiment experiment) {
		this.experiment = experiment; 
		initialize(); 
	}

	private void initialize() {
		numberTrials = experiment.getTrials().size(); 
		numberCues = experiment.getCueValidities().length; 
		trialResults = new ArrayList<TrialResult>(); 
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public ParameterSource getParameterSource() {
		return parameterSource;
	}

	public void runo() {
		for (int i = 0; i < numberTrials; i++) {
			for (int j = 0; j < numberCues; j++) {
				
			}
		}
	}

	public List<TrialResult> getResults() {
		return trialResults;
	}
	
	protected double getK() {
		return parameterSource.getParameter(0);
	}
	/**
	 * Desired level of confidence
	 * @return double
	 */
	protected double getTau() {
		return parameterSource.getParameter(1);
	}
	/**
	 * Critical level of confidence
	 * @return double
	 */
	protected double getKappa() {
		return parameterSource.getParameter(2);
	}
	/**
	 * Step-size in confidence
	 * @return
	 */
	protected double getLambda() {
		return parameterSource.getParameter(3);
	}
	public void run() {
		TrialResult result = null; 
		Trial trial = null; 
		Alternative alternative = null; 
		double o_r = 0;  // over boundary 
		double o_l = 0; 
		double u_r = 0;  // under boundary 
		double u_l = 0; 
		double k_r = getK(); 
		double k_l = getK(); 
		double h_l = 0; 
		double h_r = 0; 
		double c_r = 0; 
		double c_l = 0; 
		double x = 0; 
		StringBuffer sb = null; 
		for (int j = 0; j < numberTrials; j++) {
//			sb = new StringBuffer(); 
//			sb.append(" c_l "); sb.append(c_l); 
//			sb.append(" c_r "); sb.append(c_r); 
//			sb.append(" h_l "); sb.append(h_l); 
//			sb.append(" h_r "); sb.append(h_r); 
//			sb.append(" k_l "); sb.append(k_l); 
//			sb.append(" k_r "); sb.append(k_r); 
//			sb.append(" o_l "); sb.append(o_l); 
//			sb.append(" o_r "); sb.append(o_r); 
//			sb.append(" u_l "); sb.append(u_l); 
//			sb.append(" u_r "); sb.append(u_r); 
//			sb.append(" x "); sb.append(x); 
//			System.out.println(sb.toString());
//		for t = 1:data_N
		    trial = experiment.getTrials().get(j);  
		    result = new TrialResult();
		    trialResults.add(result); 
		    double t_l = 0;  // reset counters
		    double t_r = 0;
		    int i = 0; // cue...sjd was 1
		    double evaluatedChoice = -1;
		    boolean choice_made = false;
		    while (!choice_made) {
//			    	System.out.println("!choice made for : "+j+" i: "+i);
		    	if (i> numberCues-1) {  
		    		// sjd -1;  if run out of cues without a hit, select whatever had most evidence
		        	
		        	result.choice = (t_r < t_l) ? 1 : 0; // ??
		    		alternative = (t_r < t_l) ? Alternative.A : Alternative.B; 
		    		evaluatedChoice = (alternative.equals(trial.getCorrectAlternative())) ? 1 : 0;  
//			            choice(t) = t_r<t_l;
		    		// if ~choice(t)
		            if (alternative.equals(Alternative.B)) { // % if chose right, update confidence for right
		                if (feedback) {
		                    c_r = (t_r-t_l)*((2*evaluatedChoice)-1);  // confidence 
//			                c_r = (t_r-t_l)*(2*(choice(t)==truth(t))-1);
		                }
		                else
		                    c_r = (t_r-t_l);
		                
		                h_r = c_r - getTau();  // difference between our updated confidence and desired level of confidence
		                if (h_r>0) { // % over-confident
		                    o_r = o_r + StrictMath.abs(h_r);
		                    if (o_r> getKappa()) {  // % if critical bound hit, update trial bounds and reset
		                        k_r = StrictMath.max(k_r - getLambda()*(o_r-u_r),StrictMath.ulp(1.0));
//			                        k_r = StrictMath.max(k_r - getLambda()*(o_r-u_r),eps);
		                        o_r = 0;
		                        u_r = 0;
//			                    end
		                    }
		                }
		                else { // % under-confident
//			                    % can't increase bounds after a no-hit
//			                end
		                }
		            }
		            else {  // % chose left (A), same steps
		                if (feedback) {
		                    c_l = (t_l-t_r)*((2*evaluatedChoice)-1);
//			                    c_l = (t_l-t_r)*(2*(choice(t)==truth(t))-1);
		                }
		                else
		                    c_l = (t_l-t_r);
//			                end
		                h_l = c_l - getTau();
		                if (h_l>0) { // % over-confident
		                    o_l = o_l + StrictMath.abs(h_l);
		                    if (o_l>getKappa()) {
		                        k_l = StrictMath.max(0, k_l - getLambda()*(o_l-u_l)); // SJD why comparison with 0 instead of eps? 
		                        o_l = 0;
		                        u_l = 0;
//			                    end
		                    }
		                }
		                else { // % under-confident
//			                    % can't increase bounds after a no-hit
//			                end
		                }
//			            end
		            }
		            i = i-1;
//			            System.out.println("about to break");
		            break;
//			        end
		        }
//			    	System.out.println("after break");
//			        sjd_a = answers(i,1,t);
//			        sjd_b = answers(i,2,t);
//			        sjd_diff = sjd_a - sjd_b;
//			        sjd_lov = lov(i);
//			        sjd_nl = nl(sjd_lov);
//			        x = sjd_diff*sjd_nl; 
//			        
//			        
		        x = (trial.getResponseForAlternativeForCue(Alternative.A, i)-trial.getResponseForAlternativeForCue(Alternative.B, i))
		        		*(trial.logOddsForCue(i)); // % evidence from cue i: sjd dropped nl til understood
//			        x = (answers(i,1,t)-answers(i,2,t))*nl(lov(i)); % evidence from cue i
		        if (x>0) { // % favor left
//			        	System.out.println("left");
		            t_l = t_l + x;
		            if (t_l>k_l) {
		                choice_made = true;
		                alternative = Alternative.A; 
		                evaluatedChoice = (alternative.equals(trial.getCorrectAlternative())) ? 1 : 0;  
		                result.choice = 1; 
//			                choice(t) = 1; %left
		                if (feedback) {
		                	c_l = (t_l-t_r)*((2*evaluatedChoice)-1);
//			                	c_l = (t_l-t_r)*(2*(choice(t)==truth(t))-1);
		                }
		                else {
		                    c_l = (t_l-t_r);
//			                end
		                }
		                h_l = c_l - getTau();
		                if (h_l>0) { //% over-confident
		                    o_l = o_l + h_l;
		                    if (o_l>getKappa()) {
		                        k_l = StrictMath.max(0, k_l - getLambda()*(o_l-u_l));
		                        o_l = 0;
		                        u_l = 0;
//			                    end
		                    }
		                }
		                else if (h_l<0) { //% under-confident
		                    u_l = u_l + StrictMath.abs(h_l);
		                    if (u_l> getKappa()) {
		                    	
		                        k_l = StrictMath.max(0, k_l + getLambda()*(u_l-o_l));
		                        result.k_l_store = k_l; // sjd:  never read
//			                        k_l_store(t) = k_l;  ??
		                        o_l = 0;
		                        u_l = 0;
//			                    end
		                    }
//			                end
		                }
		            }
		            else {
		                i = i+1;
//			            end
		            }
		        }
		        else { // % favor right
		            t_r = t_r + StrictMath.abs(x);
//			            System.out.println("right: t_r "+t_r+" k_r "+k_r);
		            if (t_r>k_r) {
		                choice_made = true;
		                alternative = Alternative.B; 
		                evaluatedChoice = (alternative.equals(trial.getCorrectAlternative())) ? 1 : 0;  
		                result.choice = 0; 
//			                choice(t) = 0; % right;
		                if (feedback) {
		                    c_r = (t_r-t_l)*((2*evaluatedChoice)-1); //%/(t_r+t_l);
//			                    c_r = (t_r-t_l)*(2*(choice(t)==truth(t))-1);%/(t_r+t_l);
		                }
		                else {
		                    c_r = (t_r-t_l);
//			                end
		                }
		                h_r = c_r - getTau();
		                if (h_r>0) { //% over-confident
		                    o_r = o_r + h_r;
		                    if (o_r> getKappa()) {
		                        k_r = StrictMath.max(0, k_r - getLambda()*(o_r-u_r));
		                        o_r = 0;
		                        u_r = 0;
//			                    end
		                    }
		                }
		                else if (h_r<0) { // % under-confident
		                    u_r = u_r + StrictMath.abs(h_r);
		                    if (u_r> getKappa()) {
		                    	
		                        k_r = StrictMath.max(0, k_r + getLambda()*(u_r-o_r));
		                        result.k_r_store = k_r;
		                        o_r = 0;
		                        u_r = 0;
//			                    end
		                    }
//			                end
		                }
		            }
		            else {
		                i = i+1;
		            }
//			            end
//			        end
		        }
//			    end
		    } // while
		    result.searchDepth = i+1;
//			    rt(t) = i;
//			end
//
		}  // for
	}

	protected void setFeedback(boolean feedback) {
		this.feedback = feedback; 
	}

	protected boolean getFeedback() {
		return feedback;
	}


}
