package game_engine;

import common.models.TrainRunningStatus;

public class TrainPosition {
    /**
     * The distance of the train from the home station.
     */
    private float distanceFromHome;

    /**
     * Holds info about whether the train is running or not, and if not,
     * is it at a scheduled or unscheduled stop.
     */
    private TrainRunningStatus trainRunningStatus;

    /**
     * Initialize the <code>TrainPosition</code> using the given train status
     *
     * @param trainRunningStatus the current train status
     * @param distance distance from Home station
     */
    public TrainPosition(TrainRunningStatus trainRunningStatus, float distance) {
        this.trainRunningStatus = trainRunningStatus;
        this.distanceFromHome = distance;
    }

    /**
     * Return the train's current run status.
     * @return the current run status
     */
    public TrainRunningStatus getTrainRunningStatus() {
        return this.trainRunningStatus;
    }

    /**
     * Return the train's current distance from the home station.
     * @return the distance from the home station
     */
    public float getDistanceFromHome() {
        return this.distanceFromHome;
    }

    /**
     * Sets the distance from home of the train.
     *
     * @param distanceFromHome the distance from home.
     */
    public void setDistanceFromHome(float distanceFromHome) { this.distanceFromHome = distanceFromHome; }

}
