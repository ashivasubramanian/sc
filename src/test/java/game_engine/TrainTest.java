package game_engine;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import game_engine.initializers.TrainFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrainTest {

	private List<Station> stations;

	@BeforeEach
	public void setup() {
		this.stations = new ArrayList<>();
		this.stations.add(new Station("CAL", "", 0, 0));
		this.stations.add(new Station("TIR", "", 0, 41));
		this.stations.add(new Station("SRR", "", 0, 86));

	}
	
	@Test
	public void initializingTrainWithLegalValuesShouldPass() {
		Train train = null;
		try {
			train = new TrainFactory().create("616", "Mangala Lakshadweep Express", "TowardsHome", this.stations);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		assertNotNull(train);
	}
	
	@Test
	public void verifyTrainDirectionIsSet() {
		Train homeTrain = null;
		Train awayTrain = null;
		try {
			homeTrain = new TrainFactory().create("616", "Mangala Lakshadweep Express", "TowardsHome", this.stations);
			awayTrain = new TrainFactory().create("2653", "Mangala Lakshadweep Express", "AwayFromHome", this.stations);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		assertEquals(TrainDirection.TOWARDS_HOME,
				homeTrain.getDirection(), "Train1 direction was not set properly.");
		assertEquals(TrainDirection.AWAY_FROM_HOME,
				awayTrain.getDirection(), "Train2 direction was not set properly.");
	}
	
	@Test
	public void timetableMustBePopulatedOnInitialization() {
		try {
			Train someTrain = new TrainFactory().create("616","Calicut Shoranur Passenger", "AwayFromHome", this.stations);
			assertNotNull(someTrain.getTimetable());
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static List<Arguments> argumentSetForTrainsAtStations = Arrays.asList(
		Arguments.argumentSet("AwayFromHomeTrain at final station", "2653", "AwayFromHome", "%1$04d-%2$02d-%3$02dT06:02:00Z", 86),
		Arguments.argumentSet("AwayFromHomeTrain at intermediate station", "2653", "AwayFromHome", "%1$04d-%2$02d-%3$02dT05:50:00Z", 41),
		Arguments.argumentSet("TowardsHomeTrain at final station", "616", "TowardsHome", "%1$04d-%2$02d-%3$02dT13:35:00Z", 0),
		Arguments.argumentSet("TowardsHomeTrain at intermediate station", "616", "TowardsHome", "%1$04d-%2$02d-%3$02dT13:05:00Z", 41),
		Arguments.argumentSet("Overnight train at station", "16356", "AwayFromHome", "%1$04d-%2$02d-%3$02dT18:31:00Z", 41)
	);

	@ParameterizedTest
	@FieldSource("argumentSetForTrainsAtStations")
	public void shouldDetermineTrainIsAtStationOnGameLoad(String trainNo, String direction, String mockTimeStringValue, int distance) throws IOException, ParserConfigurationException, SAXException {
		LocalDateTime now = LocalDateTime.now();
		String mockTimeString = String.format(mockTimeStringValue,
				now.getYear(), now.getMonthValue(), now.getDayOfMonth());
		Clock mockClock = Clock.fixed(Instant.parse(mockTimeString), ZoneId.of("+05:30"));

		Train train = new TrainFactory().createWithMockTime(trainNo, "Dummy name", direction, this.stations, mockClock);
		assertEquals(TrainRunningStatus.SCHEDULED_STOP, train.getTrainPosition().getTrainRunningStatus());
		assertEquals(distance, train.getTrainPosition().getDistanceFromHome());
	}

   public static List<Arguments> argumentSetForTrainPositionTest = Arrays.asList(
	   Arguments.argumentSet("AwayFromHomeTrainIsOnTheSectionBetweenStations", "2653", "AwayFromHome", "%1$04d-%2$02d-%3$02dT05:10:00Z", TrainRunningStatus.RUNNING_BETWEEN, 4.555556f),
	   Arguments.argumentSet("AwayFromHomeTrainIsEnteringSection", "2653", "AwayFromHome", "%1$04d-%2$02d-%3$02dT04:50:00Z", TrainRunningStatus.RUNNING_BETWEEN, 10),
	   Arguments.argumentSet("AwayFromHomeTrainIsExitingSection", "2653", "AwayFromHome", "%1$04d-%2$02d-%3$02dT06:10:00Z", TrainRunningStatus.RUNNING_BETWEEN, 5),
	   Arguments.argumentSet("TowardsHomeTrainIsOnTheSectionBetweenStations", "616", "TowardsHome", "%1$04d-%2$02d-%3$02dT12:40:00Z", TrainRunningStatus.RUNNING_BETWEEN, 77f),
	   Arguments.argumentSet("TowardsHomeTrainIsEnteringSection", "616", "TowardsHome", "%1$04d-%2$02d-%3$02dT12:20:00Z", TrainRunningStatus.RUNNING_BETWEEN, 10f),
	   Arguments.argumentSet("TowardsHomeTrainIsExitingSection", "616", "TowardsHome", "%1$04d-%2$02d-%3$02dT13:45:00Z", TrainRunningStatus.RUNNING_BETWEEN, 10f),
	   Arguments.argumentSet("OvernightTrainOnSection", "22637", "TowardsHome", "%1$04d-%2$02d-%3$02dT18:35:00Z", TrainRunningStatus.RUNNING_BETWEEN, 61f)
   );

	@ParameterizedTest
	@FieldSource("argumentSetForTrainPositionTest")
	public void shouldDetermineTrainPositionIsCorrectlySetAndDistanceIsCorrectlyCalculated(String trainNo, String direction,
		   String currentTime, TrainRunningStatus expectedStatus, float expectedDistance) throws Exception {
		LocalDateTime now = LocalDateTime.now();
		String mockTimeString = String.format(currentTime, now.getYear(), now.getMonthValue(), now.getDayOfMonth());
		Clock mockClock = Clock.fixed(Instant.parse(mockTimeString), ZoneId.of("+05:30"));

		Train train = new TrainFactory().createWithMockTime(trainNo, "Dummy name", direction, this.stations, mockClock);
		assertEquals(expectedStatus, train.getTrainPosition().getTrainRunningStatus());
		assertEquals(expectedDistance, train.getTrainPosition().getDistanceFromHome());
	}

	@Nested
	public class OvernightTrains {
		@Test
		public void overnightTrainsShouldHaveCorrectDates() throws IOException, ParserConfigurationException, SAXException {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime nextDay = LocalDateTime.now().plusDays(1);
			Train overnightTrain = new TrainFactory().create("22637", "Dummy name", "TowardsHome", stations);

			TrainSchedule beforeMidnightStop = overnightTrain.getTimetable().getSchedule(new Station("SRR", null, 0, 0)).get();
			TrainSchedule afterMidnightStop = overnightTrain.getTimetable().getSchedule(new Station("TIR", null, 0, 0)).get();
			TrainSchedule laterStop = overnightTrain.getTimetable().getSchedule(new Station("CAL", null, 0, 0)).get();
			assertEquals(now.getDayOfMonth(), beforeMidnightStop.getArrivalTime().getDayOfMonth());
			assertEquals(now.getDayOfMonth(), beforeMidnightStop.getDepartureTime().getDayOfMonth());
			assertEquals(nextDay.getDayOfMonth(), afterMidnightStop.getArrivalTime().getDayOfMonth());
			assertEquals(nextDay.getDayOfMonth(), afterMidnightStop.getDepartureTime().getDayOfMonth());
			assertEquals(nextDay.getDayOfMonth(), laterStop.getArrivalTime().getDayOfMonth());
			assertEquals(nextDay.getDayOfMonth(), laterStop.getDepartureTime().getDayOfMonth());
		}

		@Test
		public void overnightStopsShouldHaveCorrectDates() throws IOException, ParserConfigurationException, SAXException {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime nextDay = LocalDateTime.now().plusDays(1);
			Train overnightStopTrain = new TrainFactory().create("16356", "DummyTrain", "AwayFromHome", stations);

			TrainSchedule beforeMidnightStop = overnightStopTrain.getTimetable().getSchedule(new Station("CAL", null, 0, 0 )).get();
			TrainSchedule overnightStop = overnightStopTrain.getTimetable().getSchedule(new Station("TIR", null, 0, 0)).get();
			TrainSchedule afterMidnightStop = overnightStopTrain.getTimetable().getSchedule(new Station("SRR", null, 0, 0)).get();
			assertEquals(now.getDayOfMonth(), beforeMidnightStop.getArrivalTime().getDayOfMonth());
			assertEquals(now.getDayOfMonth(), beforeMidnightStop.getDepartureTime().getDayOfMonth());
			assertEquals(now.getDayOfMonth(), overnightStop.getArrivalTime().getDayOfMonth());
			assertEquals(nextDay.getDayOfMonth(), overnightStop.getDepartureTime().getDayOfMonth());
			assertEquals(nextDay.getDayOfMonth(), afterMidnightStop.getArrivalTime().getDayOfMonth());
			assertEquals(nextDay.getDayOfMonth(), afterMidnightStop.getDepartureTime().getDayOfMonth());
		}

		@Test
		public void normalTrainsHaveNoDateChanges() throws IOException, ParserConfigurationException, SAXException {
			LocalDateTime now = LocalDateTime.now();
			Train overnightStopTrain = new TrainFactory().create("616", "DummyTrain", "TowardsHome", stations);

			TrainSchedule firstStop = overnightStopTrain.getTimetable().getSchedule(new Station("SRR", null, 0, 0)).get();
			TrainSchedule secondStop = overnightStopTrain.getTimetable().getSchedule(new Station("TIR", null, 0, 0)).get();
			TrainSchedule thirdStop = overnightStopTrain.getTimetable().getSchedule(new Station("CAL", null, 0, 0)).get();
			assertEquals(now.getDayOfMonth(), firstStop.getArrivalTime().getDayOfMonth());
			assertEquals(now.getDayOfMonth(), firstStop.getDepartureTime().getDayOfMonth());
			assertEquals(now.getDayOfMonth(), secondStop.getArrivalTime().getDayOfMonth());
			assertEquals(now.getDayOfMonth(), secondStop.getDepartureTime().getDayOfMonth());
			assertEquals(now.getDayOfMonth(), thirdStop.getArrivalTime().getDayOfMonth());
			assertEquals(now.getDayOfMonth(), thirdStop.getDepartureTime().getDayOfMonth());
		}
	}
}