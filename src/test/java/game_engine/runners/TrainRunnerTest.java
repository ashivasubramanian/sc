package game_engine.runners;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import game_engine.Station;
import game_engine.Timetable;
import game_engine.TrainPosition;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class TrainRunnerTest {

    @Test
    public void shouldCalculateDistanceFromSectionEntryStationBasedOnCurrentTime() {
        TrainPosition trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN, 5f);
        Station shoranur = new Station("SRR", "Shoranur", 3, 20);
        Station tirur = new Station("TIR", "Tirur", 3, 10);
        Station calicut = new Station("CAL", "Calicut", 3, 0);
        List<Station> stations = new ArrayList<>() {{add(shoranur); add(tirur); add(calicut);}};
        Timetable timetable = new Timetable(stations, TrainDirection.TOWARDS_HOME);
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.of(currentTime.getYear(), currentTime.getMonthValue(), currentTime.getDayOfMonth(), 11, 5);
        LocalDateTime departureTime = LocalDateTime.of(currentTime.getYear(), currentTime.getMonthValue(), currentTime.getDayOfMonth(), 11, 10);
        timetable.update(shoranur, arrivalTime, departureTime);
        timetable.update(tirur, arrivalTime.plusHours(1), departureTime.plusHours(1));
        timetable.update(calicut, arrivalTime.plusHours(2), departureTime.plusHours(2));

        LocalDateTime mockTime = LocalDateTime.of(
                currentTime.getYear(), currentTime.getMonthValue(), currentTime.getDayOfMonth(),
                11, 30);
        try (MockedStatic<LocalDateTime> mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockLocalDateTime.when(LocalDateTime::now).thenReturn(mockTime);
            TrainRunner runner = new TrainRunner(timetable, trainPosition);
            runner.run();
            assertEquals(TrainRunningStatus.RUNNING_BETWEEN, trainPosition.getTrainRunningStatus());
            assertEquals(25f, trainPosition.getDistanceFromHome());
        }
    }

}