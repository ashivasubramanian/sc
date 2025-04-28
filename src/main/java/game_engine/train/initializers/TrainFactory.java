package game_engine.train.initializers;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import game_engine.Train;
import game_engine.TrainPosition;
import game_engine.TrainSchedule;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TrainFactory {

    public Train create(String trainNumber, String name, String direction, Map<String, Integer> stationDistanceMap)
            throws IOException, ParserConfigurationException, SAXException {
        return createWithMockTime(trainNumber, name, direction, stationDistanceMap, Clock.systemDefaultZone());
    }

    public Train createWithMockTime(String trainNumber, String name, String direction, Map<String, Integer> stationDistanceMap, Clock systemClock)
            throws IOException, ParserConfigurationException, SAXException {
        TrainDirection directionEnum = null;
        if(direction.equals("TowardsHome"))
            directionEnum = TrainDirection.TOWARDS_HOME;
        else if(direction.equals("AwayFromHome"))
            directionEnum = TrainDirection.AWAY_FROM_HOME;
        List<TrainSchedule> scheduledStops = populateTrainData(trainNumber, directionEnum, stationDistanceMap);
        TrainPosition initialTrainPosition = determineTrainInitialPosition(directionEnum, scheduledStops, systemClock);
        return new Train(trainNumber, name, directionEnum, scheduledStops, initialTrainPosition);
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
    private List<TrainSchedule> populateTrainData(String trainNumber, TrainDirection direction, Map<String, Integer> stationDistanceMap)
            throws IOException, SAXException, ParserConfigurationException {
        System.out.printf( "Loading data for %1$s\n", trainNumber);
        List<TrainSchedule> scheduledStops = new OvernightTravelDecorator(
                new TrainScheduleInitializer(trainNumber, direction, stationDistanceMap)).populateTrainData();
        return scheduledStops;
    }

    /**
     * Determines the position of the train on game load. This has 3 scenarios:
     * # Is the train beyond the section (either not yet entered or has exit already)?78.181854
     * # Is the train stopped at a station on the section?
     * # Is the train running between stations?
     *
     * @return
     */
    private TrainPosition determineTrainInitialPosition(TrainDirection direction, List<TrainSchedule> scheduledStops, Clock systemClock) {
        // Has the train not yet entered the section?
        LocalDateTime currentTime = LocalDateTime.now(systemClock);
        TrainSchedule firstScheduledStop = scheduledStops.get(0);
        if (currentTime.isBefore(firstScheduledStop.getArrivalTime())) {
            TrainPosition trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN,
                    60 * (currentTime.until(firstScheduledStop.getArrivalTime(), ChronoUnit.MINUTES) / 60f));
            return trainPosition;
        }
        // Has the train exited the section?
        TrainSchedule lastScheduledStop = scheduledStops.get(scheduledStops.size() - 1);
        if (currentTime.isAfter(lastScheduledStop.getDepartureTime())) {
            TrainPosition trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN,
                    60 * (lastScheduledStop.getDepartureTime().until(currentTime, ChronoUnit.MINUTES) / 60f));
            return trainPosition;
        }
        ListIterator<TrainSchedule> scheduledStopsIterator = scheduledStops.listIterator();
        while (scheduledStopsIterator.hasNext()) {
            //Is the train stopped at a station on the section?
            TrainSchedule schedule = scheduledStopsIterator.next();
            if (schedule.getArrivalTime().equals(currentTime) || schedule.getDepartureTime().equals(currentTime)
                    || (schedule.getArrivalTime().isBefore(currentTime) && schedule.getDepartureTime().isAfter(currentTime))) {
                TrainPosition trainPosition = new TrainPosition(TrainRunningStatus.SCHEDULED_STOP, schedule.getDistance());
                return trainPosition;
            }
            // Is the train running between stations?
            if (scheduledStopsIterator.hasNext()) {
                TrainSchedule nextStop = scheduledStopsIterator.next();
                if (currentTime.isAfter(schedule.getDepartureTime()) && currentTime.isBefore(nextStop.getArrivalTime())) {
                    TrainPosition trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN,
                            determineInitialDistanceFromHome(schedule, nextStop, direction, systemClock));
                    return trainPosition;
                }
                scheduledStopsIterator.previous();
            }
        }
        return null;
    }

    private float determineInitialDistanceFromHome(TrainSchedule crossedStation, TrainSchedule upcomingStation,
            TrainDirection direction, Clock systemClock) {
        int distanceBetweenStations = Math.abs(crossedStation.getDistance() - upcomingStation.getDistance());
        float timeAsPerScheduleInHours = crossedStation.getDepartureTime()
                .until(upcomingStation.getArrivalTime(), ChronoUnit.MINUTES) / 60f;
        float expectedSpeedOfTrain = distanceBetweenStations / timeAsPerScheduleInHours;

        float timeSinceLastCrossedStation = crossedStation.getDepartureTime()
                .until(LocalDateTime.now(systemClock), ChronoUnit.MINUTES) / 60f;
        if (direction == TrainDirection.AWAY_FROM_HOME)
            return expectedSpeedOfTrain * timeSinceLastCrossedStation;
        else
            return 86 - (expectedSpeedOfTrain * timeSinceLastCrossedStation);
    }
}
