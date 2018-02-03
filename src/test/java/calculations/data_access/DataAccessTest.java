package calculations.data_access;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import calculations.data_access.DataAccess;

public class DataAccessTest {

	@Test
	public void shouldCreateMissingUsersFile() {
		DataAccess dataAccess = DataAccess.getInstance();
		String filePath = String.join(java.io.File.separator, "src", "test", "resources", "Users.xml");

		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		assertFalse(file.exists());

		dataAccess.createMissingFile(filePath);
		assertTrue(file.exists());
	}

}
