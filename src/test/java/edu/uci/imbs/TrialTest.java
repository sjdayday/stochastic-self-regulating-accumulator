/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package edu.uci.imbs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Categories.ExcludeCategory;

public class TrialTest {

	private Trial trial;
	@Before
	public void setUp() throws Exception {
		Trial.resetForTesting(); 
	}
	@Test
	public void verifyLogOddsCalculatedForCueValidities() {
		Trial.setCueValidities(new Double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		assertEquals(4.59511985013459, Trial.logOddsForCue(0), .00000000000001); 
		assertEquals(2.31363492918063, Trial.logOddsForCue(1), .00000000000001); 
		assertEquals(1.90095876119305, Trial.logOddsForCue(2), .00000000000001); 
		assertEquals(1.26566637333128, Trial.logOddsForCue(3), .00000000000001); 
		assertEquals(1.20831120592453, Trial.logOddsForCue(4), .00000000000001); 
		assertEquals(1.09861228866811, Trial.logOddsForCue(5), .00000000000001); 
		assertEquals(0.895384047054841, Trial.logOddsForCue(6), .00000000000001); 
		assertEquals(0.241162056816888, Trial.logOddsForCue(7), .00000000000001); 
		assertEquals(0.0400053346136992, Trial.logOddsForCue(8), .00000000000001); 
	}
	@Test
	public void verifyCueProfilesLoadedCorrectly() throws Exception {
		Trial.setCueValidities(new Double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		trial = new Trial();  
		trial.addCueProfile(Trial.BOTH_POSITIVE,
				Trial.BOTH_POSITIVE,
				Trial.A_POSITIVE,
				Trial.B_POSITIVE,
				Trial.A_POSITIVE,
				Trial.BOTH_NEGATIVE,
				Trial.B_POSITIVE,
				Trial.BOTH_POSITIVE,
				Trial.B_POSITIVE); 
		assertEquals(0, trial.accumulatedOddsForAlternativeAfterCue(Alternative.A, 0), .0000001); 
		assertEquals(0, trial.accumulatedOddsForAlternativeAfterCue(Alternative.B, 0), .0000001); 
		assertEquals(0, trial.accumulatedOddsForAlternativeAfterCue(Alternative.A, 1), .0000001); 
		assertEquals(0, trial.accumulatedOddsForAlternativeAfterCue(Alternative.B, 1), .0000001); 
		assertEquals(1.90095876119305, trial.accumulatedOddsForAlternativeAfterCue(Alternative.A, 2), .0000001); 
		assertEquals(0, trial.accumulatedOddsForAlternativeAfterCue(Alternative.B, 2), .0000001); 
		assertEquals(1.90095876119305, trial.accumulatedOddsForAlternativeAfterCue(Alternative.A, 3), .0000001); 
		assertEquals(1.26566637333128, trial.accumulatedOddsForAlternativeAfterCue(Alternative.B, 3), .0000001); 
		assertEquals(3.10926996711758, trial.accumulatedOddsForAlternativeAfterCue(Alternative.A, 4), .0000001); 
		assertEquals(1.26566637333128, trial.accumulatedOddsForAlternativeAfterCue(Alternative.B, 4), .0000001); 
		assertEquals(3.10926996711758, trial.accumulatedOddsForAlternativeAfterCue(Alternative.A, 5), .0000001); 
		assertEquals(1.26566637333128, trial.accumulatedOddsForAlternativeAfterCue(Alternative.B, 5), .0000001); 
		assertEquals(3.10926996711758, trial.accumulatedOddsForAlternativeAfterCue(Alternative.A, 6), .0000001); 
		assertEquals(2.16105042038612, trial.accumulatedOddsForAlternativeAfterCue(Alternative.B, 6), .0000001); 
		assertEquals(3.10926996711758, trial.accumulatedOddsForAlternativeAfterCue(Alternative.A, 7), .0000001); 
		assertEquals(2.16105042038612, trial.accumulatedOddsForAlternativeAfterCue(Alternative.B, 7), .0000001); 
		assertEquals(3.10926996711758, trial.accumulatedOddsForAlternativeAfterCue(Alternative.A, 8), .0000001); 
		assertEquals(2.20105575499982, trial.accumulatedOddsForAlternativeAfterCue(Alternative.B, 8), .0000001); 
	}
	@Test
	public void verifyCorrectAlternativeIsIdentified() throws Exception {
		Trial.setCueValidities(new Double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		trial = new Trial();  
		trial.setCorrectAlternative(Alternative.A); 
		assertEquals(Alternative.A, trial.getCorrectAlternative()); 
	}
	@Test(expected=IllegalArgumentException.class)
	public void verifyThrowsIfCueValiditiesNull() throws Exception {
		Trial.setCueValidities(null);
	}
	@Test(expected=IllegalArgumentException.class)
	public void verifyThrowsIfCueValiditiesEmpty() throws Exception {
		Trial.setCueValidities(new Double[]{});
	}
	@Test(expected=IllegalStateException.class)
	public void verifyThrowsOnFirstUseIfNotInitialized() throws Exception {
		trial = new Trial();  
	}
	@Test(expected=IllegalArgumentException.class)
	public void verifyNumberOfCueProfilesMatchesNumberOfCueValidities() throws Exception {
		Trial.setCueValidities(new Double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		trial = new Trial();  
		trial.addCueProfile(Trial.BOTH_POSITIVE); 
	}
	@Test(expected=IllegalStateException.class)
	public void verifyThrowsIfCorrectAlternativeNotSet() throws Exception {
		Trial.setCueValidities(new Double[]{.99, .91, .87, .78, .77, .75, .71, .56, .51});
		trial = new Trial();  
		trial.getCorrectAlternative().toString(); 
	}

}
