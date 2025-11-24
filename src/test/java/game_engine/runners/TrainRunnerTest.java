package game_engine.runners;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import game_engine.*;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class TrainRunnerTest {

    @Test
    public void shouldCalculateDistanceFromSectionEntryStationBasedOnCurrentTime() throws GameNotStartedException {
        TrainPosition trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN, 5f);
        Station shoranur = new Station("SRR", "Shoranur", 3, 20);
        Station tirur = new Station("TIR", "Tirur", 3, 10);
        Station calicut = new Station("CAL", "Calicut", 3, 0);
        List<Station> stations = new ArrayList<>() {{add(shoranur); add(tirur); add(calicut);}};
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.of(currentTime.getYear(), currentTime.getMonthValue(), currentTime.getDayOfMonth(), 11, 5);
        LocalDateTime departureTime = LocalDateTime.of(currentTime.getYear(), currentTime.getMonthValue(), currentTime.getDayOfMonth(), 11, 10);
        List<Entry> stops = new ArrayList<>();
        stops.add(new Entry(shoranur, Optional.of(new TrainSchedule(arrivalTime, departureTime)), StopType.NORMAL_STATION));
        stops.add(new Entry(tirur, Optional.of(new TrainSchedule(arrivalTime.plusHours(1), departureTime.plusHours(1))), StopType.NORMAL_STATION));
        stops.add(new Entry(calicut, Optional.of(new TrainSchedule(arrivalTime.plusHours(2), departureTime.plusHours(2))), StopType.NORMAL_STATION));
        Timetable timetable = new Timetable(stations, stops, TrainDirection.TOWARDS_HOME);

        LocalDateTime mockTime = LocalDateTime.of(
                currentTime.getYear(), currentTime.getMonthValue(), currentTime.getDayOfMonth(),
                11, 30);
        try (MockedStatic<LocalDateTime> mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockLocalDateTime.when(LocalDateTime::now).thenReturn(mockTime);
            mockLocalDateTime.when(() -> LocalDateTime.from(mockTime)).thenReturn(mockTime);
            TrainRunner runner = new TrainRunner(timetable, trainPosition);
            runner.run();
            assertEquals(TrainRunningStatus.RUNNING_BETWEEN, trainPosition.getTrainRunningStatus());
            assertEquals(25f, trainPosition.getDistanceFromHome());
        }
    }

}