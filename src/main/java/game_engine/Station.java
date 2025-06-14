package game_engine;

import common.models.SignalAspect;
import common.models.TrainDirection;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Station</code> class represents a station on the section. Any station
 * contains tracks, points and signals, which lie under the station's control.
 * The <code>Station</code> class reflects this real world behaviour, by containing
 * separate collections for tracks, points and signals. It also contains setter and
 * getter methods to these collections.<p>
 * A <code>Station</code> instance must contain two aspects, one for each direction
 * beyond the station. Also, it is necessary for that instance to have no more than
 * three tracks. Note that this does not mean that every station must contain three
 * tracks; it only implies that no more than three tracks are to be present in a
 * station.<p>
 * Why is this condition present? That is because when the game was first designed,
 * it was designed only for the Calicut - Shoranur section, which fits the above
 * conditions.
 */
public class Station implements Comparable<Station> {

    /**
     * The station code.
     */
    private String code;

    /**
     * The name of the station. This is initialized by the constructor and is
     * obtained using <code>getName()</code>.
     */
    private String name;

    /**
     * A collection of tracks inside the station. This is initialized by the
     * constructor.
     */
    private List<Track> tracks;

    /**
     * A collection of points inside the station. This is initialized by the
     * constructor.
     */
    private List<Track.TrackType> points;

    /**
     * The distance of the station from the home station.
     */
    private int distanceFromHome;

    /**
     * Java Beans class to help monitor observers for the station's signals.
     */
    private PropertyChangeSupport pcs;

    /**
     * Constructs a <code>Station</code> object.
     * <br><br>Note that the constructor sets all the aspects of the station to STOP as default.
     *
     * @param code             the station code
     * @param name             the name of the station
     * @param numberOfTracks   the number of tracks in the station
     * @param distanceFromHome the distance of the station from the home station
     */
    public Station(String code, String name, int numberOfTracks, int distanceFromHome) {
        this.code = code;
        this.name = name;

        points = new ArrayList<>();
        points.add(Track.TrackType.MAIN_TRACK);
        points.add(Track.TrackType.MAIN_TRACK);

        tracks = new ArrayList<>();
        Track.TrackType[] track_array = {Track.TrackType.MAIN_TRACK, Track.TrackType.LOOP_TRACK, Track.TrackType.LOOP_TRACK};
        for (int i = 0; i < numberOfTracks; i++) {
            tracks.add(new Track(track_array[i]));
        }

        pcs = new PropertyChangeSupport(this);
        this.distanceFromHome = distanceFromHome;
    }

    /**
     * Returns the distance of the station from the home station.
     *
     * @return the distance of the station
     */
    public Integer getDistance() {
        return new Integer(distanceFromHome);
    }

    /**
     * Returns the name of the station.
     *
     * @return the name of the station
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current aspects of both signals at the station. The method
     * returns the aspects of both signals as an array.
     *
     * @return An <code>Integer</code> array that contains both aspects.
     */
    public SignalAspect[] getAspects() {
        return new SignalAspect[] {tracks.get(0).getTowardsHomeAspect(), tracks.get(0).getAwayFromHomeAspect()};
    }

    /**
     * Sets the specified aspect to the specified signal.
     * Once the signal has been changed, any <code>Train</code>s currently observing the signal are notified.
     *
     * @param signal The signal to which <code>aspect</code> is to be set
     * @param aspect The aspect to be set to <code>signal</code>
     */
    public void setAspect(TrainDirection signal, SignalAspect aspect) {
        if (signal == TrainDirection.TOWARDS_HOME) {
            SignalAspect oldAspect = tracks.get(0).getTowardsHomeAspect();
            tracks.forEach(track -> track.setTowardsHomeAspect(aspect));
            pcs.firePropertyChange("towardsHomeAspect", oldAspect, aspect);
        }
        else {
            SignalAspect oldAspect = tracks.get(0).getAwayFromHomeAspect();
            tracks.forEach(track -> track.setAwayFromHomeAspect(aspect));
            pcs.firePropertyChange("awayFromHomeAspect", oldAspect, aspect);
        }
    }

    List<Track> getTracks() {
        return this.tracks;
    }

    List<Track.TrackType> getPoints() {
        return this.points;
    }

    /**
     * Returns the station code.
     * @return the station code
     */
    public String getCode() {
        return code;
    }

    /**
     * Adds a Train as an observer for the station's signal.
     * Since a station has 2 signals on either side, <code>direction</code> is used to determine the correct
     * signal to observe.
     *
     * @param train     the train that wants to observe the signal
     * @param direction the signal the train wants to observe
     */
    public void addObserverForSignal(Train train, TrainDirection direction) {
        if (direction == TrainDirection.TOWARDS_HOME) {
            pcs.addPropertyChangeListener("towardsHomeAspect", train);
        } else if (direction == TrainDirection.AWAY_FROM_HOME) {
            pcs.addPropertyChangeListener("awayFromHomeAspect", train);
        }
    }

    /**
     * Removes a Train as an observer for the station's signal.
     * Since a station has 2 signals on either side, <code>direction</code> is used to determine the correct
     * signal to observe.
     *
     * @param train     the train that wants to stop observing signals
     * @param direction the signal the train wants to stop observing
     */
    public void removeObserverForSignal(Train train, TrainDirection direction) {
        if (direction == TrainDirection.TOWARDS_HOME) {
            pcs.removePropertyChangeListener("towardsHomeAspect", train);
        } else if (direction == TrainDirection.AWAY_FROM_HOME) {
            pcs.removePropertyChangeListener("awayFromHomeAspect", train);
        }
    }

    @Override
    public int compareTo(Station other) {
        return this.getDistance().compareTo(other.getDistance());
    }
}
