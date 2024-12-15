package game_engine;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import game_engine.data_access.DataAccess;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


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
	 * A mapping of stations on the section & their distances from the Home station.
	 */
	Map<String,Integer> stationDistanceMap;

	/**
	 * A collection of stations where the train will stop.
	 */
	private List<TrainSchedule> scheduledStops;

	/**
	 * The distance of the train from Calicut.
	 */
	float distance;

	/**
	 * The direction in which the train is travelling.
	 */
	TrainDirection direction;

	/**
	 * Used for mocking time operations for testing purposes.
	 * By default, it aligns to system time.
	 */
	private Clock systemClock;

	/**
	 * Stores the current position of the train.
	 */
	private TrainPosition trainPosition;

	/**
	 * Constructor that initializes the <code>Train</code>.
	 * The constructor then starts the thread.
	 *
	 * @param trainNo            The number of the train.
	 * @param trainName          The name of the train.
	 * @param direction          The direction in which the train is travelling.
	 * @param stationDistanceMap a mapping of station codes & their distances from home station
	 */
	public Train(String trainNo, String trainName, String direction, Map<String, Integer> stationDistanceMap)
			throws IOException, SAXException, ParserConfigurationException {
		this(Clock.systemDefaultZone(), trainNo, trainName, direction, stationDistanceMap);
	}

	/**
	 * Constructor used for testing purposes.
	 *
	 * @param mockClock          mock instance of <code>Clock</code> that can be used for testing purposes.
	 * @param trainNo            The number of the train.
	 * @param trainName          The name of the train.
	 * @param direction          The direction in which the train is travelling.
	 * @param stationDistanceMap a mapping of station codes & their distances from home station
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public Train(Clock mockClock, String trainNo, String trainName, String direction, Map<String, Integer> stationDistanceMap)
			throws IOException, ParserConfigurationException, SAXException {
		this.systemClock = mockClock;
		this.no = trainNo;
		this.name = trainName;
		this.scheduledStops = new ArrayList<>();
		this.stationDistanceMap = stationDistanceMap;
		distance = 0;
		lag = 0;
		if(direction.equals("TowardsHome"))
			this.direction = TrainDirection.TOWARDS_HOME;
		else if(direction.equals("AwayFromHome"))
			this.direction = TrainDirection.AWAY_FROM_HOME;
		populateTrainData();
		determineTrainInitialPosition();
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
				String stationCode = stop.getAttribute("code");

				String arrivalTimeString = stop.getAttribute("arrival-time");
				int[] arrivalTimeIntArray = Arrays.stream(arrivalTimeString.split(":"))
						.mapToInt(Integer::valueOf).toArray();
				LocalDateTime arrivalTime = LocalDateTime.of(LocalDate.now(),
								LocalTime.of(arrivalTimeIntArray[0], arrivalTimeIntArray[1]));

				String departureTimeString = stop.getAttribute("departure-time");
				int[] departureTimeIntArray = Arrays.stream(departureTimeString.split(":"))
						.mapToInt(Integer::valueOf).toArray();
				LocalDateTime departureTime = LocalDateTime.of(LocalDate.now(),
					LocalTime.of(departureTimeIntArray[0], departureTimeIntArray[1]));
				scheduledStops.add(new TrainSchedule(stationCode, arrivalTime, departureTime));

			}
		}
		//Reversing the distances if travelling towards home
		if (direction == TrainDirection.TOWARDS_HOME)
			java.util.Collections.reverse(scheduledStops);
	}

	/**
	 * Determines the position of the train on game load.
	 */
	private void determineTrainInitialPosition() {
		LocalDateTime currentTime = LocalDateTime.now(this.systemClock);
		for (int i = 0; i < scheduledStops.size(); i++) {
			TrainSchedule schedule = scheduledStops.get(i);
			if (schedule.getArrivalTime().equals(currentTime) || schedule.getDepartureTime().equals(currentTime)
					|| (schedule.getArrivalTime().isBefore(currentTime) && schedule.getDepartureTime().isAfter(currentTime))) {
				trainPosition = new TrainPosition(TrainRunningStatus.SCHEDULED_STOP, stationDistanceMap.get(schedule.getStationCode()));
				break;
			}
			if ((i + 1) < (scheduledStops.size() - 1)) {
				TrainSchedule nextStop = scheduledStops.get(i + 1);
				if (currentTime.isAfter(schedule.getDepartureTime()) && currentTime.isBefore(nextStop.getArrivalTime())) {
					trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN, 0);
					break;
				}
			}
		}
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
		LocalDateTime first_station_time = scheduledStops.get(0).getArrivalTime();
		int count = 0;
		while(true)
		{
			LocalDateTime currentTime = LocalDateTime.now();
			if(first_station_time.isBefore(currentTime)) {
				//The train is already in the section.
				float totalseconds = getTimeDifference(currentTime,first_station_time);
				distance = 60 * (totalseconds/3600);
				long sleepTime = 1000;
				if (stationDistanceMap.entrySet().contains(distance)) {
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

	public List<TrainSchedule> getScheduledStops() {
		return this.scheduledStops;
	}

	/**
	 * Returns the current train position.
	 * @return the current position
	 */
	public TrainPosition getTrainPosition() {
		return this.trainPosition;
	}
}