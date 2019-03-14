package rendering;

import game_engine.Game;
import game_engine.dto.TrainDto;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.SwingWorker;
import presentation.windows.GameInfoPanel;

/**
 * The <code>TrainPositionsWorker</code> class fetches the latest position of each
 * train, and updates the <code>GameScreen</code> instance.
 */
public class TrainPositionsWorker extends SwingWorker<List<Float>, List<Float>> {

    private GameInfoPanel gameInfoPanel;

    private Game game;

    public TrainPositionsWorker(GameInfoPanel gameInfoPanel, Game game) {
        this.gameInfoPanel = gameInfoPanel;
        this.game = game;
    }

    @Override
    protected List<Float> doInBackground() throws Exception {
        while (true) {
            List<Float> objTrainPositions = new ArrayList<>();
            for(TrainDto individualTrain : game.getTrains()) {
                float distance = individualTrain.getDistanceFromHome();
                /*The following if condition is bcoz: Consider the train is moving towards
				home. The distance calculated shall be the distance from starting station,
				which shall be the 'away station' in our case. But while drawing, the
				train shall be drawn only from home. So if the train is 16km from away,
				then while drawing, it will be 16km from home. This is not what we want.
				So we reverse it.*/
                if (individualTrain.getDirection() == 1) { // towards Home; see Train.java for details
                    distance = 86 - distance;
                }
                objTrainPositions.add(distance);
            }
            publish(Collections.unmodifiableList(objTrainPositions));
            sleep(2000);
        }
    }

    @Override
    protected void process(List<List<Float>> multipleListsOfTrainPositions) {
        List<Float> latestTrainPositionList = multipleListsOfTrainPositions.get(
                multipleListsOfTrainPositions.size() - 1);
        gameInfoPanel.setTrainPositions(new Vector(latestTrainPositionList));
        gameInfoPanel.repaint(0, 200, 800, 210);
    }
}
