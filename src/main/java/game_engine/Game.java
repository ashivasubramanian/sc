package game_engine;

import common.models.SignalAspect;
import game_engine.data_access.DataAccess;
import game_engine.dto.StationDto;
import game_engine.dto.TrainDto;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The Game class represents an instance of the Section Controller game currently being
 * played. In other words, this class is the "game engine" for Section Controller.
 * Clients can use the methods exposed by this class to show to their users the
 * state of the game.
 */
public class Game {

    /**
     * A collection of stations in the game currently being played.
     */
    private List<Station> stations;
    /**
     * A collection of trains that are currently operating.
     */
    private List<Train> trains;

    /**
     * Initializes the game instance.
     * @throws GameNotStartedException if there were some problems while starting the game
     */
    public Game() throws GameNotStartedException {
        this.stations = new ArrayList<>();
        this.trains = new ArrayList<>();
        populateStations();
        populateTrains();
    }

    /**
     * Loads station data for all stations on the section.
     * @throws GameNotStartedException if a problem occurs while reading the section's XML file.
     */
    private void populateStations() throws GameNotStartedException {
        this.stations = new ArrayList<>();
        InputStream stationsXMLStream = getClass().getResourceAsStream("/data/CAL-SRR.xml");
        try {
            Vector<Element> stationsFromXMLFile = DataAccess.getInstance().extractData(stationsXMLStream, "station");
            this.stations = stationsFromXMLFile.stream().map(element -> new Station(element))
                    .collect(Collectors.toList());
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            throw new GameNotStartedException(ex);
        }
    }

    /**
     * Loads train data for all the trains that will run in the next one hour.
     * @throws GameNotStartedException if a problem occurs while reading either the section XML file
     *         or while reading any of the trains' XML files.
     */
    private void populateTrains() throws GameNotStartedException {
        try {
            //Let's get the daily trains first
            InputStream trainsXMLStream = getClass().getResourceAsStream("/data/CAL-SRR.xml");
            Vector<Element> temp1 = DataAccess.getInstance().extractData(trainsXMLStream, "train[@day-of-arrival=Daily]");
            
            String day = "";
            //Let's now find out what day it is, and then get the corresponding trains.
            switch (new GregorianCalendar().get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    day = "Su";
                    break;
                case Calendar.MONDAY:
                    day = "M";
                    break;
                case Calendar.TUESDAY:
                    day = "Tu";
                    break;
                case Calendar.WEDNESDAY:
                    day = "W";
                    break;
                case Calendar.THURSDAY:
                    day = "Th";
                    break;
                case Calendar.FRIDAY:
                    day = "F";
                    break;
                case Calendar.SATURDAY:
                    day = "Sa";
            }
            trainsXMLStream = getClass().getResourceAsStream("/data/CAL-SRR.xml");
            Vector<Element> temp2 = DataAccess.getInstance().extractData(trainsXMLStream, "train.contains(@day-of-arrival," + day + ")");
            
            temp1.addAll(temp1.size(), temp2);
            //temp1 is for Renderer; whereas the processing below is for GameScreen
            for (Element train : temp1) {
                /*All trains for the day have been loaded. But we need only the trains that will
                start from the first station in the next hour. So we check if the current time
                falls within the train's first and last station times.*/
                Calendar currentTime = Calendar.getInstance();
                String fs_time = "", ls_time = "";
                //get the first station time
                fs_time = train.getAttribute("section-entry-time");
                ls_time = train.getAttribute("section-leaving-time");
                //We have got the times; let us convert them into Calendar instances.
                String[] time1 = fs_time.split(":");
                Calendar first_station_time = Calendar.getInstance();
                int fs_hour = Integer.parseInt(time1[0]);
                first_station_time.set(Calendar.HOUR_OF_DAY, fs_hour);
                first_station_time.set(Calendar.MINUTE, Integer.parseInt(time1[1]));
                String[] time2 = ls_time.split(":");
                Calendar last_station_time = Calendar.getInstance();
                int ls_hour = Integer.parseInt(time2[0]);
                last_station_time.set(Calendar.HOUR_OF_DAY, ls_hour);
                last_station_time.set(Calendar.MINUTE, Integer.parseInt(time2[1]));
                
                if (fs_hour >= 20) {
                    if (ls_hour >= 0 && ls_hour <= 6) {
                        int current_date = last_station_time.get(Calendar.DATE);
                        last_station_time.set(Calendar.DATE, current_date + 1);
                    }
                }
                
                //We are ready to compare.
                if (currentTime.after(first_station_time) && currentTime.before(last_station_time)) {
                    Train individual_train = new Train(train.getAttribute("number"), train.getAttribute("name"), train.getAttribute("direction"));
                    trains.add(individual_train);
                }
            }
            System.out.println("Total trains within the next one hour: " + this.trains.size());
            this.trains.stream().forEach(train -> System.out.println(train.getNumber() + " " + train.getTrainName()));
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            throw new GameNotStartedException(ex);
        }
    }

    /**
     * Returns an immutable collection of all the trains that are currently running on the section.
     * Clients can repeatedly call this to get constant updates on the trains.
     * @return an immutable collection of <code>TrainDto</code> objects that describes each train.
     */
    public List<TrainDto> getTrains() {
        List<TrainDto> trainsDto = this.trains.stream()
                .map(train -> new TrainDto(train.getTrainName(), train.getDistance(), train.getDirection()))
                .collect(Collectors.toList());
        return Collections.unmodifiableList(trainsDto);
    }

    /**
     * Returns an immutable collection of all the stations that are currently running on the section.
     * Clients can repeatedly call this to get constant updates on the stations.
     * @return an immutable collection of <code>StationDto</code> objects that describes each station.
     */
    public List<StationDto> getStations() {
        List<StationDto> stationsDto = this.stations.stream()
                .map(station -> new StationDto(station.getName(), station.getDistance(), station.getAspects()))
                .collect(Collectors.toList());
        return Collections.unmodifiableList(stationsDto);
    }

    public void setStationAspect(String stationName, SignalAspect[] signalAspect) {
        Station station = this.stations.stream()
                .filter(singleStation -> singleStation.getName().equals(stationName))
                .findFirst().get();
        int i = 0;
        while (i < 2) {
            station.setAspect(i, signalAspect[i]);
            i++;
        }
    }

}
