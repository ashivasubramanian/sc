package game_engine;

import common.models.TrainDirection;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The <code>Timetable</code> class represents the timetable of a train.
 * It has entries for all stations on the section, whether the train stops at that station or not. For stations where
 * the train stops, it also has the <code>TrainSchedule</code> for that station.
 * <br><br>
 * By default, the timetable sorts entries in the order the train will encounter them.
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
        TrainSchedule trainSchedule = new TrainSchedule(arrivalTime, departureTime);
        this.timetableEntries.set(
                index,
                new Entry(station, Optional.of(trainSchedule)));
    }

    /**
     * Returns the time the train entered the section. This is typically the arrival time at the first station.
     * @return the section entry time.
     */
    public LocalDateTime getSectionEntryTime() {
        return this.timetableEntries.get(0).getSchedule().get().getArrivalTime();
    }

    /**
     * Returns the time the train exited the section. This is typically the departure time at the last station.
     * @return the section exit time.
     */
    public LocalDateTime getSectionExitTime() {
        return this.timetableEntries.get(this.timetableEntries.size() - 1).getSchedule().get().getDepartureTime();
    }

    /**
     * Returns the station at which the train should be at as per the timetable, for the given <code>currentTime</code>.
     *
     * @param currentTime the current time. This parameter is present so that we can pass in mock times for unit testing
     *                    purposes.
     * @return the station at which the train should be. If the train is not supposed to be at a station, then it returns
     *         <code>Optional.empty()</code>.
     */
    public Optional<Station> getStationHaltedAt(LocalDateTime currentTime) {
        Optional<Entry> entry = this.timetableEntries.stream()
                .filter(e -> e.getSchedule().isPresent())
                .filter(e -> {
                    LocalDateTime arrivalTime = e.getSchedule().get().getArrivalTime();
                    LocalDateTime departureTime = e.getSchedule().get().getDepartureTime();
                    return arrivalTime.equals(currentTime) || departureTime.equals(currentTime)
                            || (arrivalTime.isBefore(currentTime) && departureTime.isAfter(currentTime));
                })
                .findFirst();
        return entry.map(Entry::getStation);
    }

    /**
     * Returns an array of the stations on the section the train is travelling between as per the timetable, for the given
     * <code>currentTime</code>. <br>
     * NOTES about the array returned:
     * <ol>
     *     <li>The array returned is always 2 elements only.</li>
     *     <li>The first element is the station the train just departed from.</li>
     *     <li>The second element is the station the train is moving to.</li>
     *     <li>If the train is not moving between any stations on the section, then both elements are <code>Optional.empty()</code></li>
     *     <li>If the train is beyond the section, then both elements are <code>Optional.empty()</code></li>
     * </ol>
     *
     * @param currentTime the current time. This parameter is present so that we can pass in mock times for unit testing
     *                    purposes.
     * @return an array of the stations the train is moving between.
     */
    public Optional<Station>[] getStationsTravellingBetween(LocalDateTime currentTime) {
        ListIterator<Entry> entriesIterator = this.timetableEntries.stream()
                .filter(e -> e.getSchedule().isPresent())
                .collect(Collectors.toList())
                .listIterator();
        while (entriesIterator.hasNext()) {
            Entry firstEntry = entriesIterator.next();

            if (entriesIterator.hasNext()) {
                Entry nextEntry = entriesIterator.next();
                if (currentTime.isAfter(firstEntry.getSchedule().get().getDepartureTime())
                        && currentTime.isBefore(nextEntry.getSchedule().get().getArrivalTime())) {
                    return new Optional[] {Optional.of(firstEntry.getStation()),
                            Optional.of(nextEntry.getStation())};
                }
                entriesIterator.previous();
            }
        }
        return new Optional[] {Optional.empty(), Optional.empty()};
    }

    /**
     * Returns the schedule of the train at the station, as per the timetable.
     *
     * @param station the station for which the schedule is required.
     * @return the schedule of the train at that station. If the train doesn't stop, then it returns <code>Optional.empty()</code>
     */
    public Optional<TrainSchedule> getSchedule(Station station) {
        Optional<Entry> entry = this.timetableEntries.stream()
                .filter(e -> e.getStation().getCode().equalsIgnoreCase(station.getCode()))
                .findFirst();
        if (entry.isPresent()) {
            return entry.get().getSchedule();
        } else
            return Optional.empty();
    }

    //TODO: This is a temporary method used only for the refactoring to Timetable. Remove once done.
    public List<Entry> getEntries() {
        return this.timetableEntries;
    }
}
