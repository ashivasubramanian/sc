package game_engine;

import java.time.LocalDateTime;

public class TrainSchedule {

    /**
     * The station code of the station where the train will halt.
     */
    private String stationCode;

    /**
     * Arrival time at the station.
     */
    private LocalDateTime arrivalTime;

    /**
     * Departure time from that station.
     */
    private LocalDateTime departureTime;

    public TrainSchedule(String stationCode, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        this.stationCode = stationCode;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public String getStationCode() { return this.stationCode; }

    public LocalDateTime getArrivalTime() { return this.arrivalTime; }

    public LocalDateTime getDepartureTime() { return this.departureTime; }
}
