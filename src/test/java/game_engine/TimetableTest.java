package game_engine;

import common.models.TrainDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TimetableTest {

    private List<Station> stationsOnSection;

    private Station calicut;

    private Station tirur;

    private Station shoranur;

    private Station kallayi;

    private Station ferok;

    @BeforeEach
    public void initializeSection() {
        stationsOnSection = new ArrayList<>();
        calicut = new Station("CAL", "Calicut", 3, 0);
        kallayi = new Station("KAL", "", 0, 1);
        ferok = new Station("FER", "", 0, 9);
        tirur = new Station("TIR", "Tirur", 2, 41);
        shoranur = new Station("SRR", "Shoranur Junction", 3, 86);
        stationsOnSection.add(calicut);
        stationsOnSection.add(kallayi);
        stationsOnSection.add(ferok);
        stationsOnSection.add(tirur);
        stationsOnSection.add(shoranur);
    }

    @Test
    public void shouldContainAllStationsOnSectionOnInitializationWithEmptySchedule() {
        Timetable timetable = new Timetable(stationsOnSection, List.of(), TrainDirection.TOWARDS_HOME);
        assertEquals(5, timetable.getEntries().size());
        assertEquals(Optional.empty(), timetable.getEntries().get(0).getSchedule());
        assertEquals(Optional.empty(), timetable.getEntries().get(1).getSchedule());
        assertEquals(Optional.empty(), timetable.getEntries().get(2).getSchedule());
        assertEquals(Optional.empty(), timetable.getEntries().get(3).getSchedule());
        assertEquals(Optional.empty(), timetable.getEntries().get(4).getSchedule());
    }

    @Test
    public void shouldArrangeStationsAsPerDistance() {
        stationsOnSection.clear();
        stationsOnSection.add(tirur);
        stationsOnSection.add(shoranur);
        stationsOnSection.add(calicut);

        Timetable timetable = new Timetable(stationsOnSection, List.of(), TrainDirection.AWAY_FROM_HOME);
        assertEquals(calicut, timetable.getEntries().get(0).getStation());
        assertEquals(tirur, timetable.getEntries().get(1).getStation());
        assertEquals(shoranur, timetable.getEntries().get(2).getStation());
        Timetable timetableTowardsHome = new Timetable(stationsOnSection, List.of(), TrainDirection.TOWARDS_HOME);
        assertEquals(shoranur, timetableTowardsHome.getEntries().get(0).getStation());
        assertEquals(tirur, timetableTowardsHome.getEntries().get(1).getStation());
        assertEquals(calicut, timetableTowardsHome.getEntries().get(2).getStation());
    }

    @Test
    public void shouldUpdateTimetableWithSchedule() {
        LocalDateTime arrivalTime = LocalDateTime.of(2020, 5, 22, 23, 5);
        LocalDateTime departureTime = LocalDateTime.of(2020, 5, 23, 23, 35);
        Entry tirurEntry = new Entry(tirur, Optional.of(new TrainSchedule(arrivalTime, departureTime)), StopType.NORMAL_STATION);
        Timetable timetable = new Timetable(stationsOnSection, List.of(tirurEntry), TrainDirection.AWAY_FROM_HOME);

        tirurEntry = timetable.getEntries().stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase("TIR"))
                .findFirst().get();
        assertEquals(arrivalTime, tirurEntry.getSchedule().get().getArrivalTime());
        assertEquals(departureTime, tirurEntry.getSchedule().get().getDepartureTime());
    }

    @Test
    public void shouldIncrementDepartureDateIfOvernightStop() {
        LocalDateTime arrivalTime = LocalDateTime.of(2020, 5, 22, 23, 55);
        LocalDateTime departureTime = LocalDateTime.of(2020, 5, 22, 0, 5);
        Entry tirurEntry = new Entry(tirur, Optional.of(new TrainSchedule(arrivalTime, departureTime)), StopType.NORMAL_STATION);
        Timetable timetable = new Timetable(stationsOnSection, List.of(tirurEntry), TrainDirection.AWAY_FROM_HOME);

        tirurEntry = timetable.getEntries().stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase("TIR"))
                .findFirst().get();
        assertEquals(22, tirurEntry.getSchedule().get().getArrivalTime().getDayOfMonth());
        assertEquals(23, tirurEntry.getSchedule().get().getDepartureTime().getDayOfMonth());
    }

    @Test
    public void shouldIncrementArrivalAndDepartureDateIfTimeHasCrossedMidnight() {
        LocalDateTime arrivalTime = LocalDateTime.of(2020, 5, 22, 23, 45);
        LocalDateTime departureTime = LocalDateTime.of(2020, 5, 22, 23, 55);
        LocalDateTime arrivalTimeAfterMidnight = LocalDateTime.of(2020, 5, 22, 0, 5);
        LocalDateTime departureTimeAfterMidnight = LocalDateTime.of(2020, 5, 22, 0, 15);
        Entry tirurEntry = new Entry(tirur, Optional.of(new TrainSchedule(arrivalTime, departureTime)), StopType.NORMAL_STATION);
        Entry shoranurEntry = new Entry(shoranur,
                Optional.of(new TrainSchedule(arrivalTimeAfterMidnight, departureTimeAfterMidnight)), StopType.NORMAL_STATION);

        Timetable timetable = new Timetable(stationsOnSection, List.of(tirurEntry, shoranurEntry), TrainDirection.AWAY_FROM_HOME);
        shoranurEntry = timetable.getEntries().stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase("SRR"))
                .findFirst().get();
        assertEquals(23, shoranurEntry.getSchedule().get().getArrivalTime().getDayOfMonth());
        assertEquals(23, shoranurEntry.getSchedule().get().getDepartureTime().getDayOfMonth());
    }

    @Test
    public void shouldNotIncrementArrivalAndDepartureDateIfThereAreNoPreviousStopsToCompareAgainst() {
        LocalDateTime arrivalTimeAfterMidnight = LocalDateTime.of(2020, 5, 22, 0, 5);
        LocalDateTime departureTimeAfterMidnight = LocalDateTime.of(2020, 5, 22, 0, 15);
        Entry shoranurEntry = new Entry(shoranur,
                Optional.of(new TrainSchedule(arrivalTimeAfterMidnight, departureTimeAfterMidnight)), StopType.NORMAL_STATION);

        Timetable timetable = new Timetable(stationsOnSection, List.of(shoranurEntry), TrainDirection.AWAY_FROM_HOME);
        shoranurEntry = timetable.getEntries().stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase("SRR"))
                .findFirst().get();
        assertEquals(22, shoranurEntry.getSchedule().get().getArrivalTime().getDayOfMonth());
        assertEquals(22, shoranurEntry.getSchedule().get().getDepartureTime().getDayOfMonth());
    }

    @Test
    public void shouldReturnFirstStationArrivalTime() {
        LocalDateTime arrivalTime = LocalDateTime.of(2020, 5, 22, 0, 5);
        LocalDateTime departureTime = LocalDateTime.of(2020, 5, 22, 0, 15);
        Entry calicutEntry = new Entry(calicut, Optional.of(new TrainSchedule(arrivalTime, departureTime)), StopType.NORMAL_STATION);
        Timetable timetable = new Timetable(stationsOnSection, List.of(calicutEntry), TrainDirection.AWAY_FROM_HOME);
        assertEquals(arrivalTime, timetable.getSectionEntryTime());

        Entry shoranurEntry = new Entry(shoranur, Optional.of(new TrainSchedule(arrivalTime, departureTime)), StopType.NORMAL_STATION);
        Timetable timetableTowardsHome = new Timetable(stationsOnSection, List.of(shoranurEntry), TrainDirection.TOWARDS_HOME);
        assertEquals(arrivalTime, timetableTowardsHome.getSectionEntryTime());
    }

    @Test
    public void shouldReturnLastStationDepartureTime() {
        LocalDateTime arrivalTime = LocalDateTime.of(2020, 5, 22, 0, 5);
        LocalDateTime departureTime = LocalDateTime.of(2020, 5, 22, 0, 15);
        Entry shoranurEntry = new Entry(shoranur, Optional.of(new TrainSchedule(arrivalTime, departureTime)), StopType.NORMAL_STATION);
        Timetable timetable = new Timetable(stationsOnSection, List.of(shoranurEntry), TrainDirection.AWAY_FROM_HOME);
        assertEquals(departureTime, timetable.getSectionExitTime());

        Entry calicutEntry = new Entry(calicut, Optional.of(new TrainSchedule(arrivalTime, departureTime)), StopType.NORMAL_STATION);
        Timetable timetableTowardsHome = new Timetable(stationsOnSection, List.of(calicutEntry), TrainDirection.TOWARDS_HOME);
        assertEquals(departureTime, timetableTowardsHome.getSectionExitTime());
    }

    @Test
    public void shouldReturnTheStationIfTrainIsAtStation() {
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
        LocalDateTime mockTime = LocalDateTime.of(year, month, day, 15, 32);
        LocalDateTime arrivalTime = LocalDateTime.of(year, month, day, 15, 30);
        LocalDateTime departureTime = LocalDateTime.of(year, month, day, 15, 35);

        Entry tirurEntry = new Entry(tirur, Optional.of(new TrainSchedule(arrivalTime, departureTime)), StopType.NORMAL_STATION);
        Timetable timetable = new Timetable(stationsOnSection, List.of(tirurEntry), TrainDirection.TOWARDS_HOME);
        assertEquals(tirur, timetable.getStationHaltedAt(mockTime).get());
    }

    @Test
    public void shouldReturnEmptyIfTrainIsNotAtAnyStation() {
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
        LocalDateTime mockTime = LocalDateTime.of(year, month, day, 15, 40);
        LocalDateTime arrivalTime = LocalDateTime.of(year, month, day, 15, 30);
        LocalDateTime departureTime = LocalDateTime.of(year, month, day, 15, 35);

        Entry tirurEntry = new Entry(tirur, Optional.of(new TrainSchedule(arrivalTime, departureTime)), StopType.NORMAL_STATION);
        Timetable timetable = new Timetable(stationsOnSection, List.of(tirurEntry), TrainDirection.TOWARDS_HOME);
        assertEquals(Optional.empty(), timetable.getStationHaltedAt(mockTime));
    }

    @Test
    public void shouldReturnTheStationsTheTrainIsRunningBetween() {
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();

        Entry shoranurEntry = new Entry(shoranur, Optional.of( new TrainSchedule(
                LocalDateTime.of(year, month, day, 20, 5), LocalDateTime.of(year, month, day, 20, 10))),
                StopType.NORMAL_STATION);
        Entry tirurEntry = new Entry(tirur, Optional.of( new TrainSchedule(
                LocalDateTime.of(year, month, day, 20, 25), LocalDateTime.of(year, month, day, 20, 26))),
                StopType.NORMAL_STATION);
        Entry calicutEntry = new Entry(calicut, Optional.of( new TrainSchedule(
                LocalDateTime.of(year, month, day, 20, 55), LocalDateTime.of(year, month, day, 21, 0))),
                StopType.NORMAL_STATION);

        Timetable timetable = new Timetable(stationsOnSection, List.of(shoranurEntry, tirurEntry, calicutEntry),
                TrainDirection.TOWARDS_HOME);
        LocalDateTime mockTime = LocalDateTime.of(year, month, day, 20, 15);
        Optional<Station>[] stations = timetable.getStationsTravellingBetween(mockTime);
        assertEquals(2, stations.length);
        assertEquals(shoranur, stations[0].get());
        assertEquals(tirur, stations[1].get());
    }

    @Test
    public void shouldReturnAnEmptyArrayIfTheTrainIsNotRunningBetweenAnyStationsOnTheSection() {
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();

        Entry shoranurEntry = new Entry(shoranur, Optional.of(new TrainSchedule(
                LocalDateTime.of(year, month, day, 20, 5), LocalDateTime.of(year, month, day, 20, 10))),
                StopType.NORMAL_STATION);
        Entry tirurEntry = new Entry(tirur, Optional.of(new TrainSchedule(
                LocalDateTime.of(year, month, day, 20, 25), LocalDateTime.of(year, month, day, 20, 26))),
                StopType.NORMAL_STATION);
        Entry calicutEntry = new Entry(calicut, Optional.of(new TrainSchedule(
                LocalDateTime.of(year, month, day, 20, 55), LocalDateTime.of(year, month, day, 21, 0))),
                StopType.NORMAL_STATION);
        Timetable timetable = new Timetable(stationsOnSection, List.of(shoranurEntry, tirurEntry, calicutEntry), TrainDirection.TOWARDS_HOME);

        LocalDateTime mockTime = LocalDateTime.of(year, month, day, 21, 15);
        Optional<Station>[] stations = timetable.getStationsTravellingBetween(mockTime);
        assertEquals(2, stations.length);
        assertEquals(Optional.empty(), stations[0]);
        assertEquals(Optional.empty(), stations[1]);
    }

    @Test
    public void shouldReturnScheduleForStation() {
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
        LocalDateTime arrivalTime = LocalDateTime.of(year, month, day, 20, 5);
        LocalDateTime departureTime = LocalDateTime.of(year, month, day, 20, 10);
        Entry shoranurEntry = new Entry(shoranur, Optional.of(new TrainSchedule(arrivalTime, departureTime)), StopType.NORMAL_STATION);
        Timetable timetable = new Timetable(stationsOnSection, List.of(shoranurEntry), TrainDirection.TOWARDS_HOME);

        assertEquals(Optional.empty(), timetable.getSchedule(tirur));
        assertEquals(arrivalTime, timetable.getSchedule(shoranur).get().getArrivalTime());
        assertEquals(departureTime, timetable.getSchedule(shoranur).get().getDepartureTime());
    }

    @Test
    public void shouldThrowAnErrorIfAStationIsBothOriginatingAndTerminatingStation() {
        Timetable timetable = new Timetable(stationsOnSection, List.of(), TrainDirection.TOWARDS_HOME);
        LocalDateTime arrivalTime = LocalDateTime.now();
        LocalDateTime departureTime = arrivalTime.plusMinutes(1);

        assertThrows(GameNotStartedException.class,
                () -> timetable.update(shoranur, arrivalTime, departureTime, true, true),
                "Shoranur cannot be both originating and terminating station for a train.");
    }

    @Test
    public void shouldReturnFullTimetableForAFullSectionTrainThatHasNotYetEnteredTheSection() {
        LocalDateTime now = LocalDateTime.now();
        Entry calicutEntry = new Entry(calicut, Optional.of(new TrainSchedule(now, now.plusMinutes(2))), StopType.NORMAL_STATION);
        Entry shoranurEntry = new Entry(shoranur, Optional.of(new TrainSchedule(now.plusHours(1), now.plusHours(1).plusMinutes(2))),
                StopType.NORMAL_STATION);
        Timetable timetableForFullSectionTrain = new Timetable(this.stationsOnSection, List.of(calicutEntry, shoranurEntry),
                TrainDirection.AWAY_FROM_HOME);

        List<Station> upcomingStations = timetableForFullSectionTrain.getUpcomingStops(now.minusMinutes(1));
        assertEquals(2, upcomingStations.size());
        assertEquals(calicut, upcomingStations.get(0));
        assertEquals(shoranur, upcomingStations.get(1));
    }

    @Test
    public void shouldReturnCurrentlyHaltedStationInUpcomingStations() {
        LocalDateTime now = LocalDateTime.now();
        Entry calicutEntry = new Entry(calicut, Optional.of(new TrainSchedule(now.minusMinutes(1), now.plusMinutes(2))),
                StopType.NORMAL_STATION);
        Entry shoranurEntry = new Entry(shoranur, Optional.of(new TrainSchedule(now.plusHours(1), now.plusHours(1).plusMinutes(2))),
                StopType.NORMAL_STATION);
        Timetable timetableForFullSectionTrain = new Timetable(this.stationsOnSection, List.of(calicutEntry, shoranurEntry),
                TrainDirection.AWAY_FROM_HOME);

        List<Station> upcomingStations = timetableForFullSectionTrain.getUpcomingStops(now);
        assertEquals(2, upcomingStations.size());
        assertEquals(calicut, upcomingStations.get(0));
        assertEquals(shoranur, upcomingStations.get(1));
    }

    @Test
    public void shouldReturnCorrectUpcomingStationsWhenTravellingBetweenStations() {
        LocalDateTime now = LocalDateTime.now();
        Entry calicutEntry = new Entry(calicut, Optional.of(new TrainSchedule(now.minusMinutes(10), now.minusMinutes(5))),
                StopType.NORMAL_STATION);
        Entry tirurEntry = new Entry(tirur, Optional.of(new TrainSchedule(now.plusMinutes(30), now.plusMinutes(32))),
                StopType.NORMAL_STATION);
        Entry shoranurEntry = new Entry(shoranur, Optional.of(new TrainSchedule(now.plusHours(1), now.plusHours(1).plusMinutes(2))),
                StopType.NORMAL_STATION);
        Timetable timetableForFullSectionTrain = new Timetable(this.stationsOnSection,
                List.of(calicutEntry, tirurEntry, shoranurEntry), TrainDirection.AWAY_FROM_HOME);

        List<Station> upcomingStations = timetableForFullSectionTrain.getUpcomingStops(now);
        assertEquals(2, upcomingStations.size());
        assertEquals(tirur, upcomingStations.get(0));
        assertEquals(shoranur, upcomingStations.get(1));
    }

    //TODO: Convert this into a test for limiting timetable entries. Ignore for upcomingstops.
    @Test
    public void shouldReturnCorrectUpcomingStationsForTrainsOriginatingAndTerminatingWithinSection() throws GameNotStartedException {
        LocalDateTime now = LocalDateTime.now();
        Entry kallayiEntry = new Entry(kallayi, Optional.of(new TrainSchedule(now.plusMinutes(1), now.plusMinutes(5))),
                StopType.NORMAL_STATION);
        Entry ferokEntry = new Entry(ferok, Optional.of(new TrainSchedule(now.plusMinutes(10), now.plusMinutes(12))),
                StopType.NORMAL_STATION);
        Entry tirurEntry = new Entry(tirur, Optional.of(new TrainSchedule(now.plusHours(30), now.plusMinutes(32))),
                StopType.NORMAL_STATION);
        Timetable timetableForFullSectionTrain = new Timetable(this.stationsOnSection,
                List.of(kallayiEntry, ferokEntry, tirurEntry), TrainDirection.AWAY_FROM_HOME);

        List<Station> upcomingStations = timetableForFullSectionTrain.getUpcomingStops(now);
        assertEquals(3, upcomingStations.size());
        assertEquals(kallayi, upcomingStations.get(0));
        assertEquals(ferok, upcomingStations.get(1));
        assertEquals(tirur, upcomingStations.get(2));
    }
}