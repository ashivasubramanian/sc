package game_engine.train.initializers;

import common.models.TrainDirection;
import game_engine.Train;
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
import java.util.Arrays;
import java.util.Vector;

public class TrainInitializer {

    public void initialize(Train train)
            throws IOException, ParserConfigurationException, SAXException {
        populateTrainData(train);
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

}
