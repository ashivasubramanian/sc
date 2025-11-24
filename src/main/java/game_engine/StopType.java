package game_engine;

/**
 * Represents the type of stop the train has at a station.
 */
public enum StopType {

    /**
     * The train's journey starts from here.
     */
    ORIGINATING_STATION,

    /**
     * The train's journey ends here.
     */
    TERMINATING_STATION,

    /**
     * The train does not have a stop here (or) the train is just passing through.
     */
    NORMAL_STATION;

    /**
     * Determines the type of stop as a StopType enum, based on the values of <code>isOriginatingStation</code> and
     * <code>isTerminatingStation</code>.
     *
     * @param isOriginatingStation     if the train's journey begins here.
     * @param isTerminatingStation     if the train's journey ends here.
     * @return                         one of the values of <code>StopType</code>.
     * @throws GameNotStartedException if a station is both originating and terminating. This is an invalid scenario.
     */
    public static StopType valueOf(boolean isOriginatingStation, boolean isTerminatingStation) throws GameNotStartedException {
        if (isOriginatingStation && isTerminatingStation)
            throw new GameNotStartedException("A station cannot be both originating & terminating for a train");
        if (!isOriginatingStation && !isTerminatingStation) return NORMAL_STATION;
        else if (isOriginatingStation) return ORIGINATING_STATION;
        else return TERMINATING_STATION;
    }

}
