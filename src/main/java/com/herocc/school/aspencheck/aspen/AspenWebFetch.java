package com.herocc.school.aspencheck.aspen;

import com.herocc.school.aspencheck.*;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AspenWebFetch extends GenericWebFetch {
  private String aspenBaseUrl;
  
  public AspenWebFetch(District d, String username, String password) {
    this.aspenBaseUrl = d.aspenBaseUrl;
    this.login(username, password);
  }
  
  public AspenWebFetch(String dName, String username, String password) {
    this.aspenBaseUrl = "https://" + dName + ".myfollett.com/aspen";
    this.login(username, password);
  }
  
  public Connection.Response getClassListPage() {
    try {
      return getPage(aspenBaseUrl + "/portalClassList.do?navkey=academics.classes.list&maximized=true");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public Connection.Response getSchedulePage() {
    try {
      return getPage(aspenBaseUrl + "/studentScheduleContextList.do?navkey=myInfo.sch.list");
    } catch (HttpStatusException e) {
      if (e.getStatusCode() == 404 || e.getStatusCode() == 500) {
        AspenCheck.log.warning("This login doesn't have a schedule page!");
      } else {
        AspenCheck.log.warning("Login details incorrect, or Aspen is having issues, please try again later!");
      }
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public Connection.Response login(String username, String password) {
    try {
      String loginUrl = aspenBaseUrl + "/logon.do";
      Connection.Response loginPageResponse = Jsoup.connect(loginUrl)
                      .userAgent(AspenCheck.config.webUserAgent)
                      .timeout(10 * 1000)
                      .followRedirects(true)
                      .execute();
      
      if (loginPageResponse.statusCode() == 404) {
        AspenCheck.log.warning("No login page found at " + aspenBaseUrl);
        return null;
      }
      
      Map<String, String> mapLoginPageCookies = loginPageResponse.cookies();
      Map<String, String> mapParams = new HashMap<>();
      mapParams.put("deploymentId", "ma-melrose");
      mapParams.put("userEvent", "930");
      mapParams.put("username", username);
      mapParams.put("password", password);
      mapParams.put("mobile", "false");
      
      if (username == null || password == null) {
        AspenCheck.log.warning("Invalid Username or Password!");
        AspenCheck.rollbar.warning(new InvalidCredentialsException("Tried to login to aspen without username / password!"));
        return null;
      }
      
      Connection.Response responsePostLogin = Jsoup.connect(loginUrl)
              .referrer(loginUrl)
              .userAgent(AspenCheck.config.webUserAgent)
              .timeout(10 * 1000)
              .data(mapParams)
              .cookies(mapLoginPageCookies)
              .followRedirects(true)
              .execute();
      
      Map<String, String> mapLoggedInCookies = responsePostLogin.cookies();
      demCookies.putAll(mapLoggedInCookies);
      demCookies.putAll(mapLoginPageCookies);
  
      if (responsePostLogin.statusCode() == 500) throw new InvalidCredentialsException("Username or Pass incorrect");
      
      return responsePostLogin;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
