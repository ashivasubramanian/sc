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
    TrainRunningStatus trainRunningStatus;

}
