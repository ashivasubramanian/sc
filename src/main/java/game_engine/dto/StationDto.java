package game_engine.dto;

import common.models.SignalAspect;

/**
 * The <code>StationDto</code> class holds data that is exposed by the game engine
 * for each station. This data is constantly updated & exposed by the game engine.
 */
public class StationDto {
    private String name;
    
    private int distanceFromHome;
    
    private SignalAspect aspects[];

    public StationDto(String name, int distanceFromHome, SignalAspect[] signalAspects) {
        this.name = name;
        this.distanceFromHome = distanceFromHome;
        this.aspects = signalAspects;
    }

    public String getName() {
        return name;
    }

    public int getDistanceFromHome() {
        return distanceFromHome;
    }

    public SignalAspect[] getAspects() {
        return aspects;
    }
}
