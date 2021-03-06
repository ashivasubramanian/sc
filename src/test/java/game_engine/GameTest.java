package game_engine;

import common.models.SignalAspect;
import game_engine.dto.StationDto;
import game_engine.dto.TrainDto;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameTest {
    
    @Test
    public void shouldLoadStations() throws Exception {
        Game game = new Game();
        assertEquals(2, game.getStations().size());
    }

    @Test
    public void shouldLoadTrains() throws Exception {
        Game game = new Game();
        assertEquals(2, game.getTrains().size());
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldExposeStationDataAsImmutableCollection() throws Exception {
        Game game = new Game();
        game.getStations().add(new StationDto("new_station", 18, null));
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldExposeTrainDataAsImmutableCollection() throws Exception {
        Game game = new Game();
        game.getTrains().add(new TrainDto("New Train", 100f, 1));
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
        game.setStationAspect("Calicut", newSignalAspects);
        assertArrayEquals(newSignalAspects, calicutStationDto.getAspects());
    }
}
