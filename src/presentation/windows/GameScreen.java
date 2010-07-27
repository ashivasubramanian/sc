package presentation.windows;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * The <code>GameScreen</code> class renders/updates the game screen and
 * all the objects in it. The game screen is the window that opens up after
 * the user has logged in. It is the screen that displays the user name, his score
 * as well as all the trains and stations on the section.
 */
public class GameScreen extends JFrame
{
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
	String time;

	/**
	 * Stores the current graphics context.
	 *
	 * <p>NOTE: The code uses the <code>objGraphics</code> private instance variable to store
	 * the current graphics context. This can be readily obtained as and when required
	 * using <code>getGraphics()</code>. But for some reason, when <code>getGraphics()</code>
	 * was used, the font was not set.
	 */
	Graphics objGraphics;

	/**
	 * Contains a list of names of the stations on the section.
	 */
	Vector<String> objStationNames;

	/**
	 * Contains a list of <code>Point</code> objects that represent the on-screen
	 * positions of the stations.
	 */
	Vector<Point> objStationPositions;

	/**
	 * Contains a list of values that represent the real-life
	 * positions of the trains. These are then converted to on-screen
	 * positions by <code>drawTrains()</code>.
	 */
	Vector<Float> objTrainPositions;

	/**
	 * Contains a list of aspect values for all the signals on the section.
	 */
	Vector<Integer[]> objAspects;
	
	JTabbedPane objTabPane;

	/**
	 * Initializes the required fonts and opens up the game screen.
	 *
	 * <p>NOTE: The <code>objGraphics</code> private instance variable is set using <code>
	 * getGraphics()</code>. For some reason, the variable would be populated only after
	 * setVisible(true). Dunno why this occurs.
	 */
	public GameScreen()
	{
		objNormalFont = new Font("Arial",Font.PLAIN,12);
		objBoldedFont = new Font("Arial", Font.BOLD,12);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setTitle("Section Controller");
		setVisible(true);
		objGraphics = getGraphics();
		
		objTabPane = new JTabbedPane();
		objTabPane.addTab("Stations", new StationsTab());
		objTabPane.addTab("Trains", new TrainsTab());
		objTabPane.setVisible(true);
		objTabPane.requestFocusInWindow();
		objTabPane.setSize(200, 200);
		objTabPane.setLocation(500,400);
		JPanel objPanel = new JPanel();
		objPanel.setSize(1024, 300);
		getContentPane().add(objTabPane);
		getContentPane().add(objPanel);
	}

	/**
	 * Draws the user name on the screen.
	 * The method checks if the user name will collide with the score. If yes,
	 * the user name is drawn a little to the left.
	 *
	 * <p>NOTE:
	 * <ul>
	 * <li>The code uses the <code>objGraphics</code> private instance variable to store
	 * the current graphics context. This can be readily obtained as and when required
	 * using <code>getGraphics()</code>. But for some reason, when <code>getGraphics()
	 * </code> was used, the font was not set.</li>
	 * </ul>
	 * 
	 * @param username The username to be rendered on the screen
	 * in the Information Pane.
	 */
	private void drawUser( String username)
	{
		objGraphics.setFont(objBoldedFont);
		int overlap = 0;
		if( objGraphics.getFontMetrics().stringWidth(username) > 45)
		{
			overlap = objGraphics.getFontMetrics().stringWidth(username) - 45;
		}
		objGraphics.drawString(username, 600 - overlap,40);
	}

	/**
	 * Draws the specified score on the screen.
	 *
	 * @param score The score to draw on the screen.
	 */
	private void drawScore( String score)
	{
		objGraphics.setFont(objNormalFont);
		objGraphics.drawString( score,650,40);
	}

	/**
	 * Draws the specified time on the screen.
	 *
	 * @param time The time to draw on the screen.
	 */
	private void drawTime( String time)
	{
		objGraphics.setFont(objNormalFont);
		objGraphics.clearRect(700,20,50,20);
		objGraphics.drawString(time,700,40);
	}

	/**
	 * Draws the section.
	 * The method draws the section, then draws each station on the section.
	 * For each station, its corresponding aspects are also drawn.
	 */
	private void drawSection()
	{
		objGraphics.drawLine(0,200,800,200);
		objGraphics.drawLine(0,201,800,201);
		Enumeration<String> names = objStationNames.elements();
		Enumeration<Point> positions = objStationPositions.elements();
		Enumeration<Integer[]> aspects = objAspects.elements();
		int ctr = 1;
		int x = 0, y = 0;
		while(names.hasMoreElements())
		{
			Point objPoint = positions.nextElement();
			x = objPoint.x;
			y = objPoint.y;
			drawStation(objPoint.x,objPoint.y,ctr % 2,names.nextElement());
			ctr++;
			drawAspects(x,y, aspects.nextElement());
		}
	}

