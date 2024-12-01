package game_engine;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrainTest {

	private Map<String, Float> stationDistanceMap;

	@BeforeEach
	public void setup() {
		this.stationDistanceMap = new HashMap<>();
		this.stationDistanceMap.put("CAL", 0f);
		this.stationDistanceMap.put("TIR", 41f);
		this.stationDistanceMap.put("SRR", 86f);

	}
	
	@Test
	public void initializingTrainWithLegalValuesShouldPass() {
		Train train = null;
		try {
			train = new Train("616", "Mangala Lakshadweep Express", "TowardsHome", this.stationDistanceMap);
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
			homeTrain = new Train("616", "Mangala Lakshadweep Express", "TowardsHome", this.stationDistanceMap);
			awayTrain = new Train("2653", "Mangala Lakshadweep Express", "AwayFromHome", this.stationDistanceMap);
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
			Train someTrain = new Train("616","Calicut Shoranur Passenger", "AwayFromHome", this.stationDistanceMap);
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
			Train homeTrain = new Train("616", "Calicut Shoranur Passenger", "TowardsHome", this.stationDistanceMap);
			Train awayTrain = new Train("2653", "Kerala Sampark Kranti Express", "AwayFromHome", this.stationDistanceMap);
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
			Train homeTrain = new Train("616", "Calicut Shoranur Passenger", "TowardsHome", this.stationDistanceMap);
			TrainSchedule calicutStop = homeTrain.getScheduledStops().stream()
					.filter(stop -> stop.getStationCode().equals("CAL")).findFirst().get();
			assertEquals("00:00", calicutStop.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
					"Train arrival time is incorrect.");
			assertEquals("00:05", calicutStop.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")),
					"Train departure time is incorrect.");
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
			train = new Train("616", "Mangala Lakshadweep Express", "TowardsHome", this.stationDistanceMap);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		assertEquals(Thread.State.RUNNABLE, train.getState());
	}

	public static List<Arguments> argumentSetForTrainsAtStations = Arrays.asList(
			Arguments.argumentSet("Train at final station", "%1$04d-%2$02d-%3$02dT06:02:00Z", 86),
			Arguments.argumentSet("Train at intermediate station", "%1$04d-%2$02d-%3$02dT05:50:00Z", 41)
	);

	@ParameterizedTest
	@FieldSource("argumentSetForTrainsAtStations")
	public void shouldDetermineTrainIsAtStationOnGameLoad(String mockTimeStringValue, int distance) throws IOException, ParserConfigurationException, SAXException {
		LocalDateTime now = LocalDateTime.now();
		String mockTimeString = String.format(mockTimeStringValue,
				now.getYear(), now.getMonthValue(), now.getDayOfMonth());
		Clock mockClock = Clock.fixed(Instant.parse(mockTimeString), ZoneId.of("+05:30"));

		Train train = new Train(mockClock, "2653", "Mangala Lakshadweep Express", "TowardsHome", this.stationDistanceMap);
		assertEquals(TrainRunningStatus.SCHEDULED_STOP, train.getTrainPosition().getTrainRunningStatus());
		assertEquals(distance, train.getTrainPosition().getDistanceFromHome());
	}

	@Test
	public void shouldDetermineTrainIsInBetweenStationsOnGameLoad() throws Exception {
		LocalDateTime now = LocalDateTime.now();
		String mockTimeString = String.format("%1$04d-%2$02d-%3$02dT05:10:00Z",
				now.getYear(), now.getMonthValue(), now.getDayOfMonth());
		Clock mockClock = Clock.fixed(Instant.parse(mockTimeString), ZoneId.of("+05:30"));

		Train train = new Train(mockClock, "2653", "Mangala Lakshadweep Express", "AwayFromHome", this.stationDistanceMap);
		assertEquals(TrainRunningStatus.RUNNING_BETWEEN, train.getTrainPosition().getTrainRunningStatus());
//		assertEquals(distance, train.getTrainPosition().getDistanceFromHome());
	}

}