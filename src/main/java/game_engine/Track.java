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

    private SignalAspect towardsHome;

    private SignalAspect awayFromHome;

    public Track(TrackType typeOfTrack) {
        this.trackType = typeOfTrack;
        this.towardsHome = SignalAspect.STOP;
        this.awayFromHome = SignalAspect.STOP;
    }

    public TrackType getTrackType() {
        return this.trackType;
    }

    public void setTowardsHomeAspect(SignalAspect aspect) { this.towardsHome = aspect; }

    public void setAwayFromHomeAspect(SignalAspect aspect) { this.awayFromHome = aspect; }

    SignalAspect getTowardsHomeAspect() { return towardsHome; }

    SignalAspect getAwayFromHomeAspect() { return awayFromHome; }
}
