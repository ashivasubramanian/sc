package game_engine.train.initializers;

import common.models.TrainDirection;
import game_engine.TrainSchedule;
import game_engine.data_access.DataAccess;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Loads the train's schedules from the train's XML file.<br>
 * This class is package-scoped as anyone looking for train schedules must use the <code>Train</code> object
 * created via the <code>TrainFactory</code>.<br>
 * This class is part of a Decorator pattern, with <code>OvernightTravelDecorator</code> intended to be a decorator for
 * this class.
 */
class TrainScheduleInitializer {

    private final String trainNumber;
    private final TrainDirection direction;
    private final Map<String, Integer> stationDistanceMap;

    /**
     * Creates an instance of this class.
     *
     * @param trainNumber        the train's number
     * @param direction          the direction of travel
     * @param stationDistanceMap a mapping of stations to their distances
     */
    public TrainScheduleInitializer(String trainNumber, TrainDirection direction, Map<String, Integer> stationDistanceMap) {
        this.trainNumber = trainNumber;
        this.direction = direction;
        this.stationDistanceMap = stationDistanceMap;
    }

    /**
     * Reads the train's XML file and creates a <code>List</code> of the train's scheduled stops.
     * The method will always return the stops in the order in which the train travels.
     * <br>Note that the method will return the stops with today's date set - if a train is travelling overnight, then
     * the method will return all the stops, but will not adjust the dates accordingly for the after-midnight stops.
     * That is the job of <code>OvernightTravelDecorator</code>. Hence, this method must be called via
     * <code>OvernightTravelDecorator</code> only.
     *
     * @return                              the train's scheduled stops.
     * @throws IOException                  if any exception occurs during train XML I/O
     * @throws ParserConfigurationException if any exception occurs while parsing train XML content
     * @throws SAXException                 if any exception occurs while parsing train XML content
     */
    public List<TrainSchedule> populateTrainData()
            throws IOException, ParserConfigurationException, SAXException {
        String filePath = String.format("/data/%1$s.xml", trainNumber);
        InputStream trainXMLStream = getClass().getResourceAsStream(filePath);
        Vector<Element> stops = DataAccess.getInstance().extractData(trainXMLStream,"stop");
        List<TrainSchedule> scheduledStops = new ArrayList<>();
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
                scheduledStops.add(new TrainSchedule(stationCode, arrivalTime, departureTime, stationDistanceMap.get(stationCode)));
            }
        }
        //Reversing the distances if travelling towards home
        if (direction == TrainDirection.TOWARDS_HOME)
            java.util.Collections.reverse(scheduledStops);
        return scheduledStops;
    }
}