	/**
	 * Draws a station on the screen.
	 * The method draws the station at the specified position, and draws the
	 * name of the station either below or above the station. Whether to draw
	 * above or below, is determined by <code>stationType</code>.
	 *
	 * @param x The x-coordinate of the screen where the station is to be drawn
	 * @param y The y-coordinate of the screen where the station is to be drawn
	 * @param stationType Determines where the station name is to be drawn. If
	 *                    the value is odd, then the name is drawn above the station.
	 *                    Otherwise, the name is drawn below the station.
	 * @param stationName The name of the station.
	 */
	private void drawStation(int x,int y,int stationType,String stationName)
	{
		objGraphics.setColor(java.awt.Color.BLACK);
		objGraphics.drawLine(x+5,y-10,x+5,y+10);
		//If the station is odd, then the station name is displayed above the station.
		objGraphics.setFont(new Font("Arial", Font.ITALIC,12));
		if(stationType == 0)
			objGraphics.drawString(stationName,x+5,y+25);
		else
			objGraphics.drawString(stationName,x+5,y-18);
	}

	/**
	 * Draws all the trains that are currently running on the section
	 * at their current positions.
	 */
	private void drawTrains()
	{
		Enumeration<Float> objEnumeration = objTrainPositions.elements();
		while(objEnumeration.hasMoreElements())
		{
			float distance = objEnumeration.nextElement();
			int x = new Float((distance * 800) / 86).intValue();
			objGraphics.setColor(java.awt.Color.RED);
			objGraphics.drawLine(x, 195, x, 205);
		}
	}

	/**
	 * Draws the aspects with their current colours for a particular station.
	 * Here, <code>x</code> and <code>y</code> represent the x and y
	 * coordinates of the station, not the signal. Hence, the method does
	 * a certain offset to draw the aspect.
	 *
	 * @param x the x-position at which to draw the aspect
	 * @param y the y-position at which to draw the aspect
	 * @param aspectsForStation An <code>Integer</code> array of aspect
	 * values for the two signals in the station
	 */
	private void drawAspects(int x, int y, Integer[] aspectsForStation)
	{
		for(int i = 0; i < aspectsForStation.length; i++)
		{
			switch(aspectsForStation[i])
			{
				case 1:
					objGraphics.setColor(java.awt.Color.RED);
					break;
				case 2:
					objGraphics.setColor(java.awt.Color.ORANGE);
					break;
				case 3:
					objGraphics.setColor(java.awt.Color.GREEN);
			}
			if( i == 0)	objGraphics.fillArc(x+6, y-7, 4, 5, 0, 360);
			else if ( i == 1)	objGraphics.fillArc(x+6, y+4, 4, 5, 0, 360);
		}
	}

	/**
	 * Populates this instance with the real-life positions of all the
	 * stations on the section.
	 *
	 * @param objPos A <code>Vector</code> that contains the station positions.
	 */
	public void setStationPositions(Vector<Point> objPos)
	{
		objStationPositions = objPos;
	}

	/**
	 * Populates this instance with the names of all the stations on the section.
	 *
	 * @param objNames A <code>Vector</code> that contains the station names.
	 */
	public void setStationNames(Vector<String> objNames)
	{
		objStationNames = objNames;
	}

	/**
	 * Renders the screen.
	 * The method renders/updates the screen by calling the required methods.
	 *
	 * @param g The <code>Graphics</code> object for the screen.
	 */
	public void paint(Graphics g)
	{
		objGraphics.setColor(java.awt.Color.BLACK);
		objGraphics.drawLine(0,50,800,50);
		objGraphics.drawLine(0,51,800,51);
		drawUser(userName);
		drawScore(score);
		drawTime(time);
		drawSection();
		drawTrains();
	}

	/**
	 * Sets the user name to be drawn on the screen.
	 *
	 * @param uname The username to be drawn.
	 */
	public void setUserName(String uname)
	{
		userName = uname;
	}

	/**
	 * Sets the score to be drawn on the screen.
	 *
	 * @param score The score to be drawn.
	 */
	public void setScore( int score)
	{
		this.score = Integer.toString(score);
	}

	/**
	 * Sets the time to be drawn on the screen.
	 *
	 * @param time The time to be drawn.
	 */
	public void setTime(String time)
	{
		this.time = time;
	}

	/**
	 * Sets the specified positions of the trains to this instance.
	 * <code>positions</code> must be a <code>Vector</code> of
	 * real-life positions expressed as <code>Float</code> objects.
	 *
	 * @param positions a <code>Vector</code> of real-life positions
	 */
	public void setTrainPositions(Vector<Float> positions)
	{
		objTrainPositions = positions;
	}

	/**
	 * Sets a <code>Vector</code> of aspects that are to be drawn on the screen.
	 * <p>Note that <code>aspects</code> should be a <code>Vector</code> of
	 * <code>Integer</code>s.
	 *
	 * @param aspects the aspects to be drawn. These aspects are for the entire
	 *                section.
	 */
	public void setAspects(Vector<Integer[]> aspects)
	{
		objAspects = aspects;
	}
}