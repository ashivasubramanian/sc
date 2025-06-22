package game_engine;

import java.time.LocalDateTime;

public class TrainSchedule {

    /**
     * Arrival time at the station.
     */
    private LocalDateTime arrivalTime;

    /**
     * Departure time from that station.
     */
    private LocalDateTime departureTime;

    public TrainSchedule(LocalDateTime arrivalTime, LocalDateTime departureTime) {
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() { return this.arrivalTime; }

    public LocalDateTime getDepartureTime() { return this.departureTime; }

}
