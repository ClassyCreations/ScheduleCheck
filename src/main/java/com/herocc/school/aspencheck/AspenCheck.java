package com.herocc.school.aspencheck;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;

@EnableCaching
@SpringBootApplication
public class AspenCheck {
	public static final Logger log = Logger.getLogger(AspenCheck.class.getName());
	public static final TimeZone timezone = TimeZone.getTimeZone("America/New_York");
  
  public static Rollbar rollbar;
  public static Configs config;
	
	public static void main(String[] args) throws IOException {
    initRollbar();
    TimeZone.setDefault(timezone);
    handleConfigFile();
    SpringApplication.run(AspenCheck.class, args);
	}
	
	public static String getEnvFromKey(String key) {
    return (key.startsWith("${") && key.endsWith("}")) ? System.getenv(key.replace("${", "").replace("}", "")) : key;
  }
	
	private static void handleConfigFile() throws IOException {
    String result = new BufferedReader(new InputStreamReader(AspenCheck.class.getResourceAsStream("/config.json")))
      .lines().collect(Collectors.joining("\n"));
	  
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
    config = mapper.readValue(result, Configs.class);
  }
  
  private static void initRollbar() {
    String env = System.getenv("devEnvironment");
    if (env == null || env.trim().equals("")) env = "development";
    
    Config rollbarConfig = withAccessToken("53677a64a458455dbb31d2096a4b38ad")
      .codeVersion(Constants.VERSION)
      .environment(env)
      .build();
    rollbar = Rollbar.init(rollbarConfig);
  }
  
  public static long getUnixTime() {
    return System.currentTimeMillis() / 1000;
  }
  
  public static boolean isNullOrEmpty(Object o) {
	  return (o == null || o.equals(""));
  }
  
  public static String textToCammelCase(String og, boolean lowercaseFirstLetter) {
	  // Tweaked from https://stackoverflow.com/a/34230748/1709894. There is probably a better way to do this
    StringBuilder sb = new StringBuilder(og);
  
    for (int i = 0; i < sb.length(); i++) {
      if (sb.charAt(i) == ' ') {
        sb.deleteCharAt(i);
        sb.replace(i, i+1, String.valueOf(Character.toUpperCase(sb.charAt(i))));
      }
    }
    if (lowercaseFirstLetter) sb.replace(0, 1, String.valueOf(Character.toLowerCase(sb.charAt(0))));
    return sb.toString();
  }
}
