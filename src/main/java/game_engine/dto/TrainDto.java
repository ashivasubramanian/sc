package game_engine.dto;

/**
 * The <code>TrainDto</code> class holds data that is exposed by the game engine
 * for each train that is currently running on the section.
 * This data is constantly updated & exposed by the game engine.
 */
public class TrainDto {
    
    private String name;
    
    private Float distanceFromHome;

    private int direction;

    public TrainDto(String name, Float distanceFromHome, int direction) {
        this.name = name;
        this.distanceFromHome = distanceFromHome;
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public Float getDistanceFromHome() {
        return distanceFromHome;
    }
    
    public int getDirection() {
        return direction;
    }
}
