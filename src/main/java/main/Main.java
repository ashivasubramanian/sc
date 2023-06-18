package main;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import presentation.windows.LoginInterface;

/**
 * The Main class is the starting point of the application.
 */
public class Main {
    /**
     * The main method for the game.
     *
     * @param args
     *            An array of <code>String</code>s that can be passed as arguments.
     *            However, nothing is done with these arguments.
     */
    public static void main(String args[]) {
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {

	}
	try {
	    LoginInterface objLogin = new LoginInterface();
	    SwingUtilities.invokeAndWait(objLogin);
	} catch (InvocationTargetException | InterruptedException e) {
	    e.printStackTrace();
	}
    }
}
