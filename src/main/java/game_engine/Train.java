package game_engine;

import common.models.TrainDirection;

import java.time.LocalDateTime;
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
	private String no;

	/**
	 * The name of the train.
	 */
	private String name;

	/**
	 * The current lag of the train.
	 * The default lag is 0. When a train is late, the lag time increases. When
	 * a train is early, its lag time moves into negative.
	 */
	private int lag;

	/**
	 * A mapping of stations on the section & their distances from the Home station.
	 */
	private Map<String,Integer> stationDistanceMap;

	/**
	 * A collection of stations where the train will stop.
	 */
	private List<TrainSchedule> scheduledStops;

	/**
	 * The direction in which the train is travelling.
	 */
	private TrainDirection direction;

	/**
	 * Stores the current position of the train.
	 */
	private TrainPosition trainPosition;

	/**
	 * Constructor that initializes the <code>Train</code>.
	 * The constructor then starts the thread.
	 *
	 * @param trainNumber            The number of the train.
	 * @param name                   The name of the train.
	 * @param direction              The direction in which the train is travelling.
	 * @param initialTrainPosition   The position of the train on game load.
	 */
	public Train(String trainNumber, String name, TrainDirection direction, List<TrainSchedule> scheduledStops, TrainPosition initialTrainPosition) {
		this.no = trainNumber;
		this.name = name;
		this.direction = direction;
		this.scheduledStops = scheduledStops;
		this.trainPosition = initialTrainPosition;
		start();
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
				trainPosition.setDistanceFromHome(60 * (totalseconds/3600));
				long sleepTime = 2000;
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
		return this.trainPosition.getDistanceFromHome();
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