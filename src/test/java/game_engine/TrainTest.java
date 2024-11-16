package game_engine;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
			Train someTrain = new Train("616","Calicut Shoranur Passenger", "AwayFromHome");
			assertEquals(2, someTrain.getScheduledStops().size());
			assertEquals("CAL", someTrain.getScheduledStops().get(0).getStationCode());
			assertEquals("SRR", someTrain.getScheduledStops().get(1).getStationCode());
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
			Train homeTrain = new Train("616", "Calicut Shoranur Passenger", "TowardsHome");
			Train awayTrain = new Train("2653", "Kerala Sampark Kranti Express", "AwayFromHome");
			assertEquals(homeTrain.getScheduledStops().get(0).getStationCode(),
					awayTrain.getScheduledStops().get(awayTrain.getScheduledStops().size() - 1).getStationCode(),
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
	public void arrivalAndDepartureTimesAtStationsMustBePopulatedOnLoad() {
		try {
			Train homeTrain = new Train("616", "Calicut Shoranur Passenger", "TowardsHome");
			TrainSchedule calicutStop = homeTrain.getScheduledStops().stream()
					.filter(stop -> stop.getStationCode().equals("CAL")).findFirst().get();
			assertEquals("00:00", calicutStop.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
					"Train arrival time is incorrect.");
			assertEquals("00:05", calicutStop.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")),
					"Train departure  time is incorrect.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}	}
	
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

	@Test
	public void shouldDetermineTrainIsAtStationOnGameLoad() throws IOException, ParserConfigurationException, SAXException {
		LocalDateTime now = LocalDateTime.now();
		String mockTimeStringAtIntermediateStation = String.format("%1$s-%2$s-%3$sT05:16:00Z",
				now.getYear(), now.getMonthValue(), now.getDayOfMonth());
		Clock mockClockAtIntermediateStation = Clock.fixed(Instant.parse(mockTimeStringAtIntermediateStation), ZoneId.of("+05:30"));
		String mockTimeStringAtDestination = String.format("%1$s-%2$s-%3$sT05:32:00Z",
				now.getYear(), now.getMonthValue(), now.getDayOfMonth());
		Clock mockClockAtDestination = Clock.fixed(Instant.parse(mockTimeStringAtDestination), ZoneId.of("+05:30"));

		Train trainAtIntermediateStation = new Train(mockClockAtIntermediateStation, "2653", "Mangala Lakshadweep Express", "TowardsHome");
		assertEquals(TrainRunningStatus.SCHEDULED_STOP, trainAtIntermediateStation.getTrainPosition().getTrainRunningStatus());
//		assertEquals(41, trainAtIntermediateStation.getDistance());
		Train trainAtDestination = new Train(mockClockAtDestination, "2653", "Mangala Lakshadweep Express", "TowardsHome");
		assertEquals(TrainRunningStatus.SCHEDULED_STOP, trainAtDestination.getTrainPosition().getTrainRunningStatus());
//		assertEquals(86, trainAtDestination.getDistance());
	}
		
}