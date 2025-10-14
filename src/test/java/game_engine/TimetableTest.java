package game_engine;

import common.models.TrainDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.TOWARDS_HOME);
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

        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.AWAY_FROM_HOME);
        assertEquals(calicut, timetable.getEntries().get(0).getStation());
        assertEquals(tirur, timetable.getEntries().get(1).getStation());
        assertEquals(shoranur, timetable.getEntries().get(2).getStation());
        Timetable timetableTowardsHome = new Timetable(stationsOnSection, TrainDirection.TOWARDS_HOME);
        assertEquals(shoranur, timetableTowardsHome.getEntries().get(0).getStation());
        assertEquals(tirur, timetableTowardsHome.getEntries().get(1).getStation());
        assertEquals(calicut, timetableTowardsHome.getEntries().get(2).getStation());
    }

    @Test
    public void shouldUpdateTimetableWithSchedule() throws GameNotStartedException {
        LocalDateTime arrivalTime = LocalDateTime.of(2020, 5, 22, 23, 5);
        LocalDateTime departureTime = LocalDateTime.of(2020, 5, 23, 23, 35);
        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.AWAY_FROM_HOME);
        Timetable.Entry tirurEntry = timetable.getEntries().stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase("TIR"))
                .findFirst().get();
        assertFalse(tirurEntry.getSchedule().isPresent());
        assertFalse(tirurEntry.getSchedule().isPresent());

        timetable.update(tirur, arrivalTime, departureTime, false, false);
        tirurEntry = timetable.getEntries().stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase("TIR"))
                .findFirst().get();
        assertEquals(arrivalTime, tirurEntry.getSchedule().get().getArrivalTime());
        assertEquals(departureTime, tirurEntry.getSchedule().get().getDepartureTime());
    }

    @Test
    public void shouldIncrementDepartureDateIfOvernightStop() throws GameNotStartedException {
        LocalDateTime arrivalTime = LocalDateTime.of(2020, 5, 22, 23, 55);
        LocalDateTime departureTime = LocalDateTime.of(2020, 5, 22, 0, 5);
        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.AWAY_FROM_HOME);

        timetable.update(tirur, arrivalTime, departureTime, false, false);
        Timetable.Entry tirurEntry = timetable.getEntries().stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase("TIR"))
                .findFirst().get();
        assertEquals(22, tirurEntry.getSchedule().get().getArrivalTime().getDayOfMonth());
        assertEquals(23, tirurEntry.getSchedule().get().getDepartureTime().getDayOfMonth());
    }

    @Test
    public void shouldIncrementArrivalAndDepartureDateIfTimeHasCrossedMidnight() throws GameNotStartedException {
        LocalDateTime arrivalTime = LocalDateTime.of(2020, 5, 22, 23, 45);
        LocalDateTime departureTime = LocalDateTime.of(2020, 5, 22, 23, 55);
        LocalDateTime arrivalTimeAfterMidnight = LocalDateTime.of(2020, 5, 22, 0, 5);
        LocalDateTime departureTimeAfterMidnight = LocalDateTime.of(2020, 5, 22, 0, 15);
        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.AWAY_FROM_HOME);

        timetable.update(tirur, arrivalTime, departureTime, false, false);
        timetable.update(shoranur, arrivalTimeAfterMidnight, departureTimeAfterMidnight, false, false);
        Timetable.Entry shoranurEntry = timetable.getEntries().stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase("SRR"))
                .findFirst().get();
        assertEquals(23, shoranurEntry.getSchedule().get().getArrivalTime().getDayOfMonth());
        assertEquals(23, shoranurEntry.getSchedule().get().getDepartureTime().getDayOfMonth());
    }

    @Test
    public void shouldNotIncrementArrivalAndDepartureDateIfThereAreNoPreviousStopsToCompareAgainst() throws GameNotStartedException {
        LocalDateTime arrivalTimeAfterMidnight = LocalDateTime.of(2020, 5, 22, 0, 5);
        LocalDateTime departureTimeAfterMidnight = LocalDateTime.of(2020, 5, 22, 0, 15);
        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.AWAY_FROM_HOME);

        timetable.update(shoranur, arrivalTimeAfterMidnight, departureTimeAfterMidnight, false, false);
        Timetable.Entry shoranurEntry = timetable.getEntries().stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase("SRR"))
                .findFirst().get();
        assertEquals(22, shoranurEntry.getSchedule().get().getArrivalTime().getDayOfMonth());
        assertEquals(22, shoranurEntry.getSchedule().get().getDepartureTime().getDayOfMonth());
    }

    @Test
    public void shouldReturnFirstStationArrivalTime() throws GameNotStartedException {
        LocalDateTime arrivalTime = LocalDateTime.of(2020, 5, 22, 0, 5);
        LocalDateTime departureTime = LocalDateTime.of(2020, 5, 22, 0, 15);
        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.AWAY_FROM_HOME);
        timetable.update(calicut, arrivalTime, departureTime, false, false);
        assertEquals(arrivalTime, timetable.getSectionEntryTime());

        Timetable timetableTowardsHome = new Timetable(stationsOnSection, TrainDirection.TOWARDS_HOME);
        timetableTowardsHome.update(shoranur, arrivalTime, departureTime, false, false);
        assertEquals(arrivalTime, timetableTowardsHome.getSectionEntryTime());
    }

    @Test
    public void shouldReturnLastStationDepartureTime() throws GameNotStartedException {
        LocalDateTime arrivalTime = LocalDateTime.of(2020, 5, 22, 0, 5);
        LocalDateTime departureTime = LocalDateTime.of(2020, 5, 22, 0, 15);
        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.AWAY_FROM_HOME);
        timetable.update(shoranur, arrivalTime, departureTime, false, false);
        assertEquals(departureTime, timetable.getSectionExitTime());

        Timetable timetableTowardsHome = new Timetable(stationsOnSection, TrainDirection.TOWARDS_HOME);
        timetableTowardsHome.update(calicut, arrivalTime, departureTime, false, false);
        assertEquals(departureTime, timetableTowardsHome.getSectionExitTime());
    }

    @Test
    public void shouldReturnTheStationIfTrainIsAtStation() throws GameNotStartedException {
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
        LocalDateTime mockTime = LocalDateTime.of(year, month, day, 15, 32);
        LocalDateTime arrivalTime = LocalDateTime.of(year, month, day, 15, 30);
        LocalDateTime departureTime = LocalDateTime.of(year, month, day, 15, 35);

        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.TOWARDS_HOME);
        timetable.update(tirur, arrivalTime, departureTime, false, false);

        assertEquals(tirur, timetable.getStationHaltedAt(mockTime).get());
    }

    @Test
    public void shouldReturnEmptyIfTrainIsNotAtAnyStation() throws GameNotStartedException {
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
        LocalDateTime mockTime = LocalDateTime.of(year, month, day, 15, 40);
        LocalDateTime arrivalTime = LocalDateTime.of(year, month, day, 15, 30);
        LocalDateTime departureTime = LocalDateTime.of(year, month, day, 15, 35);

        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.TOWARDS_HOME);
        timetable.update(tirur, arrivalTime, departureTime, false, false);

        assertEquals(Optional.empty(), timetable.getStationHaltedAt(mockTime));
    }

    @Test
    public void shouldReturnTheStationsTheTrainIsRunningBetween() throws GameNotStartedException {
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();

        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.TOWARDS_HOME);
        timetable.update(shoranur, LocalDateTime.of(year, month, day, 20, 5), LocalDateTime.of(year, month, day, 20, 10), false, false);
        timetable.update(tirur, LocalDateTime.of(year, month, day, 20, 25), LocalDateTime.of(year, month, day, 20, 26), false, false);
        timetable.update(calicut, LocalDateTime.of(year, month, day, 20, 55), LocalDateTime.of(year, month, day, 21, 0), false, false);

        LocalDateTime mockTime = LocalDateTime.of(year, month, day, 20, 15);
        Optional<Station>[] stations = timetable.getStationsTravellingBetween(mockTime);
        assertEquals(2, stations.length);
        assertEquals(shoranur, stations[0].get());
        assertEquals(tirur, stations[1].get());
    }

    @Test
    public void shouldReturnAnEmptyArrayIfTheTrainIsNotRunningBetweenAnyStationsOnTheSection() throws GameNotStartedException {
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();

        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.TOWARDS_HOME);
        timetable.update(shoranur, LocalDateTime.of(year, month, day, 20, 5), LocalDateTime.of(year, month, day, 20, 10), false, false);
        timetable.update(tirur, LocalDateTime.of(year, month, day, 20, 25), LocalDateTime.of(year, month, day, 20, 26), false, false);
        timetable.update(calicut, LocalDateTime.of(year, month, day, 20, 55), LocalDateTime.of(year, month, day, 21, 0), false, false);

        LocalDateTime mockTime = LocalDateTime.of(year, month, day, 21, 15);
        Optional<Station>[] stations = timetable.getStationsTravellingBetween(mockTime);
        assertEquals(2, stations.length);
        assertEquals(Optional.empty(), stations[0]);
        assertEquals(Optional.empty(), stations[1]);
    }

    @Test
    public void shouldReturnScheduleForStation() throws GameNotStartedException {
        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.TOWARDS_HOME);
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
        LocalDateTime arrivalTime = LocalDateTime.of(year, month, day, 20, 5);
        LocalDateTime departureTime = LocalDateTime.of(year, month, day, 20, 10);
        timetable.update(shoranur, arrivalTime, departureTime, false, false);

        assertEquals(Optional.empty(), timetable.getSchedule(tirur));
        assertEquals(arrivalTime, timetable.getSchedule(shoranur).get().getArrivalTime());
        assertEquals(departureTime, timetable.getSchedule(shoranur).get().getDepartureTime());
    }

    @Test
    public void shouldThrowAnErrorIfAStationIsBothOriginatingAndTerminatingStation() {
        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.TOWARDS_HOME);
        LocalDateTime arrivalTime = LocalDateTime.now();
        LocalDateTime departureTime = arrivalTime.plusMinutes(1);

        assertThrows(GameNotStartedException.class,
                () -> timetable.update(shoranur, arrivalTime, departureTime, true, true),
                "Shoranur cannot be both originating and terminating station for a train.");
    }

    @Test
    public void shouldReturnFullTimetableForAFullSectionTrainThatHasNotYetEnteredTheSection() throws GameNotStartedException {
        LocalDateTime now = LocalDateTime.now();
        Timetable timetableForFullSectionTrain = new Timetable(this.stationsOnSection, TrainDirection.AWAY_FROM_HOME);
        timetableForFullSectionTrain.update(calicut, now, now.plusMinutes(2), false, false);
        timetableForFullSectionTrain.update(shoranur, now.plusHours(1), now.plusHours(1).plusMinutes(2), false, false);

        List<Station> upcomingStations = timetableForFullSectionTrain.getUpcomingStops(now.minusMinutes(1));
        assertEquals(2, upcomingStations.size());
        assertEquals(calicut, upcomingStations.get(0));
        assertEquals(shoranur, upcomingStations.get(1));
    }

    @Test
    public void shouldReturnCurrentlyHaltedStationInUpcomingStations() throws GameNotStartedException {
        LocalDateTime now = LocalDateTime.now();
        Timetable timetableForFullSectionTrain = new Timetable(this.stationsOnSection, TrainDirection.AWAY_FROM_HOME);
        timetableForFullSectionTrain.update(calicut, now.minusMinutes(1), now.plusMinutes(2), false, false);
        timetableForFullSectionTrain.update(shoranur, now.plusHours(1), now.plusHours(1).plusMinutes(2), false, false);

        List<Station> upcomingStations = timetableForFullSectionTrain.getUpcomingStops(now);
        assertEquals(2, upcomingStations.size());
        assertEquals(calicut, upcomingStations.get(0));
        assertEquals(shoranur, upcomingStations.get(1));
    }

    @Test
    public void shouldReturnCorrectUpcomingStationsWhenTravellingBetweenStations() throws GameNotStartedException {
        LocalDateTime now = LocalDateTime.now();
        Timetable timetableForFullSectionTrain = new Timetable(this.stationsOnSection, TrainDirection.AWAY_FROM_HOME);
        timetableForFullSectionTrain.update(calicut, now.minusMinutes(10), now.minusMinutes(5), false, false);
        timetableForFullSectionTrain.update(tirur, now.plusMinutes(30), now.plusMinutes(32), false, false);
        timetableForFullSectionTrain.update(shoranur, now.plusHours(1), now.plusHours(1).plusMinutes(2), false, false);

        List<Station> upcomingStations = timetableForFullSectionTrain.getUpcomingStops(now);
        assertEquals(2, upcomingStations.size());
        assertEquals(tirur, upcomingStations.get(0));
        assertEquals(shoranur, upcomingStations.get(1));
    }

    @Test
    public void shouldReturnCorrectUpcomingStationsForTrainsOriginatingAndTerminatingWithinSection() throws GameNotStartedException {
        LocalDateTime now = LocalDateTime.now();
        Timetable timetableForFullSectionTrain = new Timetable(this.stationsOnSection, TrainDirection.AWAY_FROM_HOME);
        timetableForFullSectionTrain.update(kallayi, now.plusMinutes(1), now.plusMinutes(5), true, false);
        timetableForFullSectionTrain.update(ferok, now.plusMinutes(10), now.plusMinutes(12), false, false);
        timetableForFullSectionTrain.update(tirur, now.plusHours(30), now.plusMinutes(32), false, true);

        List<Station> upcomingStations = timetableForFullSectionTrain.getUpcomingStops(now);
        assertEquals(3, upcomingStations.size());
        assertEquals(kallayi, upcomingStations.get(0));
        assertEquals(ferok, upcomingStations.get(1));
        assertEquals(tirur, upcomingStations.get(2));
    }
}