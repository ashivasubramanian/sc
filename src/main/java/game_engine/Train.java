package game_engine;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import game_engine.train.initializers.TrainInitializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
		new TrainInitializer().initialize(this);
		determineTrainInitialPosition();
		start();
	}

	/**
	 * Determines the position of the train on game load. This has 3 scenarios:
	 * # Is the train beyond the section (either not yet entered or has exit already)?78.181854
	 * # Is the train stopped at a station on the section?
	 * # Is the train running between stations?
	 */
	private void determineTrainInitialPosition() {
		// Has the train not yet entered the section?
		LocalDateTime currentTime = LocalDateTime.now(this.systemClock);
		TrainSchedule firstScheduledStop = scheduledStops.get(0);
		if (currentTime.isBefore(firstScheduledStop.getArrivalTime())) {
			trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN,
					60 * (currentTime.until(firstScheduledStop.getArrivalTime(), ChronoUnit.MINUTES) / 60f));
			return;
		}
		// Has the train exited the section?
		TrainSchedule lastScheduledStop = scheduledStops.get(scheduledStops.size() - 1);
		if (currentTime.isAfter(lastScheduledStop.getDepartureTime())) {
			trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN,
					60 * (lastScheduledStop.getDepartureTime().until(currentTime, ChronoUnit.MINUTES) / 60f));
			return;
		}
		ListIterator<TrainSchedule> scheduledStopsIterator = scheduledStops.listIterator();
		while (scheduledStopsIterator.hasNext()) {
			//Is the train stopped at a station on the section?
			TrainSchedule schedule = scheduledStopsIterator.next();
			if (schedule.getArrivalTime().equals(currentTime) || schedule.getDepartureTime().equals(currentTime)
					|| (schedule.getArrivalTime().isBefore(currentTime) && schedule.getDepartureTime().isAfter(currentTime))) {
				trainPosition = new TrainPosition(TrainRunningStatus.SCHEDULED_STOP, stationDistanceMap.get(schedule.getStationCode()));
				break;
			}
			// Is the train running between stations?
			if (scheduledStopsIterator.hasNext()) {
				TrainSchedule nextStop = scheduledStopsIterator.next();
				if (currentTime.isAfter(schedule.getDepartureTime()) && currentTime.isBefore(nextStop.getArrivalTime())) {
					trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN,
							determineInitialDistanceFromHome(schedule, nextStop));
					break;
				}
				scheduledStopsIterator.previous();
			}
		}
	}

	private float determineInitialDistanceFromHome(TrainSchedule crossedStation, TrainSchedule upcomingStation) {
		int distanceBetweenStations = Math.abs(stationDistanceMap.get(crossedStation.getStationCode())
                        - stationDistanceMap.get(upcomingStation.getStationCode()));
		float timeAsPerScheduleInHours = crossedStation.getDepartureTime()
                .until(upcomingStation.getArrivalTime(), ChronoUnit.MINUTES) / 60f;
		float expectedSpeedOfTrain = distanceBetweenStations / timeAsPerScheduleInHours;

		float timeSinceLastCrossedStation = crossedStation.getDepartureTime()
				.until(LocalDateTime.now(this.systemClock), ChronoUnit.MINUTES) / 60f;
		if (direction == TrainDirection.AWAY_FROM_HOME)
			return expectedSpeedOfTrain * timeSinceLastCrossedStation;
		else
			return 86 - (expectedSpeedOfTrain * timeSinceLastCrossedStation);
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