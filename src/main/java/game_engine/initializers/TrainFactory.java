package game_engine.initializers;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import game_engine.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory class to create <code>Train</code> objects.
 * The reason to have a factory is to avoid <code>Train</code> having state unrelated to its needs,
 * and also to have a common class for creating <code>Train</code>s for testing and real use.
 */
public class TrainFactory {

    /**
     * Create <code>Train</code> instances with the train running on current system time.
     *
     * @param trainNumber                   the train's number
     * @param name                          the train's name
     * @param direction                     the direction of travel. It can only be one of "TowardsHome" or "AwayFromHome"
     * @param stations                      a list of stations on the section
     * @return                              the <code>Train</code> instance
     * @throws IOException                  if any exception occurs during train XML I/O
     * @throws ParserConfigurationException if any exception occurs while parsing train XML content
     * @throws SAXException                 if any exception occurs while parsing train XML content
     */
    public Train create(String trainNumber, String name, String direction, List<Station> stations)
            throws IOException, ParserConfigurationException, SAXException {
        return createWithMockTime(trainNumber, name, direction, stations, Clock.systemDefaultZone());
    }

    /**
     * Create <code>Train</code> instances with the train running on mock time.
     * The mock time will be specified using the <code>systemClock</code> parameter,
     * and the train will load assuming the current time is the time in <code>systemClock</code>.
     * <br><br> It is expected this method will only be used for testing purposes.
     *
     * @param trainNumber                   the train's number
     * @param name                          the train's name
     * @param direction                     the direction of travel. It can only be one of "TowardsHome" or "AwayFromHome"
     * @param stations                      a list of stations on the section
     * @param systemClock                   the <code>Clock</code> that the train assumes is current time.
     * @return                              the <code>Train</code> instance
     * @throws IOException                  if any exception occurs during train XML I/O
     * @throws ParserConfigurationException if any exception occurs while parsing train XML I/O
     * @throws SAXException                 if any exception occurs while parsing train XML I/O
     */
    public Train createWithMockTime(String trainNumber, String name, String direction, List<Station> stations, Clock systemClock)
            throws IOException, ParserConfigurationException, SAXException {
        TrainDirection directionEnum = null;
        if(direction.equals("TowardsHome"))
            directionEnum = TrainDirection.TOWARDS_HOME;
        else if(direction.equals("AwayFromHome"))
            directionEnum = TrainDirection.AWAY_FROM_HOME;
        Timetable timetable = populateTrainData(trainNumber, directionEnum, stations);
        List<TrainSchedule> scheduledStops = timetable.getEntries().stream()
                .filter(entry -> entry.getSchedule().isPresent())
                .map(entry -> entry.getSchedule().get())
                .collect(Collectors.toList());
        TrainPosition initialTrainPosition = determineTrainInitialPosition(directionEnum, scheduledStops, systemClock);
        return new Train(trainNumber, name, directionEnum, scheduledStops, initialTrainPosition);
    }

    /**
     * Populates the train with a timetable of stations,
     * the distances of those stations, and the arrival and departure times at those stations.
     * <br><br>The order of the stations corresponds to the direction of the train.
     * For example, if the train travels from home station to its final station, then the timetable will have a list
     * of stations, starting from the home station and ends at the final station. If the train
     * travels from the final station to home, then the list is reversed.
     * <br><br>This method also takes care to ensure overnight trains have their dates adjusted correctly.
     *
     * @param trainNumber                   the train's number
     * @param direction                     the direction of travel
     * @param stations                      a list of stations on the section.
     * @return                              a timetable.
     * @throws IOException                  if any exception occurs during train XML I/O
     * @throws ParserConfigurationException if any exception occurs while parsing train XML I/O
     * @throws SAXException                 if any exception occurs while parsing train XML I/O
     */
    private Timetable populateTrainData(String trainNumber, TrainDirection direction, List<Station> stations)
            throws IOException, SAXException, ParserConfigurationException {
        System.out.printf( "Loading data for %1$s\n", trainNumber);
        Timetable timetable = new TrainScheduleInitializer(trainNumber, direction, stations).populateTrainData();
        return timetable;
    }

    /**
     * Determines the position of the train on game load.<br>This has 3 scenarios:
     * <ol>
     * <li>Is the train beyond the section (either not yet entered or has exit already)?</li>
     * <li>Is the train stopped at a station on the section?</li>
     * <li>Is the train running between stations?</li>
     * </ol>
     *
     * @param direction      the current direction of the train
     * @param scheduledStops the stops for the train, as per its timetable
     * @param systemClock    the current time
     * @return               the current position of the train
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

        //Is the train stopped at a station on the section?
        Optional<TrainPosition> trainPositionAtStation = scheduledStops.stream()
                .filter(sch -> sch.getArrivalTime().equals(currentTime) || sch.getDepartureTime().equals(currentTime)
                        || (sch.getArrivalTime().isBefore(currentTime) && sch.getDepartureTime().isAfter(currentTime)))
                .map(sch -> new TrainPosition(TrainRunningStatus.SCHEDULED_STOP, sch.getDistance()))
                .findFirst();
        if (trainPositionAtStation.isPresent()) return trainPositionAtStation.get();

        // Is the train running between stations?
        ListIterator<TrainSchedule> scheduledStopsIterator = scheduledStops.listIterator();
        while (scheduledStopsIterator.hasNext()) {
            TrainSchedule schedule = scheduledStopsIterator.next();

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

    /**
     * Determines, on game load, the distance of the train from the Home station.
     * The method assumes the train is on the section and is currently between two stations,
     * <code>crossedStation</code> and <code>upcomingStation</code>. Based on the schedules at those stations, the method
     * calculates the speed the train should have in order to reach <code>upcomingStation</code> on-time. Based on this
     * speed, it returns the distance the train has covered until now.
     *
     * @param crossedStation  the station that was just crossed
     * @param upcomingStation the station that is upcoming
     * @param direction       the direction of the train
     * @param systemClock     the current time
     * @return                the current distance from Home station
     */
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
