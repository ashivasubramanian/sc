package game_engine;

import common.models.SignalAspect;
import common.models.TrainDirection;
import game_engine.dto.StationDto;
import game_engine.dto.TrainDto;
import game_engine.runners.TrainRunner;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameTest {
    
    @Test
    public void shouldLoadStations() throws Exception {
        Game game = new Game();
        assertEquals(3, game.getStations().size());
    }

    @Test
    public void shouldLoadTrains() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String mockClockString = String.format("%1$04d-%2$02d-%3$02dT13:10:00Z",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        Clock mockClock = Clock.fixed(Instant.parse(mockClockString), ZoneId.of("+05:30"));

        Game game = new Game(mockClock);
        assertEquals(2, game.getTrains().size());
    }
    
    @Test
    public void shouldExposeStationDataAsImmutableCollection() throws Exception {
        Game game = new Game();
        assertThrows(UnsupportedOperationException.class,
                () -> game.getStations().add(new StationDto("new_station", 18, null)));
    }
    
    @Test
    public void shouldExposeTrainDataAsImmutableCollection() throws Exception {
        Game game = new Game();
        assertThrows(UnsupportedOperationException.class,
                () -> game.getTrains().add(new TrainDto("New Train", 100f, TrainDirection.TOWARDS_HOME)));
    }

    @Test
    public void shouldSetStationAspects() throws Exception {
        Game game = new Game();
        StationDto calicutStationDto = game.getStations().stream()
                .filter(station -> station.getName().equals("Calicut"))
                .findFirst().get();
        SignalAspect[] defaultSignalAspects = new SignalAspect[] {SignalAspect.STOP, SignalAspect.STOP};
        assertArrayEquals(defaultSignalAspects, calicutStationDto.getAspects());

        SignalAspect[] newSignalAspects = new SignalAspect[]{SignalAspect.PROCEED, SignalAspect.PROCEED};
        game.setStationAspect("Calicut", newSignalAspects[0], newSignalAspects[1]);
        calicutStationDto = game.getStations().stream()
                .filter(station -> station.getName().equals("Calicut"))
                .findFirst().get();
        assertArrayEquals(newSignalAspects, calicutStationDto.getAspects());
    }

    @Test
    public void shouldRunTrainUsingExecutor() throws GameNotStartedException {
        try (MockedStatic<Executors> executors = mockStatic(Executors.class)) {
            ScheduledExecutorService mockExecutorService = mock(ScheduledExecutorService.class);
            executors.when(Executors::newSingleThreadScheduledExecutor).thenReturn(mockExecutorService);
            Clock mockTime = Clock.fixed(Instant.parse("2025-06-23T12:35:00Z"), ZoneId.of("+05:30"));
            Game game = new Game(mockTime);

            //Only 2 trains are available at this point in time - so only 2 calls are made to the executor.
            verify(mockExecutorService, times(2)).scheduleWithFixedDelay(
                    any(TrainRunner.class), eq(2L), eq(2L), eq(TimeUnit.SECONDS));
        }
    }
}
