package game_engine;

import common.models.TrainDirection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * The <code>Train</code> class represents a train running on the section. Some of the
 * properties that an instance of <code>Train</code> holds are the train number, the train
 * name, the distance of the train from the home station, etc.
 *
 * @see game_engine.runners.TrainRunner
 */
public class Train implements PropertyChangeListener {
	/**
	 * The unique number of the train.
	 * This is of <code>String</code> datatype, rather than <code>int</code>,
	 * because some train numbers can contain alphabets or leading zeros, for example, 4021A or 03064.
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
	 * The train's timetable.
	 */
	private Timetable timetable;

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
	 *
	 * @param trainNumber          The number of the train.
	 * @param name                 The name of the train.
	 * @param direction            The direction in which the train is travelling.
	 * @param timetable            The train's timetable.
	 * @param initialTrainPosition The position of the train on game load.
	 */
	public Train(String trainNumber, String name, TrainDirection direction, Timetable timetable, TrainPosition initialTrainPosition) {
		this.no = trainNumber;
		this.name = name;
		this.direction = direction;
		this.timetable = timetable;
		this.trainPosition = initialTrainPosition;
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

	/**
	 * Returns the timetable for the train.
	 * <br>
	 * This method is package-scoped as it's meant to be used only for tests.
	 *
	 * @return the timetable.
	 */
	Timetable getTimetable() {
		return this.timetable;
	}

	/**
	 * Returns the current train position.
	 * @return the current position
	 */
	public TrainPosition getTrainPosition() {
		return this.trainPosition;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("received event...");
	}
}