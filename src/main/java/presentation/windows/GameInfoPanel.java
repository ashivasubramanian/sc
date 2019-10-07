/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.windows;

import common.models.SignalAspect;
import game_engine.Game;
import game_engine.dto.StationDto;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Enumeration;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JPanel;

/**
 *
 * @author shivasubramanian
 */
public class GameInfoPanel extends JPanel {
    
    private Graphics objGraphics;
    private String userName;
    private String score;
    private AtomicReference<String> time = new AtomicReference<>("");
        /**
     * The <code>Font</code> object used to render normal text.
     */
    Font objNormalFont;

    /**
     * The <code>Font</code> object used to render bold text.
     */
    Font objBoldedFont;
    
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
    private Game game;
    
    private Dimension screenSize;

    public GameInfoPanel(Dimension screenSize, String username, String score, Game game) {
        this.userName = username;
        this.score = score;
        this.game = game;
        this.screenSize = screenSize;
        objGraphics = getGraphics();
        objNormalFont = new Font("Arial", Font.PLAIN, 12);
        objBoldedFont = new Font("Arial", Font.BOLD, 12);
        SignalAspect[] defaultAspects = new SignalAspect[]{SignalAspect.STOP, SignalAspect.STOP};
        this.objStationNames = this.game.getStations().stream()
                .map(station -> station.getName())
                .collect(Vector::new, Vector::add, Vector::addAll);
        int twentyFifthPercentOfHeight = screenSize.height * 25 / 100;
        int maxDistanceOfSection = this.game.getStations().stream()
                .mapToInt(station -> station.getDistanceFromHome()).max().getAsInt();
        this.objStationPositions = this.game.getStations().stream()
                .map(station -> new Point((station.getDistanceFromHome() * (screenSize.width - 25)) / maxDistanceOfSection, twentyFifthPercentOfHeight))
                .collect(Vector::new, Vector::add, Vector::addAll);
        this.objAspects = new AtomicReference<>(this.game.getStations().stream()
                .map(station -> defaultAspects)
                .collect(Vector::new, Vector::add, Vector::addAll));
        Vector<Float> defaultTrainPositions = this.game.getTrains().stream()
                .map(train -> 0.0f)
                .collect(Vector::new, Vector::add, Vector::addAll);
        this.objTrainPositions = new AtomicReference<>(defaultTrainPositions);
    }
    
        /**
     * Renders the screen. The method renders/updates the screen by calling the
     * required methods.
     *
     * @param g The <code>Graphics</code> object for the screen.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        objGraphics = g;
        objGraphics.setColor(java.awt.Color.BLACK);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int tenPercentOfHeight = screenSize.height * 10/100;
        objGraphics.drawLine(0, tenPercentOfHeight, screenSize.width, tenPercentOfHeight);
        objGraphics.drawLine(0, tenPercentOfHeight + 1, screenSize.width, tenPercentOfHeight + 1);
        drawUser(userName);
        drawScore(score);
        drawTime(time.get());
        drawSection();
        drawTrains();
    }
    
        /**
     * Draws the user name on the screen. The method checks if the user name
     * will collide with the score. If yes, the user name is drawn a little to
     * the left.
     *
     * <p>
     * NOTE:
     * <ul>
     * <li>The code uses the <code>objGraphics</code> private instance variable
     * to store the current graphics context. This can be readily obtained as
     * and when required using <code>getGraphics()</code>. But for some reason,
     * when <code>getGraphics()
     * </code> was used, the font was not set.</li>
     * </ul>
     *
     * @param username The username to be rendered on the screen in the
     * Information Pane.
     */
    private void drawUser(String username) {
        objGraphics.setFont(objBoldedFont);
        int overlap = 0;
        if (objGraphics.getFontMetrics().stringWidth(username) > 45) {
            overlap = objGraphics.getFontMetrics().stringWidth(username) - 45;
        }
        objGraphics.drawString(username, (this.screenSize.width * 70 /100) - overlap,
                this.screenSize.height * 5 / 100);
    }

    /**
     * Draws the specified score on the screen.
     *
     * @param score The score to draw on the screen.
     */
    private void drawScore(String score) {
        objGraphics.setFont(objNormalFont);
        objGraphics.drawString(score, this.screenSize.width * 80 / 100, this.screenSize.height * 5 /100);
    }

    /**
     * Draws the specified time on the screen.
     *
     * @param time The time to draw on the screen.
     */
    private void drawTime(String time) {
        objGraphics.setFont(objNormalFont);
        objGraphics.drawString(time, this.screenSize.width * 90 / 100, this.screenSize.height * 5 / 100);
    }

