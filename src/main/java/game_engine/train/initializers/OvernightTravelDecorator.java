package game_engine.train.initializers;

import game_engine.TrainSchedule;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class that ensures the dates are correctly set for overnight trains.
 * <br> This class is package-scoped as anyone looking for train schedules must use the <code>Train</code> object
 * created via the <code>TrainFactory</code>.<br>
 * This class is part of a Decorator pattern with this class intended to decorate <code>TrainScheduleInitializer</code>.
 */
class OvernightTravelDecorator {

    private TrainScheduleInitializer trainScheduleInitializer;

    /**
     * Creates an instance of this class.
     *
     * @param trainScheduleInitializer the <code>TrainScheduleInitializer</code> instance that this class decorates
     */
    public OvernightTravelDecorator(TrainScheduleInitializer trainScheduleInitializer) {
        this.trainScheduleInitializer = trainScheduleInitializer;
    }

    /**
     * Returns a list of the train's scheduled stops.
     * The method calls the decorated <code>TrainScheduleInitializer</code> instance's <code>populateTrainData()</code>
     * to load the train details. It then iterates over each schedule and determines if the day has changed. If yes, the
     * schedule and all upcoming schedules have their dates incremented by 1.
     *
     * @return                              the train's scheduled stops adjusted for overnight operations.
     * @throws IOException                  if any exception occurs during train XML I/O
     * @throws ParserConfigurationException if any exception occurs while parsing train XML content
     * @throws SAXException                 if any exception occurs while parsing train XML content
     */
    public List<TrainSchedule> populateTrainData() throws IOException, ParserConfigurationException, SAXException {
        List<TrainSchedule> schedules = trainScheduleInitializer.populateTrainData();
        if (schedules.get(0).getArrivalTime().isBefore(schedules.get(schedules.size() - 1).getDepartureTime()))
            return schedules;

        AtomicReference<Optional<LocalDateTime>> previousDepartureTime = new AtomicReference<>(Optional.empty());
        AtomicBoolean overnightStop = new AtomicBoolean();
        List<TrainSchedule> updatedSchedules = new ArrayList<>();
        Predicate<TrainSchedule> overnightTrainCondition = sch -> {
            if (sch.getDepartureTime().isBefore(sch.getArrivalTime())) {
                overnightStop.set(true);
                return true;
            }
            if (previousDepartureTime.get().isPresent()) {
                if (sch.getArrivalTime().isBefore(previousDepartureTime.get().get())) {
                    previousDepartureTime.set(Optional.of(sch.getDepartureTime()));
                    return true;
                }
            } else {
                previousDepartureTime.set(Optional.of(sch.getDepartureTime()));
            }
            return false;
        };

        List<TrainSchedule> beforeMidnightStops = schedules.stream().takeWhile(overnightTrainCondition.negate()).collect(Collectors.toList());
        updatedSchedules.addAll(beforeMidnightStops);
        int overnightOccursAtIndex = schedules.indexOf(beforeMidnightStops.get(beforeMidnightStops.size() - 1)) + 1;

        updatedSchedules.addAll(schedules.subList(overnightOccursAtIndex, schedules.size()).stream()
                .map(sch -> {
                    LocalDateTime arrivalTime = sch.getArrivalTime().plusDays(1);
                    if (overnightStop.get()) {
                        arrivalTime = sch.getArrivalTime();
                        overnightStop.set(false);
                    }
                    return new TrainSchedule(sch.getStationCode(), arrivalTime,
                            sch.getDepartureTime().plusDays(1), sch.getDistance());
                }).collect(Collectors.toList()));
        return updatedSchedules;
    }
}
