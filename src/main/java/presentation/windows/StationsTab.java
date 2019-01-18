package presentation.windows;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import common.models.SignalAspect;
import game_engine.Game;
import presentation.panes.GamePane;

/**
 * The <code>StationsTab</code> class contains the content that is drawn under the Stations
 * tab of the Control Panel.
 */
public class StationsTab extends JPanel implements ActionListener, Runnable
{
	/**
	 * A <code>JComboBox</code> control that lists the stations on the section.
	 */
	JComboBox<String> objStations;

	/**
	 * A <code>JButton</code> control that can be used to set the aspects and the points
	 * of the station.
	 */
	JButton objSet;

	JComboBox<String> objPointFacingCalicutValue;

	JComboBox<String> objPointFacingShoranurValue;

	JComboBox<SignalAspect> objAspectTowardsShoranurValue;

	JComboBox<SignalAspect> objAspectTowardsCalicutValue;
    
        private Game game;
        
        public StationsTab(Game game) {
            this.game = game;
        }

	/**
	 * Initializes all the controls that are available under the Stations tab of the
	 * Control Panel.
	 */
	public void run() {
	    Font objNormalFont = new Font("Arial", Font.PLAIN, 12);
	    Font objBoldFont = new Font("Arial", Font.BOLD, 12);
	    GridBagLayout objLayout = new GridBagLayout();
	    GridBagConstraints objConstraints = new GridBagConstraints();

	    // Are JLabels bold by default?
	    JLabel objSelectStation = new JLabel("Select a station:");
	    objConstraints.gridx = 0;
	    objConstraints.gridy = 0;
	    objConstraints.weightx = 1.0;
	    objConstraints.weighty = 1.0;
	    objLayout.setConstraints(objSelectStation, objConstraints);

	    objStations = new JComboBox<>(GamePane.getInstance().getStationNames());
	    objConstraints.gridx = 1;
	    objConstraints.gridy = 0;
	    objLayout.setConstraints(objStations, objConstraints);

	    JLabel objTrack = new JLabel("Tracks:");
	    objConstraints.gridx = 0;
	    objConstraints.gridy = 1;
	    objLayout.setConstraints(objTrack, objConstraints);

	    JLabel objTracks = new JLabel("Main, Loop(pf)");
	    objTracks.setFont(objNormalFont);
	    objConstraints.gridx = 1;
	    objConstraints.gridy = 1;
	    objLayout.setConstraints(objTracks, objConstraints);

	    JLabel objPointFacingCalicut = new JLabel("Point facing Calicut:");
	    objConstraints.gridx = 0;
	    objConstraints.gridy = 2;
	    objLayout.setConstraints(objPointFacingCalicut, objConstraints);

	    objPointFacingCalicutValue = new JComboBox<>(new String[] { "Main", "Loop(pf)" });
	    objPointFacingCalicutValue.setFont(objNormalFont);
	    objConstraints.gridx = 1;
	    objConstraints.gridy = 2;
	    objLayout.setConstraints(objPointFacingCalicutValue, objConstraints);

	    JLabel objPointFacingShoranur = new JLabel("Point facing Shoranur:");
	    objConstraints.gridx = 0;
	    objConstraints.gridy = 3;
	    objLayout.setConstraints(objPointFacingShoranur, objConstraints);

	    objPointFacingShoranurValue = new JComboBox<>(new String[] { "Main", "Loop(pf)" });
	    objPointFacingShoranurValue.setFont(objNormalFont);
	    objConstraints.gridx = 1;
	    objConstraints.gridy = 3;
	    objLayout.setConstraints(objPointFacingShoranurValue, objConstraints);

	    // Aspects
	    JLabel objAspectTowardsCalicut = new JLabel("Aspect facing Calicut:");
	    objAspectTowardsCalicut.setFont(objBoldFont);
	    objConstraints.gridx = 0;
	    objConstraints.gridy = 4;
	    objLayout.setConstraints(objAspectTowardsCalicut, objConstraints);

	    objAspectTowardsCalicutValue = new JComboBox<>(SignalAspect.values());
	    objAspectTowardsCalicutValue.setFont(objNormalFont);
	    objConstraints.gridx = 1;
	    objConstraints.gridy = 4;
	    objLayout.setConstraints(objAspectTowardsCalicutValue, objConstraints);

	    JLabel objAspectTowardsShoranur = new JLabel("Aspect facing Shoranur:");
	    objAspectTowardsShoranur.setFont(objBoldFont);
	    objConstraints.gridx = 0;
	    objConstraints.gridy = 5;
	    objLayout.setConstraints(objAspectTowardsShoranur, objConstraints);

	    objAspectTowardsShoranurValue = new JComboBox<>(SignalAspect.values());
	    objAspectTowardsShoranurValue.setFont(objNormalFont);
	    objConstraints.gridx = 1;
	    objConstraints.gridy = 5;
	    objLayout.setConstraints(objAspectTowardsShoranurValue, objConstraints);

	    objSet = new JButton("Set");
	    objSet.addActionListener(this);
	    objConstraints.gridx = 0;
	    objConstraints.gridy = 6;
	    objConstraints.anchor = GridBagConstraints.EAST;
	    objLayout.setConstraints(objSet, objConstraints);

	    add(objSelectStation);
	    add(objStations);
	    add(objTrack);
	    add(objTracks);
	    add(objPointFacingCalicut);
	    add(objPointFacingCalicutValue);
	    add(objPointFacingShoranur);
	    add(objPointFacingShoranurValue);
	    add(objAspectTowardsCalicut);
	    add(objAspectTowardsCalicutValue);
	    add(objAspectTowardsShoranur);
	    add(objAspectTowardsShoranurValue);
	    add(objSet);
	    setLayout(objLayout);
	}

	/**
	 * Handles the click event of the Set button.
	 * 
	 * @param objActionEvent
	 *            The <code>ActionEvent</code> that represents the button click.
	 */
	public void actionPerformed(ActionEvent objActionEvent) {
            this.game.setStationAspect(objStations.getSelectedItem().toString(),
		    new SignalAspect[] { (SignalAspect) objAspectTowardsCalicutValue.getSelectedItem(),
			    (SignalAspect) objAspectTowardsShoranurValue.getSelectedItem() });
	}
}