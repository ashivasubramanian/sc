package common.models;

/**
 * An enum that contains the possible states, aka aspects, of a signal.
 */
public enum SignalAspect {
    
    /**
     * A constant for the STOP aspect indicated by the red colour of the signal.
     */
    STOP,
    /**
     * A constant for the CAUTION aspect indicated by the amber colour of the signal.
     */
    CAUTION,
    /**
     * A constant for the PROCEED aspect indicated by the green colour of the signal.
     */
    PROCEED;
}
