package com.herocc.school.schedulecheck;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Tests {
	private File credsFile = new File(System.getProperty("user.dir") + "creds.txt");
	
	@Test
	public void checkCredentialsFileIsReadable() throws IOException {
		if (credsFile.exists()){
			assertTrue("Credential File exists and is readable", credsFile.canRead());
		}
	}
	
	@Test
	public void checkCredentialsFileHasCreds() throws IOException {
		if (credsFile.exists()) {
			List<String> lines = Files.readAllLines(credsFile.toPath());
			assertFalse("Lines 1 and 2 contain valid text", (lines.get(0).isEmpty() || lines.get(1).isEmpty()));
		}
	}
}
