package rendering;

import common.models.SignalAspect;
import game_engine.Game;
import game_engine.dto.StationDto;
import java.awt.Dimension;
import static java.lang.Thread.sleep;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.SwingWorker;
import presentation.windows.GameInfoPanel;
import presentation.windows.StationsTab;

/**
 * The <code>StationAspectsWorker</code> class fetches the latest aspects at each
 * station, and updates the <code>GameScreen</code> instance.
 */
public class StationAspectsWorker extends SwingWorker<List<StationDto>, List<StationDto>> {

    private GameInfoPanel gameInfoPanel;

    private Game game;

    private Dimension screenSize;

    /**
     * A reference to the Stations tab on the UI which this worker can use for pushing updates related to that tab.
     */
    private final StationsTab stationsTab;

    public StationAspectsWorker(GameInfoPanel gameInfoPanel, StationsTab stationsTab, Game game, Dimension screenSize) {
        this.gameInfoPanel = gameInfoPanel;
        this.stationsTab = stationsTab;
        this.game = game;
        this.screenSize = screenSize;
    }

    @Override
    protected List<StationDto> doInBackground() throws Exception {
        while (true) {
            publish(this.game.getStations());
            sleep(2000);
        }
    }

    @Override
    protected void process(List<List<StationDto>> multipleListsOfStations) {
        List<StationDto> latestStationsList = multipleListsOfStations.get(
            multipleListsOfStations.size() - 1);
        stationsTab.setLatestStationInformation(latestStationsList);

        int twentyFifthPercentOfHeight = this.screenSize.height * 25 / 100;
        List<SignalAspect[]> latestAspects = latestStationsList.stream()
                .map(StationDto::getAspects)
                .collect(Collectors.toList());
        gameInfoPanel.setAspects(new Vector(latestAspects));
        gameInfoPanel.repaint(0, twentyFifthPercentOfHeight - 7,
                this.screenSize.width, 11);
    }
    
}
