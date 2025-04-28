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

class OvernightTravelDecorator {

    private TrainScheduleInitializer trainScheduleInitializer;

    public OvernightTravelDecorator(TrainScheduleInitializer trainScheduleInitializer) {
        this.trainScheduleInitializer = trainScheduleInitializer;
    }

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
