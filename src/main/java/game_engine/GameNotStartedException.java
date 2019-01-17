package game_engine;

/**
 * Exception class that is used to indicate problems with starting the game.
 * Use the <code>Exception.getCause()</code> method to understand the underlying
 * problem that caused this exception to be thrown.
 */
public class GameNotStartedException extends Exception {

    public GameNotStartedException(Throwable ex) {
        super("Unable to start game", ex);
    }
}
