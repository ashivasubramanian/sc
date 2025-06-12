package game_engine;

import java.util.List;
import java.util.Optional;

/**
 * The <code>Timetable</code> class represents the timetable of a train.
 * It has entries for all stations on the section, whether the train stops at that station or not. For stations where
 * the train stops, it also has the <code>TrainSchedule</code> for that station.
 */
public class Timetable {

    /**
     * The <code>Entry</code> class represents one timetable entry.
     * It has a <code>Station</code> and an optional <code>TrainSchedule</code>.
     */
    public class Entry {

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

    }

    /**
     * A collection of <code>Entry</code> instances that represent the train's timetable.
     */
    private List<Entry> timetableEntries;
}
