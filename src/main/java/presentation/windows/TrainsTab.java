package presentation.windows;

import game_engine.Game;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * The <code>TrainsTab</code> class contains the content that is drawn under the Trains
 * tab of the Control Panel.
 */
public class TrainsTab extends JPanel implements Runnable
{

    private Game game;

    public TrainsTab(Game game) {
        this.game = game;
        SwingUtilities.invokeLater(this);
    }
	/**
	 * Initializes all the controls that are available under the Trains tab of the
	 * Control Panel.
	 */
	public void run()
	{
		Font objNormalFont = new Font("Arial",Font.PLAIN,12);
		GridBagLayout objLayout = new GridBagLayout();
		GridBagConstraints objConstraints = new GridBagConstraints();

		JLabel objSelectTrain = new JLabel("<html>Select a train:</html>");
		objConstraints.gridx = 0;
		objConstraints.gridy = 0;
		objConstraints.weightx = 1.0;
		objConstraints.weighty = 1.0;
		objLayout.setConstraints(objSelectTrain,objConstraints);

                String[] trainNames = this.game.getTrains().stream()
                        .map(train -> train.getName())
                        .collect(Collectors.toList())
                        .toArray(new String[0]);
		JComboBox<String> objTrains = new JComboBox<>(trainNames);
		objConstraints.gridx = 1;
		objConstraints.gridy = 0;
		objLayout.setConstraints(objTrains,objConstraints);

		JLabel objTrainStatus = new JLabel("Status:");
		objConstraints.gridx = 0;
		objConstraints.gridy = 1;
		objLayout.setConstraints(objTrainStatus,objConstraints);

		JLabel objTrainBehaviour = new JLabel("Crossing");
		objTrainBehaviour.setFont(objNormalFont);
		objConstraints.gridx = 1;
		objConstraints.gridy = 1;
		objLayout.setConstraints(objTrainBehaviour,objConstraints);

		JLabel objTrainAtStation = new JLabel("<html>Between Parpanangadi and Tirur</html>");
		objTrainAtStation.setFont(objNormalFont);
		objConstraints.gridx = 1;
		objConstraints.gridy = 2;
		objLayout.setConstraints(objTrainAtStation,objConstraints);

		JLabel objHaltType = new JLabel("Unscheduled");
		objHaltType.setFont(objNormalFont);
		objConstraints.gridx = 1;
		objConstraints.gridy = 3;
		objLayout.setConstraints(objHaltType,objConstraints);

		JLabel objNextScheduledStop = new JLabel("<html>Next scheduled stop:</html>");
		objNextScheduledStop.setVerticalAlignment(javax.swing.SwingConstants.TOP);
		objConstraints.gridx = 0;
		objConstraints.gridy = 4;
		objLayout.setConstraints(objNextScheduledStop,objConstraints);

		JLabel objNextScheduledStopValue = new JLabel("Perashannur Halt");
		objNextScheduledStopValue.setFont(objNormalFont);
		objConstraints.gridx = 1;
		objConstraints.gridy = 4;
		objLayout.setConstraints(objNextScheduledStopValue,objConstraints);

		JLabel objLag = new JLabel("Lag time:");
		objConstraints.gridx = 0;
		objConstraints.gridy = 5;
		objLayout.setConstraints(objLag,objConstraints);

		JLabel objLagValue = new JLabel("00:35");
		objLagValue.setFont(objNormalFont);
		objConstraints.gridx = 1;
		objConstraints.gridy = 5;
		objLayout.setConstraints(objLagValue,objConstraints);

		setLayout(objLayout);
		add(objSelectTrain);
		add(objTrains);
		add(objTrainStatus);
		add(objTrainBehaviour);
		add(objTrainAtStation);
		add(objHaltType);
		add(objNextScheduledStop);
		add(objNextScheduledStopValue);
		add(objLag);
		add(objLagValue);
	}
}