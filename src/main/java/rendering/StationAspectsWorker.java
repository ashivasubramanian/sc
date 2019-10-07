package rendering;

import common.models.SignalAspect;
import game_engine.Game;
import game_engine.dto.StationDto;
import java.awt.Dimension;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.SwingWorker;
import presentation.windows.GameInfoPanel;

/**
 * The <code>StationAspectsWorker</code> class fetches the latest aspects at each
 * station, and updates the <code>GameScreen</code> instance.
 */
public class StationAspectsWorker extends SwingWorker<List<SignalAspect[]>, List<SignalAspect[]>> {

    private GameInfoPanel gameInfoPanel;

    private Game game;
    
    private Dimension screenSize;

    public StationAspectsWorker(GameInfoPanel gameInfoPanel, Game game, Dimension screenSize) {
        this.gameInfoPanel = gameInfoPanel;
        this.game = game;
        this.screenSize = screenSize;
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
        int twentyFifthPercentOfHeight = this.screenSize.height * 25 / 100;
        List<SignalAspect[]> latestSignalAspectsForAllStations = multipleListsOfAspects.get(
            multipleListsOfAspects.size() - 1);
        gameInfoPanel.setAspects(new Vector(latestSignalAspectsForAllStations));
        gameInfoPanel.repaint(0, twentyFifthPercentOfHeight - 7,
                this.screenSize.width, 11);
    }
    
}
