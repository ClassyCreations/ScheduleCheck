package com.herocc.school.aspencheck;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

@EnableCaching
@SpringBootApplication
public class AspenCheck {
	public static final Logger log = Logger.getLogger(AspenCheck.class.getName());
	public static final TimeZone timezone = TimeZone.getTimeZone("America/New_York");
	
	public static String username; //"taaspenstudent";
	public static String password; //"teacher2013";
	
	public static void main(String[] args) {
		TimeZone.setDefault(timezone);
    getLoginDetails();
		SpringApplication.run(AspenCheck.class, args);
	}
	
	/**
	 * Gets the login Username and Password
	 * Priority: Params, Env, File, Testing
	 * @throws IOException Couldn't read file
	 */
	private static void getLoginDetails() {
		if (username == null || password == null) {
			if (System.getenv("ASPEN_UNAME") != null && System.getenv("ASPEN_PASS") != null) {
				username = System.getenv("ASPEN_UNAME");
				password = System.getenv("ASPEN_PASS");
			} else {
				// File Check
				File credsFile = new File(System.getProperty("user.dir") + "creds.txt");
				if (credsFile.canRead()) {
					log.fine("Using credentials file: " + credsFile.getPath());
					try {
            List<String> lines = Files.readAllLines(credsFile.toPath());
            if (lines.get(0) != null && username == null) {
              username = lines.get(0);
            }
            if (lines.get(1) != null && password == null) {
              password = lines.get(1);
            }
          } catch (IOException e) {
					  AspenCheck.log.warning("Unable to parse creds.txt file, may not be able to cope!");
					  e.printStackTrace();
          }
				}
			}
		}
	}
  
  public static Calendar getICal(String url) throws IOException {
    URLConnection c = new URL(url).openConnection();
    c.setRequestProperty("User-Agent", Configs.WEB_USER_AGENT);
    
    try (InputStream is = c.getInputStream()) {
      return new CalendarBuilder().build(is);
    } catch (ParserException e) {
			AspenCheck.log.warning("Unable to parse iCal from " + url);
      e.printStackTrace();
    }
    return null;
  }
}
