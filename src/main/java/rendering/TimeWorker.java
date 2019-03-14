package rendering;

import static java.lang.Thread.sleep;
import java.util.Calendar;
import java.util.List;
import javax.swing.SwingWorker;
import presentation.windows.GameInfoPanel;

/**
 * The <code>TimeWorker</code> class fetches the current time,
 * and updates the <code>GameScreen</code> instance.
 */
public class TimeWorker extends SwingWorker<String, String> {

    private GameInfoPanel gameInfoPanel;

    public TimeWorker(GameInfoPanel gameInfoPanel) {
        this.gameInfoPanel = gameInfoPanel;
    }

    @Override
    protected String doInBackground() throws Exception {
        while(true) {
            Calendar calendar = Calendar.getInstance();
            publish(String.format("%1$s:%2$s:%3$s", calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)));
            sleep(2000);
        }
    }

    @Override
    protected void process(List<String> multipleTimes) {
        String latestTime = multipleTimes.get(multipleTimes.size() - 1);
        this.gameInfoPanel.setTime(latestTime);
        this.gameInfoPanel.repaint(700, 20, 50, 20);
    }
    
}
