package game_engine;

import game_engine.data_access.DataAccess;
import game_engine.dto.StationDto;
import game_engine.dto.TrainDto;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

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
}
