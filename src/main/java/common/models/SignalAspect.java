package common.models;

import java.awt.Color;

/**
 * A signal's aspect is what is colloquially referred to as "colour of the signal".
 * This enum describes contains the possible aspects of a signal.
 */
public enum SignalAspect {
    
    /**
     * A constant for the STOP aspect indicated by the red colour of the signal.
     */
    STOP(Color.RED),
    /**
     * A constant for the CAUTION aspect indicated by the amber colour of the signal.
     */
    CAUTION(Color.ORANGE),
    /**
     * A constant for the PROCEED aspect indicated by the green colour of the signal.
     */
    PROCEED(Color.GREEN);
    
    private Color colour;

    SignalAspect(Color colour) {
	this.colour = colour;
    }

    public Color getColour() {
        return colour;
    }
}
