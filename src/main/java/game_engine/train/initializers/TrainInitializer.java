package game_engine.train.initializers;

import common.models.TrainDirection;
import common.models.TrainRunningStatus;
import game_engine.Train;
import game_engine.TrainPosition;
import game_engine.TrainSchedule;
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
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

public class TrainInitializer {

    public void initialize(Train train, Clock systemClock, Map<String, Integer> stationDistanceMap)
            throws IOException, ParserConfigurationException, SAXException {
        populateTrainData(train);
        determineTrainInitialPosition(train, systemClock, stationDistanceMap);
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
    private void populateTrainData(Train train)
            throws IOException, SAXException, ParserConfigurationException {
        System.out.printf( "Loading data for %1$s\n", train.getNumber());
        String filePath = String.format("/data/%1$s.xml", train.getNumber());
        InputStream trainXMLStream = getClass().getResourceAsStream(filePath);
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
                train.getScheduledStops().add(new TrainSchedule(stationCode, arrivalTime, departureTime));
            }
        }
        //Reversing the distances if travelling towards home
        if (train.getDirection() == TrainDirection.TOWARDS_HOME)
            java.util.Collections.reverse(train.getScheduledStops());
    }

    /**
     * Determines the position of the train on game load. This has 3 scenarios:
     * # Is the train beyond the section (either not yet entered or has exit already)?78.181854
     * # Is the train stopped at a station on the section?
     * # Is the train running between stations?
     */
    private void determineTrainInitialPosition(Train train, Clock systemClock, Map<String, Integer> stationDistanceMap) {
        // Has the train not yet entered the section?
        LocalDateTime currentTime = LocalDateTime.now(systemClock);
        TrainSchedule firstScheduledStop = train.getScheduledStops().get(0);
        if (currentTime.isBefore(firstScheduledStop.getArrivalTime())) {
            TrainPosition trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN,
                    60 * (currentTime.until(firstScheduledStop.getArrivalTime(), ChronoUnit.MINUTES) / 60f));
            train.setTrainPosition(trainPosition);
            return;
        }
        // Has the train exited the section?
        TrainSchedule lastScheduledStop = train.getScheduledStops().get(train.getScheduledStops().size() - 1);
        if (currentTime.isAfter(lastScheduledStop.getDepartureTime())) {
            TrainPosition trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN,
                    60 * (lastScheduledStop.getDepartureTime().until(currentTime, ChronoUnit.MINUTES) / 60f));
            train.setTrainPosition(trainPosition);
            return;
        }
        ListIterator<TrainSchedule> scheduledStopsIterator = train.getScheduledStops().listIterator();
        while (scheduledStopsIterator.hasNext()) {
            //Is the train stopped at a station on the section?
            TrainSchedule schedule = scheduledStopsIterator.next();
            if (schedule.getArrivalTime().equals(currentTime) || schedule.getDepartureTime().equals(currentTime)
                    || (schedule.getArrivalTime().isBefore(currentTime) && schedule.getDepartureTime().isAfter(currentTime))) {
                TrainPosition trainPosition = new TrainPosition(TrainRunningStatus.SCHEDULED_STOP, stationDistanceMap.get(schedule.getStationCode()));
                train.setTrainPosition(trainPosition);
                break;
            }
            // Is the train running between stations?
            if (scheduledStopsIterator.hasNext()) {
                TrainSchedule nextStop = scheduledStopsIterator.next();
                if (currentTime.isAfter(schedule.getDepartureTime()) && currentTime.isBefore(nextStop.getArrivalTime())) {
                    TrainPosition trainPosition = new TrainPosition(TrainRunningStatus.RUNNING_BETWEEN,
                            determineInitialDistanceFromHome(schedule, nextStop, train.getDirection(), systemClock, stationDistanceMap));
                    train.setTrainPosition(trainPosition);
                    break;
                }
                scheduledStopsIterator.previous();
            }
        }
    }

    private float determineInitialDistanceFromHome(TrainSchedule crossedStation, TrainSchedule upcomingStation,
            TrainDirection direction, Clock systemClock, Map<String, Integer> stationDistanceMap) {
        int distanceBetweenStations = Math.abs(stationDistanceMap.get(crossedStation.getStationCode())
                - stationDistanceMap.get(upcomingStation.getStationCode()));
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
