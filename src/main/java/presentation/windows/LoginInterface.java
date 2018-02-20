package presentation.windows;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.w3c.dom.Element;

import calculations.data_access.DataAccess;
import presentation.panes.InfoPane;
import rendering.Renderer;

/**
 * The <code>LoginInterface</code> class displays the login interface to the user.
 * <p>TO DO: Can't we extends JFrame here?
 */
public class LoginInterface
{
	/**
	 * The <code>JFrame</code> object that is the Login interface.
	 */
	JFrame objFrame;

	/**
	 * The <code>JPanel</code> to which controls are added.
	 */
	JPanel objPanel;

	/**
	 * The <code>JList</code> which displays the list of user names.
	 */
	JList objList;

	/**
	 * The <code>JButton</code> control which displays the 'Login' caption.
	 * On click of this button, the username selected is validated. If a username is
	 * entered into the "Or sign up here:" textbox, then that username is stored.
	 */
	JButton objLogin;

	/**
	 * The <code>JLabel</code> that displays the "Or sign up here." text.
	 */
	JLabel objSpecifyUserName;

	/**
	 * The <code>JLabel</code> that displays the "Select username:" text.
	 */
	JLabel objSelectUserName;

	/**
	 * The <code>JTextField</code> in which a user can sign up by specifying a username.
	 */
	JTextField objNewLogin;

	/**
	 * The <code>GridBagLayout</code> using which we can arrange the controls on the
	 * <code>JFrame</code>.
	 */
	GridBagLayout objLayout;

	/**
	 * The <code>GridBagConstraints</code> using which we can arrange the controls on the
	 * <code>JFrame</code>.
	 */
	GridBagConstraints objConstraints;

	/**
	 * The <code>InfoPane</code> instance into which the username selected and the user's
	 * score are set.
	 */
	InfoPane objInfoPane;

	/**
	 * A <code>Vector</code> that contains the user objects.
	 */
	Vector<Element> objUsers;
	
	/**
	 * Initializes the components of the login interface and displays them to the user.
	 */
	public void drawInterface()
	{
		objLayout = new GridBagLayout();
		objConstraints = new GridBagConstraints();

		objSelectUserName = new JLabel("Select username:");
		objSelectUserName.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_S);
		objConstraints.gridx = 0;
		objConstraints.gridy = 0;
		objConstraints.weightx = 1.0;
		objConstraints.weighty = 1.0;
		objLayout.setConstraints(objSelectUserName,objConstraints);

		objList = new JList();
		objList.setVisibleRowCount(5);
		objList.setSelectedIndex(0);
		objList.setSelectionMode( javax.swing.ListSelectionModel.SINGLE_SELECTION);
		objList.setPrototypeCellValue("abcdefghijklmno");
		JScrollPane objScrollPane = new JScrollPane(objList);
		objConstraints.gridx = 1;
		objConstraints.gridy = 0;
		objLayout.setConstraints(objScrollPane,objConstraints);

