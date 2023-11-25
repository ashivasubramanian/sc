package game_engine;

import common.models.SignalAspect;

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
public class Station {

    /**
     * The name of the station. This is initialized by the constructor and is
     * obtained using <code>getName()</code>.
     */
    String name;

    /**
     * A collection of tracks inside the station. This is initialized by the
     * constructor.
     */
    List<Track> tracks;

    /**
     * A collection of points inside the station. This is initialized by the
     * constructor.
     */
    List<Track.TrackType> points;

    /**
     * The distance of the station from the home station.
     */
    int distance_from_home;

    /**
     * Uses the data present in <code>objStationData</code> to set the station
     * name, its aspects, points and tracks as well as the distance of the
     * station from the home station. Note that the constructor does not expect
     * aspect data in <code>objStationData</code> and that it automatically sets
     * the aspects to STOP.
     *
     * @param objStationData Contains station data such as name, no. of tracks,
     * no. of points and its distance
     */
    public Station(org.w3c.dom.Element objStationData) {
        name = objStationData.getAttribute("name");

        points = new ArrayList<>();
        points.add(Track.TrackType.MAIN_TRACK);
        points.add(Track.TrackType.MAIN_TRACK);

        tracks = new ArrayList<>();
        Track.TrackType track_array[] = {Track.TrackType.MAIN_TRACK, Track.TrackType.LOOP_TRACK, Track.TrackType.LOOP_TRACK};
        for (int i = 0; i < Integer.parseInt(objStationData.getAttribute("no-of-tracks")); i++) {
            tracks.add(new Track(track_array[i]));
        }

        distance_from_home = Integer.parseInt(objStationData.getAttribute("distance-from-home"));
    }

    /**
     * Returns the distance of the station from the home station.
     *
     * @return the distance of the station
     */
    public Integer getDistance() {
        return new Integer(distance_from_home);
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
        return tracks.get(0).getAspects();
    }

    /**
     * Sets the specified aspect to the specified signal.
     *
     * @param signal The signal to which <code>aspect</code> is to be set
     * @param aspect The aspect to be set to <code>signal</code>
     */
    public void setAspect(int signal, SignalAspect aspect) {
        tracks.forEach(track -> track.getAspects()[signal] = aspect);
    }

    public List<Track> getTracks() {
        return this.tracks;
    }
}
