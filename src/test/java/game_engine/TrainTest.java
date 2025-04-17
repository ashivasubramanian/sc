package game_engine;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import game_engine.train.initializers.TrainFactory;
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

	private Map<String, Integer> stationDistanceMap;

	@BeforeEach
	public void setup() {
		this.stationDistanceMap = new HashMap<>();
		this.stationDistanceMap.put("CAL", 0);
		this.stationDistanceMap.put("TIR", 41);
		this.stationDistanceMap.put("SRR", 86);

	}
	
	@Test
	public void initializingTrainWithLegalValuesShouldPass() {
		Train train = null;
		try {
			train = new TrainFactory().create("616", "Mangala Lakshadweep Express", "TowardsHome", this.stationDistanceMap);
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
			homeTrain = new TrainFactory().create("616", "Mangala Lakshadweep Express", "TowardsHome", this.stationDistanceMap);
			awayTrain = new TrainFactory().create("2653", "Mangala Lakshadweep Express", "AwayFromHome", this.stationDistanceMap);
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
			Train someTrain = new TrainFactory().create("616","Calicut Shoranur Passenger", "AwayFromHome", this.stationDistanceMap);
			assertEquals(3, someTrain.getScheduledStops().size());
			assertEquals("CAL", someTrain.getScheduledStops().get(0).getStationCode());
			assertEquals("TIR", someTrain.getScheduledStops().get(1).getStationCode());
			assertEquals("SRR", someTrain.getScheduledStops().get(2).getStationCode());
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
			Train homeTrain = new TrainFactory().create("616", "Calicut Shoranur Passenger", "TowardsHome", this.stationDistanceMap);
			Train awayTrain = new TrainFactory().create("2653", "Kerala Sampark Kranti Express", "AwayFromHome", this.stationDistanceMap);
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
			Train homeTrain = new TrainFactory().create("616", "Calicut Shoranur Passenger", "TowardsHome", this.stationDistanceMap);
			TrainSchedule calicutStop = homeTrain.getScheduledStops().stream()
					.filter(stop -> stop.getStationCode().equals("CAL")).findFirst().get();
			assertEquals("19:00", calicutStop.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
					"Train arrival time is incorrect.");
			assertEquals("19:05", calicutStop.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")),
					"Train departure time is incorrect.");
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
    }
	
	@Test
	public void initializingTheTrainShouldStartTheTrainThread() {
		Train train = null;
		try {
			train = new TrainFactory().create("616", "Mangala Lakshadweep Express", "TowardsHome", this.stationDistanceMap);
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
		Arguments.argumentSet("AwayFromHomeTrain at final station", "2653", "AwayFromHome", "%1$04d-%2$02d-%3$02dT06:02:00Z", 86),
		Arguments.argumentSet("AwayFromHomeTrain at intermediate station", "2653", "AwayFromHome", "%1$04d-%2$02d-%3$02dT05:50:00Z", 41),
		Arguments.argumentSet("TowardsHomeTrain at final station", "616", "TowardsHome", "%1$04d-%2$02d-%3$02dT13:35:00Z", 0),
		Arguments.argumentSet("TowardsHomeTrain at intermediate station", "616", "TowardsHome", "%1$04d-%2$02d-%3$02dT13:05:00Z", 41)
	);

	@ParameterizedTest
	@FieldSource("argumentSetForTrainsAtStations")
	public void shouldDetermineTrainIsAtStationOnGameLoad(String trainNo, String direction, String mockTimeStringValue, int distance) throws IOException, ParserConfigurationException, SAXException {
		LocalDateTime now = LocalDateTime.now();
		String mockTimeString = String.format(mockTimeStringValue,
				now.getYear(), now.getMonthValue(), now.getDayOfMonth());
		Clock mockClock = Clock.fixed(Instant.parse(mockTimeString), ZoneId.of("+05:30"));

		Train train = new TrainFactory().createWithMockTime(trainNo, "Dummy name", direction, this.stationDistanceMap, mockClock);
		assertEquals(TrainRunningStatus.SCHEDULED_STOP, train.getTrainPosition().getTrainRunningStatus());
		assertEquals(distance, train.getTrainPosition().getDistanceFromHome());
	}

   public static List<Arguments> argumentSetForTrainPositionTest = Arrays.asList(
	   Arguments.argumentSet("AwayFromHomeTrainIsOnTheSectionBetweenStations", "2653", "AwayFromHome", "%1$04d-%2$02d-%3$02dT05:10:00Z", TrainRunningStatus.RUNNING_BETWEEN, 4.555556f),
	   Arguments.argumentSet("AwayFromHomeTrainIsEnteringSection", "2653", "AwayFromHome", "%1$04d-%2$02d-%3$02dT04:50:00Z", TrainRunningStatus.RUNNING_BETWEEN, 10),
	   Arguments.argumentSet("AwayFromHomeTrainIsExitingSection", "2653", "AwayFromHome", "%1$04d-%2$02d-%3$02dT06:10:00Z", TrainRunningStatus.RUNNING_BETWEEN, 5),
	   Arguments.argumentSet("TowardsHomeTrainIsOnTheSectionBetweenStations", "616", "TowardsHome", "%1$04d-%2$02d-%3$02dT12:40:00Z", TrainRunningStatus.RUNNING_BETWEEN, 77f),
	   Arguments.argumentSet("TowardsHomeTrainIsEnteringSection", "616", "TowardsHome", "%1$04d-%2$02d-%3$02dT12:20:00Z", TrainRunningStatus.RUNNING_BETWEEN, 10f),
	   Arguments.argumentSet("TowardsHomeTrainIsExitingSection", "616", "TowardsHome", "%1$04d-%2$02d-%3$02dT13:45:00Z", TrainRunningStatus.RUNNING_BETWEEN, 10f)
   );

	@ParameterizedTest
	@FieldSource("argumentSetForTrainPositionTest")
	public void shouldDetermineTrainPositionIsCorrectlySetAndDistanceIsCorrectlyCalculated(String trainNo, String direction,
		   String currentTime, TrainRunningStatus expectedStatus, float expectedDistance) throws Exception {
		LocalDateTime now = LocalDateTime.now();
		String mockTimeString = String.format(currentTime, now.getYear(), now.getMonthValue(), now.getDayOfMonth());
		Clock mockClock = Clock.fixed(Instant.parse(mockTimeString), ZoneId.of("+05:30"));

		Train train = new TrainFactory().createWithMockTime(trainNo, "Dummy name", direction, this.stationDistanceMap, mockClock);
		assertEquals(expectedStatus, train.getTrainPosition().getTrainRunningStatus());
		assertEquals(expectedDistance, train.getTrainPosition().getDistanceFromHome());
	}
}