    /**
     * Draws the section. The method draws the section, then draws each station
     * on the section. For each station, its corresponding aspects are also
     * drawn.
     */
    private void drawSection() {
        int twentyFifthPercentOfHeight = this.screenSize.height * 25 / 100;
        objGraphics.drawLine(0, twentyFifthPercentOfHeight, this.screenSize.width, twentyFifthPercentOfHeight);
        objGraphics.drawLine(0, twentyFifthPercentOfHeight + 1, this.screenSize.width, twentyFifthPercentOfHeight + 1);
        Enumeration<String> names = objStationNames.elements();
        Enumeration<Point> positions = objStationPositions.elements();
        Enumeration<SignalAspect[]> aspects = objAspects.get().elements();
        int ctr = 1;
        int x = 0, y = 0;
        while (names.hasMoreElements()) {
            Point objPoint = positions.nextElement();
            x = objPoint.x;
            y = objPoint.y;
            drawStation(objPoint.x, objPoint.y, ctr % 2, names.nextElement());
            ctr++;
            drawAspects(x, y, aspects.nextElement());
        }
    }

    /**
     * Draws a station on the screen. The method draws the station at the
     * specified position, and draws the name of the station either below or
     * above the station. Whether to draw above or below, is determined by
     * <code>stationType</code>.
     *
     * @param x The x-coordinate of the screen where the station is to be drawn
     * @param y The y-coordinate of the screen where the station is to be drawn
     * @param stationType Determines where the station name is to be drawn. If
     * the value is odd, then the name is drawn above the station. Otherwise,
     * the name is drawn below the station.
     * @param stationName The name of the station.
     */
    private void drawStation(int x, int y, int stationType, String stationName) {
        objGraphics.setColor(java.awt.Color.BLACK);
        objGraphics.drawLine(x + 5, y - 10, x + 5, y + 10);
        //If the station is odd, then the station name is displayed above the station.
        objGraphics.setFont(new Font("Arial", Font.ITALIC, 12));
        if (stationType == 0) {
            objGraphics.drawString(stationName, x + 5, y + 25);
        } else {
            objGraphics.drawString(stationName, x + 5, y - 18);
        }
    }

    /**
     * Draws all the trains that are currently running on the section at their
     * current positions.
     */
    private void drawTrains() {
        int twentyFifthPercentOfHeight = this.screenSize.height * 25 / 100;
        Enumeration<Float> objEnumeration = objTrainPositions.get().elements();
        while (objEnumeration.hasMoreElements()) {
            float distance = objEnumeration.nextElement();
            int x = new Float((distance * this.screenSize.width) / 86).intValue();
            objGraphics.setColor(java.awt.Color.RED);
            objGraphics.drawLine(x, twentyFifthPercentOfHeight - 5, x, twentyFifthPercentOfHeight + 5);
        }
    }

    /**
     * Draws the aspects of the signals for a particular station. Here,
     * <code>x</code> and <code>y</code> represent the x and y coordinates of
     * the station, not the signal. Hence, the method does a certain offset to
     * draw the aspect.
     *
     * @param x the x-position of the station
     * @param y the y-position of the station
     * @param aspectsForStation A <code>SignalAspect</code> array of aspect
     * values for the two signals in the station
     */
    private void drawAspects(int x, int y, SignalAspect[] aspectsForStation) {
        for (int i = 0; i < aspectsForStation.length; i++) {
            objGraphics.setColor(aspectsForStation[i].getColour());
            if (i == 0) {
                objGraphics.fillArc(x + 6, y - 7, 4, 5, 0, 360);
            } else if (i == 1) {
                objGraphics.fillArc(x + 6, y + 4, 4, 5, 0, 360);
            }
        }
    }

    /**
     * Sets the time to be drawn on the screen.
     *
     * @param time The time to be drawn.
     */
    public void setTime(String time) {
        this.time.set(time);
    }

    /**
     * Sets the specified positions of the trains to this instance.
     * <code>positions</code> must be a <code>Vector</code> of real-life
     * positions expressed as <code>Float</code> objects.
     *
     * @param positions a <code>Vector</code> of real-life positions
     */
    public void setTrainPositions(Vector<Float> positions) {
        objTrainPositions.set(positions);
    }

    /**
     * Sets a <code>Vector</code> of aspects that are to be drawn on the screen.
     *
     * @param aspects the aspects to be drawn. These aspects are for the entire
     * section.
     */
    public void setAspects(Vector<SignalAspect[]> aspects) {
        objAspects.set(aspects);
    }
}
