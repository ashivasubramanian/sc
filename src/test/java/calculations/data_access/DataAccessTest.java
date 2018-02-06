package calculations.data_access;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DataAccessTest {

    @Test
    public void shouldCreateMissingUsersFile() {
	String filePath = String.join(java.io.File.separator, "src", "test", "resources", "Users.xml");
	File file = new File(filePath);
	if (file.exists()) {
	    file.delete();
	}
	assertFalse(file.exists());

	DataAccess dataAccess = DataAccess.getInstance();
	dataAccess.createMissingUsersFile(filePath);
	assertTrue(file.exists());
    }

    @Test
    public void shouldEnsureThatStandardFolderIsCreatedIfNotExisting() {
	String standardDirectoryPath = String.join(java.io.File.separator, "src", "test", "resources");
	String usersXMLPath = String.join(java.io.File.separator, "src", "test", "resources", "Users.xml");
	File standardDirectoryFile = new File(standardDirectoryPath);
	File usersXMLFile = new File(usersXMLPath);

	if (standardDirectoryFile.exists()) {
	    if (usersXMLFile.exists()) {
		usersXMLFile.delete();
	    }
	    standardDirectoryFile.delete();
	}

	DataAccess dataAccess = DataAccess.getInstance();
	dataAccess.createMissingUsersFile(usersXMLPath);
	assertTrue(standardDirectoryFile.exists());
    }

    @Test
    public void shouldEnsureThatNewlyCreatedUsersXMLHasEmptyContent() {
	String filePath = String.join(java.io.File.separator, "src", "test", "resources", "Users.xml");
	File file = new File(filePath);
	if (file.exists()) {
	    file.delete();
	}

	DataAccess dataAccess = DataAccess.getInstance();
	dataAccess.createMissingUsersFile(filePath);

	try {
	    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
	    assertEquals(1, document.getElementsByTagName("users").getLength());
	    assertEquals(0, document.getElementsByTagName("user").getLength());
	} catch (ParserConfigurationException | SAXException | IOException e) {
	    e.printStackTrace();
	    fail();
	}
    }

}