		objSpecifyUserName = new JLabel("Or sign up here.");
		objSpecifyUserName.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_O);
		objConstraints.gridx = 0;
		objConstraints.gridy = 1;
		objLayout.setConstraints(objSpecifyUserName,objConstraints);

		objNewLogin = new JTextField(10);
		objSpecifyUserName.setLabelFor(objNewLogin);
		objConstraints.gridx = 1;
		objConstraints.gridy = 1;
		objLayout.setConstraints(objNewLogin,objConstraints);
		
		objLogin = new JButton("Login");
		objLogin.setFont( new Font("Arial",Font.PLAIN,12));
		objLogin.setMnemonic(java.awt.event.KeyEvent.VK_L);
		objConstraints.gridx = 1;
		objConstraints.gridy = 2;
		objLayout.setConstraints(objLogin,objConstraints);
		objLogin.addActionListener( new LoginEvents());

		objPanel = new JPanel();
		objPanel.setLayout( objLayout);
		objPanel.add(objSelectUserName);
		objPanel.add(objScrollPane);
		objPanel.add(objLogin);
		objPanel.add(objSpecifyUserName);
		objPanel.add( objNewLogin);

		objFrame = new JFrame( "Section Controller - Login");
		objFrame.getContentPane().add( objPanel);
		objFrame.getRootPane().setDefaultButton(objLogin);
		objFrame.setSize( 300, 300);
		objFrame.setLocation( 300, 200);		//To be done later: set the location and size using Dimension objects.
		objFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
		objFrame.addWindowListener(new LoginEvents());
		objFrame.setResizable(false);
		objFrame.setVisible( true);
		
		int verticalResolution = objFrame.getGraphicsConfiguration().getBounds().height;
		int horizResolution = objFrame.getGraphicsConfiguration().getBounds().width;
		objFrame.setTitle( objFrame.getTitle() + ": " + horizResolution + "x" + verticalResolution);
	}

	/**
	 * The <code>LoginEvents</code> class handles the events that are
	 * related to the login interface.
	 */
	class LoginEvents extends WindowAdapter implements ActionListener
	{
		private String USERS_FILE = String.join(java.io.File.separator, System.getProperty("user.home"), ".section_controller", "Users.xml");

		/**
		 * Handles the load event of this form.
		 * This method extracts the list of users from the Users.xml file and sets it to
		 * the list displayed in the Login window.
		 *
		 * @param e The <code>WindowEvent</code> object.
		 */
		public void windowOpened(WindowEvent e) {
			try {
				// The values for the JList are in objUsers. But they are in the form of
				// <code>Element</code> objects. Therefore, the usernames have to be extracted and
				// populated into the list.
				objUsers = DataAccess.getInstance().extractData(USERS_FILE, "user");
				Enumeration<Element> objEnumeration = objUsers.elements();
				Vector<String> objUserNames = new Vector<>();
				while (objEnumeration.hasMoreElements()) {
					Element singleUser = objEnumeration.nextElement();
					objUserNames.add(singleUser.getAttribute("name"));
				}
				objList.setListData(objUserNames);
			} catch (FileNotFoundException fnfe) {
				DataAccess.getInstance().createMissingUsersFile(USERS_FILE);
			} catch (Exception objException) {
				javax.swing.JOptionPane.showMessageDialog(objFrame, objException.getMessage());
				objException.printStackTrace();
			}
		}

		/**
		 * Handles all clicks generated by the login interface.<p>
		 * The method does the following actions:
		 * <ul>
		 *  <li> If the user signs up by entering his username in the "Or sign up here:"
		 * field, then:
		 *   <ul>
		 *    <li>The user name specified is checked for length, since it cannot be
		 * greater than 10 characters. If yes, a message is displayed informing the user
		 * that usernames are limited to 10 characters.</li>
		 *    <li>The user name specified is checked against the list of users in
		 * Users.xml to verify if the name already exists. If yes, a message is displayed
		 * informing the user that the username already exists, and asking the user to
		 * select another.</li>
		 *    <li>The user name is added to Users.xml.
		 *   </ul>
		 *  <li>If the user does not sign up, but selects an option from the list of
		 * usernames, then the login screen will close and the user will now see the
		 * game screen.
		 *
		 * @param objActionEvent The event that was generated
		 */
		public void actionPerformed(ActionEvent objActionEvent)
		{
			try
			{
				//No need to check for the source, as is usually done. Only one component(button) exists!!
				String newUserName = objNewLogin.getText();
				if(newUserName.length() > 0)
				{
					if(newUserName.length() > 10)
					{
						javax.swing.JOptionPane.showMessageDialog(objFrame,"User name cannot exceed 10 characters.");
						return;
					}
					Vector<Element> existingUsersWithSameName = DataAccess.getInstance().extractData(USERS_FILE, "user[@name=" + newUserName + "]");
					if(existingUsersWithSameName.contains(newUserName))
					{
						javax.swing.JOptionPane.showMessageDialog(objFrame,"User name specified already exists.");
						return;
					}
					String[] objAttributes = {"name","score"};
					String[] objValues = {newUserName,"100"};
					//If the insert operation to add a new user is successful, proceed to the game screen,
					//otherwise throw an error message and return.
					if( !DataAccess.getInstance().insertData(USERS_FILE,"user",objAttributes, objValues))
					{
						javax.swing.JOptionPane.showMessageDialog(objFrame,"Unable to add user.","Error!!",javax.swing.JOptionPane.ERROR_MESSAGE);
						return;
					}
					objInfoPane = new InfoPane();
					objInfoPane.setUserName(newUserName);
					objInfoPane.setScore(100);
				}
				else
				{
					if(objList.getSelectedValue() == null)
					{
						javax.swing.JOptionPane.showMessageDialog(objFrame,"Please select a username, or sign up.","Error!!",javax.swing.JOptionPane.ERROR_MESSAGE);
						return;
					}
					objInfoPane = new InfoPane();
					objInfoPane.setUserName(objList.getSelectedValue().toString());
					Enumeration<Element> objEnumeration = objUsers.elements();
					while(objEnumeration.hasMoreElements())
					{
						Element singleUser = objEnumeration.nextElement();
						if(singleUser.getAttribute("name").equals(objList.getSelectedValue().toString()))
							objInfoPane.setScore( Integer.parseInt( singleUser.getAttribute("score")));
					}
				}
				Renderer.getInstance().setInfoPane(objInfoPane);
				objInfoPane = null;
				objFrame.setVisible(false);
				Renderer.getInstance().setGameScreen();
				objFrame.dispose();
			}
			catch(Exception e)
			{
				javax.swing.JOptionPane.showMessageDialog(objFrame,e.getMessage());
				e.printStackTrace();
			}
		}
	}
}