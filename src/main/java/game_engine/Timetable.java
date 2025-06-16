package game_engine;

import common.models.TrainDirection;

import java.time.LocalDateTime;
import java.util.*;

/**
 * The <code>Timetable</code> class represents the timetable of a train.
 * It has entries for all stations on the section, whether the train stops at that station or not. For stations where
 * the train stops, it also has the <code>TrainSchedule</code> for that station.
 * <br><br>
 * By default, the timetable sorts entries in the ascending order of their station distances from the home station.
 * This works for trains moving away from home as they will encounter the stations in the same order, but not for trains
 * moving towards home. Developers must call the {@link Timetable#sortTowardsHome()} method to arrange the timetable
 * correctly for trains moving towards home.
 */
public class Timetable {

    /**
     * The <code>Entry</code> class represents one timetable entry.
     * It has a <code>Station</code> and an optional <code>TrainSchedule</code>.
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
         * Creates an <code>Entry</code> instance.
         *
         * @param station  the station where the train stops or passes through.
         * @param schedule the schedule of the train.
         */
        public Entry(Station station, Optional<TrainSchedule> schedule) {
            this.station = station;
            this.schedule = schedule;
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
    }
    /**
     * A collection of <code>Entry</code> instances that represent the train's timetable.
     */
    private List<Entry> timetableEntries = new ArrayList<>();

    public Timetable(List<Station> stationsOnSection, TrainDirection direction) {
        if (direction == TrainDirection.TOWARDS_HOME)
            stationsOnSection.sort(Comparator.reverseOrder());
        else
            stationsOnSection.sort(Comparator.naturalOrder());
        stationsOnSection.stream().forEach(s -> timetableEntries.add(new Entry(s, Optional.empty())));
    }

    /**
     * Updates the station in the timetable with the arrival & departure time.
     * <br><br>
     * This method performs certain modifications to the passed in times:
     * <ol>
     * <li> If <code>departureTime</code> is before <code>arrivalTime</code>, then the method increments <code>departureTime</code>'s
     * date by 1 day.</li>
     * <li> If <code>arrivalTime</code> is before the departure time of the previous station that had a <code>TrainSchedule</code>,
     * then the method increments the date of both <code>arrivalTime</code> and <code>departureTime</code> by 1 day.</li>
     * </ol>
     * <br>The method does these as it assumes that these scenarios are due to an overnight train's schedules
     * crossing into the next day.
     * <br>
     * NOTE: The implication of the 2nd modification is that calls to this method must only be made in the order of the
     * stations that the train will encounter. Otherwise, the 2nd modification will not be performed and the timetable
     * will have invalid times.
     *
     * @param station       the station where the train has a stop.
     * @param arrivalTime   the arrival time of the train at the station
     * @param departureTime the departure time of the train from the station
     */
    public void update(Station station, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        if (departureTime.isBefore(arrivalTime))
            departureTime = departureTime.plusDays(1);
        Entry entry = this.timetableEntries.stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase(station.getCode()))
                .findFirst().get();
        int index = this.timetableEntries.indexOf(entry);
        Entry previousStop = this.timetableEntries.subList(0, index).stream()
                .filter(e -> e.getSchedule().isPresent())
                .sorted(Comparator.reverseOrder())
                .findFirst().orElseGet(() -> new Entry(station, Optional.empty()));
        if (previousStop.getSchedule().isPresent() && previousStop.getSchedule().get().getDepartureTime().isAfter(arrivalTime)) {
            arrivalTime = arrivalTime.plusDays(1);
            departureTime = departureTime.plusDays(1);
        }
        TrainSchedule trainSchedule = new TrainSchedule(station.getCode(), arrivalTime, departureTime, station.getDistance());
        this.timetableEntries.set(
                index,
                new Entry(station, Optional.of(trainSchedule)));
    }

    /**
     * Arranges the stations of the timetable in the order a train moving towards home will encounter.
     * There is no equivalent methods for trains moving away from home as this is taken care automatically.
     */
    public void sortTowardsHome() {
        this.timetableEntries.sort(Comparator.reverseOrder());
    }

    //TODO: This is a temporary method used only for the refactoring to Timetable. Remove once done.
    public List<Entry> getEntries() {
        return this.timetableEntries;
    }
}
