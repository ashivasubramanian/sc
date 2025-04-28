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

class TrainScheduleInitializer {

    private final String trainNumber;
    private final TrainDirection direction;
    private final Map<String, Integer> stationDistanceMap;

    public TrainScheduleInitializer(String trainNumber, TrainDirection direction, Map<String, Integer> stationDistanceMap) {
        this.trainNumber = trainNumber;
        this.direction = direction;
        this.stationDistanceMap = stationDistanceMap;
    }

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
