package game_engine;

import common.models.TrainDirection;
import game_engine.Train;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class TrainTest {
	
	@Test
	public void initializingTrainWithLegalValuesShouldPass() {
		Train train = null;
		try {
			train = new Train("616", "Mangala Lakshadweep Express", "TowardsHome");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		assertNotNull(train);
	}
	
	@Test
	public void verifyTrainDirectionIsSet() {
		Train homeTrain = null;
		Train awayTrain = null;
		try {
			homeTrain = new Train("616", "Mangala Lakshadweep Express", "TowardsHome");
			awayTrain = new Train("2653", "Mangala Lakshadweep Express", "AwayFromHome");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		assertEquals(TrainDirection.TOWARDS_HOME,
				homeTrain.getDirection(), "Train1 direction was not set properly.");
		assertEquals(TrainDirection.AWAY_FROM_HOME,
				awayTrain.getDirection(), "Train2 direction was not set properly.");
	}
	
	@Test
	public void stationListMustBePopulatedOnInitialization() {
		try {
			Train someTrain = new Train("616","Poorna Express", "TowardsHome");
			assertTrue(someTrain.stations.size() > 0, "Station list not set.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
	}
	
	@Test
	public void stationListMustBeReversedForTrainsTowardsHome() {
		try {
			Train homeTrain = new Train("616", "Mangala Lakshadweep Express", "TowardsHome");
			Train awayTrain = new Train("2653", "Mangala Lakshadweep Express", "AwayFromHome");
			assertEquals(homeTrain.stations.get(0), awayTrain.stations.get(awayTrain.stations.size() - 1),
					"Train directions are not reversed.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
	}
	
	@Test
	public void initializingTheTrainShouldStartTheTrainThread() {
		Train train = null;
		try {
			train = new Train("616", "Mangala Lakshadweep Express", "TowardsHome");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		assertEquals(Thread.State.RUNNABLE, train.getState());
	}
		
}