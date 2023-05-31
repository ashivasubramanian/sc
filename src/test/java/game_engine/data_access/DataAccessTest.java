package game_engine.data_access;

import game_engine.data_access.DataAccess;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DataAccessTest {

    @TempDir
    public File temporaryFolder;

    @Test
    public void shouldCreateMissingUsersFile() {
	String filePath = String.join(java.io.File.separator, temporaryFolder.getAbsolutePath(), "Users.xml");
	File file = new File(filePath);

	assertFalse(file.exists());
	DataAccess dataAccess = DataAccess.getInstance();
	dataAccess.createMissingUsersFile(filePath);
	assertTrue(file.exists());
    }

    @Test
    public void shouldEnsureThatStandardFolderIsCreatedIfNotExisting() {
	String standardDirectoryPath = String.join(java.io.File.separator, temporaryFolder.getAbsolutePath(), ".section_controller");
	String usersXMLPath = String.join(java.io.File.separator, temporaryFolder.getAbsolutePath(), ".section_controller", "Users.xml");
	File standardDirectoryFile = new File(standardDirectoryPath);

	assertFalse(standardDirectoryFile.exists());
	DataAccess dataAccess = DataAccess.getInstance();
	dataAccess.createMissingUsersFile(usersXMLPath);
	assertTrue(standardDirectoryFile.exists());
    }

    @Test
    public void shouldEnsureThatNewlyCreatedUsersXMLHasEmptyContent() {
	String filePath = String.join(java.io.File.separator, temporaryFolder.getAbsolutePath(), "Users.xml");
	File file = new File(filePath);

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
