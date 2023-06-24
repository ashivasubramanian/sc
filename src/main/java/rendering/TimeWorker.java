package rendering;

import java.awt.Color;
import java.awt.Dimension;
import static java.lang.Thread.sleep;

import java.time.LocalDateTime;
import java.util.List;
import javax.swing.SwingWorker;
import presentation.windows.GameInfoPanel;

/**
 * The <code>TimeWorker</code> class fetches the current time,
 * and updates the <code>GameScreen</code> instance.
 */
public class TimeWorker extends SwingWorker<String, String> {

    private GameInfoPanel gameInfoPanel;
    private Dimension screenSize;

    public TimeWorker(GameInfoPanel gameInfoPanel, Dimension screenSize) {
        this.gameInfoPanel = gameInfoPanel;
        this.screenSize = screenSize;
    }

    @Override
    protected String doInBackground() throws Exception {
        while(true) {
            LocalDateTime currentTime = LocalDateTime.now();
            publish(String.format("%1$TH:%1$TM:%1$TS", currentTime));
            sleep(2000);
        }
    }

    @Override
    protected void process(List<String> multipleTimes) {
        String latestTime = multipleTimes.get(multipleTimes.size() - 1);
        this.gameInfoPanel.setTime(latestTime);
        this.gameInfoPanel.repaint((this.screenSize.width * 90 / 100), (this.screenSize.height * 5 / 100) - 20, 50, 20);
    }
    
}
