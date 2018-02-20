package presentation.panes;

import java.util.Vector;
import java.awt.Point;

/**
 * The <code>GamePane</code> class contains information that is frequently needed in the
 * Game Panel. <p>Certain data may need to be constantly obtained from the Rendering and
 * Calculation layers. Such data are not present here. Only data that does not need to
 * be frequently updated by the Rendering and Presentation layers is present here.
 * This is done to prevent frequent multiple-layer calls for data that can instead be
 * retained here.
 * <p>The data that will not frequently change are:
 * <ul>
 *  <li>Station names</li>
 *  <li>Station positions</li>
 *  <li>Train names</li>
 * </ul>
 * <p>Actually, the list of train names <i>may</i> change during program execution. For
 * example, a train may enter the section. At that point, the list of train names should
 * be updated. But this event is considered rare, since at any point of time, it is
 * expected that only a few trains will enter.<p>
 * This class is a singleton class, and as such, instances of this class may be obtained
 * only using the <code>getInstance()</code> call.
 */
public final class GamePane
{
	/**
	 * Stores the names of the stations on the section. Can be retrieved using
	 * <code>getStationNames()</code> method call. These names are the ones that are
	 * drawn on the screen along the track.
	 */
	Vector<String> objStationNames;

	/**
	 * Stores the positions of the stations as <code>Point</code> objects. Can be
	 * retrieved using <code>getStationPositions()</code> method call.
	 */
	Vector<Point> objStationPositions;

	/**
	 * Contains a list of train names that are currently running on the section.
	 * Actually, this list is of trains that are to run on this day. So, it may not
	 * reflect trains that are currently running.
	 * <p>NOTE: This is to be checked. I think that the code has since been updated
	 * to reflect trains that run only in the next one hour.
	 */
	Vector objTrains;
	
	/**
	 * Stores the horizontal resolution of the screen.
	 */
	int screenWidth;
	
	/**
	 * Stores the vertical resolution of the screen.
	 */
	int screenHeight;

	/**
	 * Contains an instance of this class. This is the instance that is returned
	 * when an instance of the class is requested.
	 */
	static GamePane objGamePane = new GamePane();

	/**
	 * Returns an instance of this class.
	 * Because the <code>GamePane</code> class is a singleton, this is the only way
	 * to obtain instances of this class.
	 *
	 * @return an instance of <code>GamePane</code> class.
	 */
	public static GamePane getInstance()
	{
		return objGamePane;
	}

	/**
	 * Constructor made private to disable default constructor provided by Java.
	 * Initializes <code>objStationPositions</code> and <code>objStationNames</code>.
	 */
	private GamePane()
	{
		objStationPositions = new Vector<>();
		objStationNames = new Vector<>();
	}

	/**
	 * Sets the station positions.
	 * The method obtains a <code>Vector</code> of real life station distances
	 * and calculates the on-screen distance for each station.
	 *
	 * @param positions a <code>Vector</code> of station distances
	 */
	public void setStationPositions(Vector<Integer> positions)
	{
		for(int i = 0; i < positions.size(); i++)
		{
			int distance = positions.elementAt(i);
			objStationPositions.add( new Point( (distance*790)/86,200));
		}
	}

	/**
	 * Returns the on-screen positions of all the stations.
	 * The positions are as <code>Point</code> objects.
	 *
	 * @return a <code>Vector</code> of positions
	 */
	public Vector<Point> getStationPositions()
	{
		return objStationPositions;
	}

	/**
	 * Sets the names of the stations on the section.
	 *
	 * @param names a <code>Vector</code> of the station names.
	 */
	public void setStationNames(Vector<String> names)
	{
		objStationNames = names;
	}

	/**
	 * Returns the names of the stations on the section.
	 *
	 * @return a <code>Vector</code> of the station names.
	 */
	public Vector<String> getStationNames()
	{
		return objStationNames;
	}

	/**
	 * Sets the names of the trains that are currently running on the section.
	 *
	 * @param trains a <code>Vector</code> of trains that are currently running
	 */
	public void setTrainNames(Vector trains)
	{
		objTrains = trains;
	}

	/**
	 * Obtains the names of the trains that are currently running on the section.
	 *
	 * @return a <code>Vector</code> of trains that are currently running
	 */
	public Vector getTrainNames()
	{
		return objTrains;
	}

	/**
	 * Returns the vertical resolution of the screen.
	 * 
	 * @return the vertical resolution
	 */
	public int getScreenHeight() {
		return screenHeight;
	}

	/**
	 * Sets the vertical resolution of the screen.
	 * 
	 * @param screenHeight the vertical resolution
	 */
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	/**
	 * Returns the horizontal resolution of the screen.
	 * 
	 * @return the horizontal resolution
	 */
	public int getScreenWidth() {
		return screenWidth;
	}

	/**
	 * Sets the horizontal resolution of the screen.
	 * 
	 * @param screenWidth the horizontal resolution
	 */
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}
}