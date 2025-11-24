package game_engine;

import java.util.Optional;

/**
 * The <code>Entry</code> class represents one timetable entry.
 */
public class Entry implements Comparable<Entry> {

    /**
     * The station where the train passes through (or) stops.
     */
    private Station station;

    /**
     * If the train stops at a station, then this will have a <code>TrainSchedule</code> set.
     * Otherwise, it will be <code>Optional.empty()</code>.
     */
    private Optional<TrainSchedule> schedule;

    /**
     * The type of stop for this train at the station.
     */
    private final StopType stopType;

    /**
     * Creates an <code>Entry</code> instance.
     *
     * @param station  the station where the train stops or passes through.
     * @param schedule the schedule of the train.
     * @param stopType the type of stop for this train at this station.
     */
    public Entry(Station station, Optional<TrainSchedule> schedule, StopType stopType) {
        this.station = station;
        this.schedule = schedule;
        this.stopType = stopType;
    }

    @Override
    public int compareTo(Entry other) {
        return this.station.getDistance().compareTo(other.station.getDistance());
    }

    //TODO: This is a temporary method used only for the refactoring to Timetable. Remove once done.
    public Optional<TrainSchedule> getSchedule() {
        return this.schedule;
    }

    Station getStation() { return this.station; }

    /**
     * Determines if the train's journey originates from this station.
     * @return <code>true</code> if the train's journey originates from this station.
     */
    public boolean isOriginatingStation() {
        return stopType == StopType.ORIGINATING_STATION;
    }

    /**
     * Determines if the train's journey ends at this station.
     * @return <code>true</code> if the train's journey ends at this station.
     */
    public boolean isTerminatingStation() {
        return stopType == StopType.TERMINATING_STATION;
    }
}
