package game_engine.initializers;

import common.models.TrainDirection;
import game_engine.*;
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
import java.util.stream.Collectors;

/**
 * Loads the train's schedules from the train's XML file.<br>
 * This class is package-scoped as anyone looking for train schedules must use the <code>Train</code> object
 * created via the <code>TrainFactory</code>.<br>
 */
class TrainScheduleInitializer {

    private final String trainNumber;
    private final TrainDirection direction;
    private final List<Station> stations;

    /**
     * Creates an instance of this class.
     *
     * @param trainNumber the train's number
     * @param direction   the direction of travel
     * @param stations    a list of stations on the section
     */
    public TrainScheduleInitializer(String trainNumber, TrainDirection direction, List<Station> stations) {
        this.trainNumber = trainNumber;
        this.direction = direction;
        this.stations = stations;
    }

    /**
     * Reads the train's XML file and creates a <code>Timetable</code> updated with the train's scheduled stops.
     *
     * @return                              the train's timetable.
     * @throws IOException                  if any exception occurs during train XML I/O
     * @throws ParserConfigurationException if any exception occurs while parsing train XML content
     * @throws SAXException                 if any exception occurs while parsing train XML content
     */
    public Timetable populateTrainData()
            throws IOException, ParserConfigurationException, SAXException {
        String filePath = String.format("/data/%1$s.xml", trainNumber);
        InputStream trainXMLStream = getClass().getResourceAsStream(filePath);
        Vector<Element> stops = DataAccess.getInstance().extractData(trainXMLStream,"stop");
        List<Entry> timetableEntries = stops.stream()
                .map(stop -> {
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

                    boolean isOriginatingStation = Boolean.parseBoolean(stop.getAttribute("originating-station"));
                    boolean isTerminatingStation = Boolean.parseBoolean(stop.getAttribute("terminating-station"));

                    Station station = this.stations.stream()
                            .filter(s -> s.getCode().equalsIgnoreCase(stationCode)).findFirst().get();
                    try {
                        return new Entry(station,
                                Optional.of(new TrainSchedule(arrivalTime, departureTime)),
                                StopType.valueOf(isOriginatingStation, isTerminatingStation));
                    } catch (GameNotStartedException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        Timetable timetable = new Timetable(this.stations, timetableEntries, this.direction);
        return timetable;
    }
}
