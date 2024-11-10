package game_engine;

import common.models.TrainDirection;
import game_engine.data_access.DataAccess;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;


/**
 * The <code>Train</code> class represents a train running on the section. Some of the
 * properties that an instance of <code>Train</code> holds are the train number, the train
 * name, the distance of the train from the home station, etc. <p>The class extends
 * <code>Thread</code>, thus enabling it to keep train-related data up-to-date
 * by making its own calculations. This means that though there <i>may</i> be delays in
 * rendering of data on-screen, the data held by an instance of <code>Train</code> is
 * always accurate.
 */
public class Train extends Thread
{
	/**
	 * The unique number of the train.
	 * This is of <code>String</code> datatype, rather than <code>int</code>,
	 * because some train numbers can contain alphabets, for example, 4021A.
	 */
	String no;

	/**
	 * The name of the train.
	 */
	String name;

	/**
	 * The current lag of the train.
	 * The default lag is 0. When a train is late, the lag time increases. When
	 * a train is early, its lag time moves into negative.
	 */
	int lag;

	/**
	 * A <code>String</code> collection of stations on the section where the
	 * train will halt.
	 */
	Vector<String> stations;
	
	/**
	 * A <code>HashMap</code> that stores the distances of stations that the
	 * train halts.
	 */
	HashMap<String,Integer> distances;

	/**
 	 * A <code>Hashtable</code> of arrival times of the train at its halts. The
 	 * keys of the <code>Hashtable</code> are the names of the stations where it
 	 * halts.
	 */
	HashMap<String,LocalDateTime> arrivalTimes;

	/**
 	 * A <code>Hashtable</code> of departure times of the train at its halts.
 	 * The keys of the <code>Hashtable</code> are the names of the stations
 	 * where it halts.
	 */
	HashMap<String,LocalDateTime> departureTimes;

	/**
	 * The distance of the train from Calicut.
	 */
	float distance;

	/**
	 * The direction in which the train is travelling.
	 */
	TrainDirection direction;

	/**
	 * Constructor that initializes the <code>Train</code>.
	 * The constructor then starts the thread.
	 *
	 * @param trainNo   The number of the train.
	 * @param trainName The name of the train.
	 * @param direction The direction in which the train is travelling.
	 */
	public Train(String trainNo, String trainName, String direction)
			throws IOException, SAXException, ParserConfigurationException
	{
		this.no = trainNo;
		this.name = trainName;
		stations = new Vector<>();
		arrivalTimes = new HashMap<>();
		departureTimes = new HashMap<>();
		distances = new HashMap<>();
		distance = 0;
		lag = 0;
		if(direction.equals("TowardsHome"))
			this.direction = TrainDirection.TOWARDS_HOME;
		else if(direction.equals("AwayFromHome"))
			this.direction = TrainDirection.AWAY_FROM_HOME;
		populateTrainData();
		start();
	}

	/**
	 * Populates the train with the list of stations where the train halts,
	 * their distances and the arrival and departure times at those stations.
	 * The order of the stations corresponds to the direction of the train.
	 * For example, if the train travels from Calicut to Shoranur, then the list
	 * of stations starts from Calicut and ends at Shoranur. If the train
	 * travels from Shoranur to Calicut, then the list of stations starts from
	 * Shoranur and ends at Calicut.
	 */
	private void populateTrainData()
			throws IOException, SAXException, ParserConfigurationException {
		System.out.println( "Loading data for " + no);
		InputStream trainXMLStream = getClass().getResourceAsStream("/data/" + no + ".xml");
		Vector<Element> stops = DataAccess.getInstance().extractData(trainXMLStream,"stop");
		if(stops.size() > 0) {
			for (Element stop : stops) {
				stations.add(stop.getAttribute("name"));

				String arrivalTimeString = stop.getAttribute("arrival-time");
				int[] arrivalTimeIntArray = Arrays.stream(arrivalTimeString.split(":"))
						.mapToInt(Integer::valueOf).toArray();
				arrivalTimes.put( stop.getAttribute("name"),
						LocalDateTime.of(LocalDate.now(),
								LocalTime.of(arrivalTimeIntArray[0], arrivalTimeIntArray[1])));

				String departureTimeString = stop.getAttribute("departure-time");
				int[] departureTimeIntArray = Arrays.stream(departureTimeString.split(":"))
						.mapToInt(Integer::valueOf).toArray();
				departureTimes.put( stop.getAttribute("name"),
					LocalDateTime.of(LocalDate.now(),
					LocalTime.of(departureTimeIntArray[0], departureTimeIntArray[1])));

			}
		}
		//Reversing the distances if travelling towards home
		if (direction == TrainDirection.TOWARDS_HOME)
			java.util.Collections.reverse(stations);
	}

	/**
	 * The run method of the <code>Thread</code>.
	 * The method constantly determines the distance of the train from Calicut station.
	 */
	public void run()
	{
		//Let's get the time first, then we can calculate where each train is supposed to
		//be at the time the game is started.
		/*Now let's get the arrival (or departure) time of the train in the station it
		encounters first while entering the section. If initialTime is greater, then the
		train has already entered the section. We then calculate the number of seconds
		between initialTime and the time at first station. Depending upon this, we
		calculate the distance the train would have crossed as 60(the MPS) * seconds. We
		then draw it on the screen.*/
		String station = stations.get(0);
		station = station.replace(' ','-');
		String time = "";
		LocalDateTime first_station_time = arrivalTimes.get(station);
		int count = 0;
		while(true)
		{
			LocalDateTime currentTime = LocalDateTime.now();
			if(first_station_time.isBefore(currentTime)) {
				//The train is already in the section.
				float totalseconds = getTimeDifference(currentTime,first_station_time);
				distance = 60 * (totalseconds/3600);
				long sleepTime = 1000;
				if (distances.entrySet().contains(distance)) {
					sleepTime = 2000;
				}
				try
				{
					System.out.println("sleeping..." + count++);
					sleep(sleepTime);
				}
				catch(InterruptedException objInterruptedException)
				{
					System.out.println("InterruptedException");
					objInterruptedException.printStackTrace();
				}
			}
		}
	}

	/**
	 * A utility method that returns the difference(in seconds) between
	 * any two times.<p> This method can be used to calculate the lag time
	 * of the train.<p>Of the two times, <code>time1</code> is the greater
	 * time, while <code>time2</code> is the lesser time.
	 *
	 * @param time1 The greater time
	 * @param time2 The lesser time
	 * @return The difference between the two times in seconds.
	 */
	private float getTimeDifference(LocalDateTime time1, LocalDateTime time2)
	{
		float hours = time1.getHour() - time2.getHour();
		float minutes = time1.getMinute() - time2.getMinute();
		float seconds = time1.getSecond() - time2.getSecond();
		float totalseconds = (hours * 3600) + (minutes * 60) + seconds;
		return totalseconds;
	}

	/**
	 * Returns the distance of the train from Calicut station.
	 *
	 * @return the distance of the train.
	 */
	public float getDistance()
	{
		return distance;
	}

	/**
	 * Returns the direction in which the train is travelling across the section.
	 *
	 * @return the direction of the train
	 */
	public TrainDirection getDirection()
	{
		return direction;
	}

    public String getNumber() {
        return this.no;
    }

    public String getTrainName() {
        return this.name;
    }
}