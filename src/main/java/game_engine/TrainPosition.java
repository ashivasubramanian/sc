package game_engine;

import common.models.TrainRunningStatus;

public class TrainPosition {
    /**
     * The distance of the train from the home station.
     */
    float distanceFromHome;

    /**
     * Holds info about whether the train is running or not, and if not,
     * is it at a scheduled or unscheduled stop.
     */
    private TrainRunningStatus trainRunningStatus;

    /**
     * Initialize the <code>TrainPosition</code> using the given train status
     * @param trainRunningStatus the current train status
     */
    public TrainPosition(TrainRunningStatus trainRunningStatus) {
        this.trainRunningStatus = trainRunningStatus;
    }

    /**
     * Return the train's current run status.
     * @return the current run status
     */
    public TrainRunningStatus getTrainRunningStatus() {
        return this.trainRunningStatus;
    }

}
