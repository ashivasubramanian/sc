package game_engine;

import common.models.SignalAspect;

public class Track {

    enum TrackType {
        /**
         * A constant <code>Integer</code> for the main track.
         * A station will always have this track - no station can exist without this track.
         * This track will not have platforms. Platforms are always on loop tracks.
         * So if a train needs to stop at a station, it must have loop tracks.
         */
        MAIN_TRACK,

        /**
         * A constant <code>Integer</code> for the loop track.
         * Note that a station may or may not have loop tracks, but all stations shall
         * contain a main track. Hence the presence of this track in a station is
         * not guaranteed.
         */
        LOOP_TRACK
    }

    private final TrackType trackType;

    /**
     * An array of aspects inside the station. This is
     * initialized to STOP by the constructor. <p>At any point during the game, any
     * aspect of the station can be changed using the
     * <code>setAspect(int, String)</code> method. All the aspects can be retrieved
     * using <code>getAspects()</code>. Currently, there is no way to obtain only
     * a particular aspect.
     */
    SignalAspect[] aspects;

    public Track(TrackType typeOfTrack) {
        this.trackType = typeOfTrack;
        aspects = new SignalAspect[] {SignalAspect.STOP, SignalAspect.STOP};
    }

    public TrackType getTrackType() {
        return this.trackType;
    }

    public SignalAspect[] getAspects() {
        return aspects;
    }
}
