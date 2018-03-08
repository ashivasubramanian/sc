package calculations;

import java.util.Vector;

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
public class Station
{
	/**
	 * A constant <code>Integer</code> for the main track.
	 */
	final Integer MAIN_TRACK = new Integer(4);

	/**
	 * A constant <code>Integer</code> for the first loop track.
	 * Note that a station may or may not have loop tracks, but all stations shall
	 * contain a main track. Hence the presence of this track in a station is
	 * not guaranteed.
	 */
	final Integer LOOP1_TRACK = new Integer(5);

	/**
	 * A constant <code>Integer</code> for the second loop track.
	 * Note that a station may or may not have loop tracks, but all stations shall
	 * contain a main track. Hence the presence of this track in a station is
	 * not guaranteed.
	 */
	final Integer LOOP2_TRACK = new Integer(6);

	/**
	 * The name of the station. This is initialized by the constructor and is
	 * obtained using <code>getName()</code>.
	 */
	String name;

	/**
	 * A collection of tracks inside the station. This is initialized by the
	 * constructor.
	 */
	Vector<Integer> tracks;

	/**
	 * A collection of points inside the station. This is initialized by the
	 * constructor.
	 */
	Vector<Integer> points;

	/**
	 * An array of aspects inside the station. This is
	 * initialized to STOP by the constructor. <p>At any point during the game, any
	 * aspect of the station can be changed using the
	 * <code>setAspect(int, String)</code> method. All the aspects can be retrieved
	 * using <code>getAspects()</code>. Currently, there is no way to obtain only
	 * a particular aspect.
	 */
	SignalAspect[] aspects;

	/**
	 * The distance of the station from the home station.
	 */
	int distance_from_home;

	/**
	 * Uses the data present in <code>objStationData</code> to set the station
	 * name, its aspects, points and tracks as well as the distance of the
	 * station from the home station. Note that the constructor does not expect
	 * aspect data in <code>objStationData</code> and that it automatically
	 * sets the aspects to STOP.
	 *
	 * @param objStationData Contains station data such as name, no. of tracks,
	 *                       no. of points and its distance
	 */
	public Station(org.w3c.dom.Element objStationData)
	{
		name = objStationData.getAttribute("name");
		aspects = new SignalAspect[] {SignalAspect.STOP, SignalAspect.STOP};

		points = new Vector<>();
		points.add(MAIN_TRACK);
		points.add(MAIN_TRACK);

		tracks = new Vector<>();
		Integer track_array[] = {MAIN_TRACK,LOOP1_TRACK,LOOP2_TRACK};
		for(int i=0; i < Integer.parseInt(objStationData.getAttribute("nooftracks"));i++)
		{
			tracks.add(track_array[i]);
		}

		distance_from_home = Integer.parseInt(objStationData.getAttribute("distancefromhome"));
	}

	/**
	 * Returns the distance of the station from the home station.
	 *
	 * @return the distance of the station
	 */
	public Integer getDistance()
	{
		return new Integer(distance_from_home);
	}

	/**
	 * Returns the name of the station.
	 *
	 * @return the name of the station
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the current aspects of both signals at the station.
	 * The method returns the aspects of both signals as an array.
	 *
	 * @return An <code>Integer</code> array that contains both aspects.
	 */
	public SignalAspect[] getAspects() {
	    return aspects;
	}

	/**
	 * Sets the specified aspect to the specified signal.
	 *
	 * @param signal The signal to which <code>aspect</code> is to be set
	 * @param aspect The aspect to be set to <code>signal</code>
	 */
	public void setAspect(int signal, SignalAspect aspect) {
	    aspects[signal] = aspect;
	}
}