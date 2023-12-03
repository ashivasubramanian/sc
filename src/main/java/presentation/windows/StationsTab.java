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
import game_engine.dto.StationDto;

import java.util.List;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;

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

	/**
	 * A list of <code>StationDto</code> objects which contain the latest information on stations, and which is pulled frequently from <code>Game</code>.
	 */
	private List<StationDto> latestStationInformation;

	public StationsTab(Game game) {
		this.game = game;
		SwingUtilities.invokeLater(this);
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

            String[] stationNames = this.game.getStations().stream()
                    .map(station -> station.getName())
                    .collect(Collectors.toList())
                    .toArray(new String[0]);
	    objStations = new JComboBox<>(stationNames);
		objStations.addActionListener(this);
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
	 * Handles click events on the Stations tab
	 * 
	 * @param objActionEvent
	 *            The <code>ActionEvent</code> that represents the click.
	 */
	public void actionPerformed(ActionEvent objActionEvent) {
		if (objActionEvent.getSource() instanceof JButton) {
			this.game.setStationAspect(objStations.getSelectedItem().toString(),
					new SignalAspect[]{(SignalAspect) objAspectTowardsCalicutValue.getSelectedItem(),
							(SignalAspect) objAspectTowardsShoranurValue.getSelectedItem()});
		} else if (objActionEvent.getSource() instanceof JComboBox) {
			String selectedStationName = ((JComboBox<String>) objActionEvent.getSource()).getSelectedItem().toString();
			StationDto selectedStation = latestStationInformation.stream()
					.filter(stationDto -> stationDto.getName().equals(selectedStationName)).findFirst().get();
			objAspectTowardsCalicutValue.setSelectedItem(selectedStation.getAspects()[0]);
			objAspectTowardsShoranurValue.setSelectedItem(selectedStation.getAspects()[1]);
		}
	}

	/**
	 * Sets the latest station information into the Stations tab
	 *
	 * @param latestStationInformation the up-to-date information on stations passed to us by SwingWorkers.
	 */
	public void setLatestStationInformation(List<StationDto> latestStationInformation) {
		this.latestStationInformation = latestStationInformation;
	}
}