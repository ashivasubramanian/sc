package game_engine.runners;

import game_engine.Timetable;
import game_engine.TrainPosition;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Class that implements the logic to move trains across the section.
 * It is intended that this class will be used as tasks, by something like Executor framework or similar,
 * to move the trains across the section. These tasks must be repeatedly executed to give the effect of movement across
 * the section.
 */
public class TrainRunner implements Runnable {

    /**
     * The timetable of the train that determines how the train should move across the section.
     */
    private Timetable timetable;

    /**
     * The train's current position that will be updated by this class.
     */
    private TrainPosition trainPosition;

    /**
     * Creates an instance of the <code>TrainRunner</code>.
     * The class will use the <code>timetable</code>, to update the <code>trainPosition</code>.
     * @param timetable     the train's timetable that helps determine where the train should be.
     * @param trainPosition an instance of <code>TrainPosition</code> that is updated with the train's current position.
     */
    public TrainRunner(Timetable timetable, TrainPosition trainPosition) {
        this.timetable = timetable;
        this.trainPosition = trainPosition;
    }

    /**
     * Method that implements logic to move trains across the section.
     * Multiple invocations of this task by the Executor framework or some other similar mechanism, will result in
     * constant updates to <code>trainPosition</code>, giving the effect of the train moving across the section.
     */
    @Override
    public void run() {
        //Let's get the time first, then we can calculate where each train is supposed to
        //be at the time the game is started.
		/*Now let's get the arrival (or departure) time of the train in the station it
		encounters first while entering the section. If initialTime is greater, then the
		train has already entered the section. We then calculate the number of seconds
		between initialTime and the time at first station. Depending upon this, we
		calculate the distance the train would have crossed as 60(the MPS) * seconds. We
		then draw it on the screen.*/
        LocalDateTime first_station_time = this.timetable.getSectionEntryTime();
        LocalDateTime currentTime = LocalDateTime.now();
        if(first_station_time.isBefore(currentTime)) {
            //The train is already in the section.
            float totalseconds = first_station_time.until(currentTime, ChronoUnit.SECONDS);
            trainPosition.setDistanceFromHome(60 * (totalseconds/3600));
        }
   }
}
