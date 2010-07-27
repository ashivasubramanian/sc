package presentation.panes;

import java.util.Calendar;

/**
 * The <code>InfoPanel</code> class contains methods that draw/update the Info Panel.
 * The Info Panel is the panel on the game screen that displays the user name,
 * score and the current time.<p>
 * Because the
 * InfoPane is a class that contains the data to be displayed on the Info Panel, and
 * because the Info Panel contains the current time, this class contains an inner
 * class, <code>CurrentTime</code>, that subclasses <code>Thread</code>.
 * This thread is initialized by the <code>InfoPane</code> constructor.
 */
public class InfoPane
{
	/**
	 * Stores the user name.
	 */
	String userName;

	/**
	 * Stores the score.
	 */
	int score;

	/**
	 * Stores the current time.
	 */
	String currentTime;

	/**
	 * Constructor that initializes the <code>CurrentTime</code> class.
	 */
	public InfoPane()
	{
		CurrentTime objCurrentTime = new CurrentTime();
		objCurrentTime.start();
	}

	/**
	 * Returns the user name.
	 *
	 * @return the user name.
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Returns the current score of the player.
	 *
	 * @return the current score.
	 */
	public int getScore()
	{
		return score;
	}

	/**
	 * Returns the current time.
	 *
	 * @return the current time.
	 */
	public String getCurrentTime()
	{
		return currentTime;
	}

	/**
	 * Sets the current time.
	 *
	 * @param time the current time
	 */
	public void setCurrentTime(String time)
	{
		currentTime = time;
	}

	/**
	 * Sets the user name.
	 *
	 * @param user the user name
	 */
	public void setUserName(String user)
	{
		userName = user;
	}

	/**
	 * Sets the current score of the player.
	 *
	 * @param score the current score.
	 */
	public void setScore(int score)
	{
		this.score = score;
	}

	/**
	 * The <code>CurrentTime</code> class sets the current time.
	 * This class extends <code>Thread</code>.
	 */
	class CurrentTime extends Thread
	{
		/**
		 * Obtains and sets the current time.
		 */
		public void run()
		{
			while(true)
			{
				Calendar objCalendar = Calendar.getInstance();
				setCurrentTime( Integer.toString( objCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(objCalendar.get(Calendar.MINUTE)) + ":" + Integer.toString(objCalendar.get(Calendar.SECOND)));
				try
				{
					sleep(1000);
				}
				catch(InterruptedException objInterruptedException)
				{
					System.out.println( "InterruptedException: " + objInterruptedException.getMessage());
				}
			}
		}
	}
}