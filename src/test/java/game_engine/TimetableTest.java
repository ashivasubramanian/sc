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

    @BeforeEach
    public void initializeSection() {
        stationsOnSection = new ArrayList<>();
        calicut = new Station("CAL", "Calicut", 3, 0);
        tirur = new Station("TIR", "Tirur", 2, 41);
        shoranur = new Station("SRR", "Shoranur Junction", 3, 86);
        stationsOnSection.add(calicut);
        stationsOnSection.add(tirur);
        stationsOnSection.add(shoranur);
    }

    @Test
    public void shouldContainAllStationsOnSectionOnInitializationWithEmptySchedule() {
        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.TOWARDS_HOME);
        assertEquals(3, timetable.getEntries().size());
        assertEquals(Optional.empty(), timetable.getEntries().get(0).getSchedule());
        assertEquals(Optional.empty(), timetable.getEntries().get(1).getSchedule());
        assertEquals(Optional.empty(), timetable.getEntries().get(2).getSchedule());
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
    public void shouldUpdateTimetableWithSchedule() {
        LocalDateTime arrivalTime = LocalDateTime.of(2020, 5, 22, 23, 5);
        LocalDateTime departureTime = LocalDateTime.of(2020, 5, 23, 0, 5);
        Timetable timetable = new Timetable(stationsOnSection, TrainDirection.AWAY_FROM_HOME);
        Timetable.Entry tirurEntry = timetable.getEntries().stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase("TIR"))
                .findFirst().get();
        assertFalse(tirurEntry.getSchedule().isPresent());
        assertFalse(tirurEntry.getSchedule().isPresent());

        timetable.update(tirur, arrivalTime, departureTime);
        tirurEntry = timetable.getEntries().stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase("TIR"))
                .findFirst().get();
        assertEquals(arrivalTime, tirurEntry.getSchedule().get().getArrivalTime());
        assertEquals(departureTime, tirurEntry.getSchedule().get().getDepartureTime());
    }
}