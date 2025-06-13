package game_engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        public Entry(Station station, TrainSchedule schedule) {
            this.station = station;
            this.schedule = Optional.of(schedule);
        }

        @Override
        public int compareTo(Entry other) {
            return this.station.getDistance().compareTo(other.station.getDistance());
        }

        //TODO: This is a temporary method used only for the refactoring to Timetable. Remove once done.
        public TrainSchedule getSchedule() {
            return this.schedule.get();
        }
    }

    /**
     * A collection of <code>Entry</code> instances that represent the train's timetable.
     */
    private List<Entry> timetableEntries = new ArrayList<>();

    /**
     * Adds the station & its schedule to the timetable.
     * @param station       the station where the train has a stop.
     * @param trainSchedule the schedule of the train
     */
    public void add(Station station, TrainSchedule trainSchedule) {
        this.timetableEntries.add(new Entry(station, trainSchedule));
    }

    /**
     * Arranges the stations of the timetable in the order a train moving towards home will encounter.
     * There is no equivalent methods for trains moving away from home as this is taken care automatically.
     */
    public void sortTowardsHome() {
        this.timetableEntries.sort(Comparator.reverseOrder());
    }

    //TODO: This is a temporary method used only for the refactoring to Timetable. Remove once done.
    public List<TrainSchedule> getSchedules() {
        return this.timetableEntries.stream().map(Entry::getSchedule).collect(Collectors.toList());
    }
}
