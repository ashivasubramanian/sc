package presentation.windows;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import common.models.SignalAspect;
import game_engine.Game;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import rendering.StationAspectsWorker;
import rendering.TimeWorker;
import rendering.TrainPositionsWorker;

/**
 * The <code>GameScreen</code> class renders/updates the game screen and all the
 * objects in it. The game screen is the window that opens up after the user has
 * logged in. It is the screen that displays the user name, his score as well as
 * all the trains and stations on the section.
 */
public class GameScreen extends JFrame implements Runnable {

    /**
     * The <code>Font</code> object used to render normal text.
     */
    Font objNormalFont;

    /**
     * The <code>Font</code> object used to render bold text.
     */
    Font objBoldedFont;

    /**
     * The username to be displayed on the screen.
     */
    String userName;

    /**
     * The score to be displayed on the screen.
     */
    String score;

    /**
     * The time to be displayed on the screen.
     */
    private AtomicReference<String> time = new AtomicReference<>("");

    /**
     * Stores the current graphics context.
     *
     * <p>
     * NOTE: The code uses the <code>objGraphics</code> private instance
     * variable to store the current graphics context. This can be readily
     * obtained as and when required using <code>getGraphics()</code>. But for
     * some reason, when <code>getGraphics()</code> was used, the font was not
     * set.
     */
    Graphics objGraphics;

    /**
     * Contains a list of names of the stations on the section.
     */
    Vector<String> objStationNames;

    /**
     * Contains a list of <code>Point</code> objects that represent the
     * on-screen positions of the stations.
     */
    Vector<Point> objStationPositions;

    /**
     * Contains a list of values that represent the real-life positions of the
     * trains. These are then converted to on-screen positions by
     * <code>drawTrains()</code>.
     */
    private AtomicReference<Vector<Float>> objTrainPositions;

    /**
     * Contains a list of aspect values for all the signals on the section.
     */
    private AtomicReference<Vector<SignalAspect[]>> objAspects;

    JTabbedPane objTabPane;

    private Game game;
    private GameInfoPanel gameInfoPanel;

    public GameScreen(String username, Game game, String score) {
        this.userName = username;
        this.game = game;
        this.objStationNames = game.getStations().stream()
                .map(station -> station.getName())
                .collect(Vector::new, Vector::add, Vector::addAll);
        this.objStationPositions = game.getStations().stream()
                .map(station -> new Point((station.getDistanceFromHome() * 790) / 86, 200))
                .collect(Vector::new, Vector::add, Vector::addAll);
        this.score = score;
        SignalAspect[] defaultAspects = new SignalAspect[]{SignalAspect.STOP, SignalAspect.STOP};
        this.objAspects = new AtomicReference<>(game.getStations().stream()
                .map(station -> defaultAspects)
                .collect(Vector::new, Vector::add, Vector::addAll));
        Vector<Float> defaultTrainPositions = game.getTrains().stream()
                .map(train -> 0.0f)
                .collect(Vector::new, Vector::add, Vector::addAll);
        this.objTrainPositions = new AtomicReference<>(defaultTrainPositions);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        gameInfoPanel = new GameInfoPanel(screenSize, userName, score, game);
        new TimeWorker(gameInfoPanel, screenSize).execute();
        new TrainPositionsWorker(gameInfoPanel, game, screenSize).execute();
        new StationAspectsWorker(gameInfoPanel, game, screenSize).execute();
    }

    /**
     * Initializes the required fonts and opens up the game screen.
     *
     * <p>
     * NOTE: The <code>objGraphics</code> private instance variable is set using <code>
     * getGraphics()</code>. For some reason, the variable would be populated
     * only after setVisible(true). Dunno why this occurs.
     */
    public void run() {
        objNormalFont = new Font("Arial", Font.PLAIN, 12);
        objBoldedFont = new Font("Arial", Font.BOLD, 12);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Section Controller");

        objTabPane = new JTabbedPane();
        StationsTab stationsTab = new StationsTab(this.game);
        objTabPane.addTab("Stations", stationsTab);
        TrainsTab trainsTab = new TrainsTab(this.game);
        objTabPane.addTab("Trains", trainsTab);
        objTabPane.requestFocusInWindow();
        objTabPane.setSize(200, 200);
        objTabPane.setLocation(500, 400);

        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        getContentPane().add(objTabPane);
        getContentPane().add(gameInfoPanel);
        setVisible(true);
    }

    /**
     * Populates this instance with the real-life positions of all the stations
     * on the section.
     *
     * @param objPos A <code>Vector</code> that contains the station positions.
     */
    public void setStationPositions(Vector<Point> objPos) {
        objStationPositions = objPos;
    }

    /**
     * Populates this instance with the names of all the stations on the
     * section.
     *
     * @param objNames A <code>Vector</code> that contains the station names.
     */
    public void setStationNames(Vector<String> objNames) {
        objStationNames = objNames;
    }

    /**
     * Sets the user name to be drawn on the screen.
     *
     * @param uname The username to be drawn.
     */
    public void setUserName(String uname) {
        userName = uname;
    }

    /**
     * Sets the score to be drawn on the screen.
     *
     * @param score The score to be drawn.
     */
    public void setScore(int score) {
        this.score = Integer.toString(score);
    }
}
