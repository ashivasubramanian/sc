package rendering;

import common.models.SignalAspect;
import game_engine.Game;
import game_engine.dto.StationDto;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.SwingWorker;
import presentation.windows.GameScreen;

/**
 * The <code>StationAspectsWorker</code> class fetches the latest aspects at each
 * station, and updates the <code>GameScreen</code> instance.
 */
public class StationAspectsWorker extends SwingWorker<List<SignalAspect[]>, List<SignalAspect[]>> {

    private GameScreen gameScreen;

    private Game game;

    public StationAspectsWorker(GameScreen gameScreen, Game game) {
        this.gameScreen = gameScreen;
        this.game = game;
    }

    @Override
    protected List<SignalAspect[]> doInBackground() throws Exception {
        while (true) {
            List<SignalAspect[]> aspects = new ArrayList<>();
            for (StationDto individualStation : this.game.getStations()) {
                aspects.add(individualStation.getAspects());
            }
            publish(aspects);
            sleep(2000);
        }
    }

    @Override
    protected void process(List<List<SignalAspect[]>> multipleListsOfAspects) {
        List<SignalAspect[]> latestSignalAspectsForAllStations = multipleListsOfAspects.get(
            multipleListsOfAspects.size() - 1);
        gameScreen.setAspects(new Vector(latestSignalAspectsForAllStations));
        gameScreen.repaint(0, 200, 800, 210);
    }
    
}
