package game_engine;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

	@ParameterizedTest
	@CsvSource({"%1$s-%2$s-%3$sT05:16:00Z", "%1$s-%2$s-%3$sT05:32:00Z"})
	public void shouldDetermineTrainIsAtStationOnGameLoad(String mockTimeStringValue) throws IOException, ParserConfigurationException, SAXException {
		LocalDateTime now = LocalDateTime.now();
		String mockTimeString = String.format(mockTimeStringValue,
				now.getYear(), now.getMonthValue(), now.getDayOfMonth());
		Clock mockClock = Clock.fixed(Instant.parse(mockTimeString), ZoneId.of("+05:30"));

		Train train = new Train(mockClock, "2653", "Mangala Lakshadweep Express", "TowardsHome");
		assertEquals(TrainRunningStatus.SCHEDULED_STOP, train.getTrainPosition().getTrainRunningStatus());
//		assertEquals(41, train.getDistance());
	}
}