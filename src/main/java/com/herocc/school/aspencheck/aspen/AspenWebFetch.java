package com.herocc.school.aspencheck.aspen;

import com.herocc.school.aspencheck.AspenCheck;
import com.herocc.school.aspencheck.Configs;
import com.herocc.school.aspencheck.GenericWebFetch;
import com.herocc.school.aspencheck.InvalidCredentialsException;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AspenWebFetch extends GenericWebFetch {
  public AspenWebFetch(String username, String password) {
    this.login(username, password);
  }
  
  public Connection.Response getClassListPage() {
    try {
      return getPage(Configs.aspenBaseUrl + "/portalClassList.do?navkey=academics.classes.list&maximized=true");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public Connection.Response getSchedulePage() {
    try {
      return getPage(Configs.aspenBaseUrl + "/studentScheduleContextList.do?navkey=myInfo.sch.list");
    } catch (HttpStatusException e) {
      if (e.getStatusCode() == 404 || e.getStatusCode() == 500) {
        AspenCheck.log.warning("This login doesn't have a schedule page!");
      } else {
        AspenCheck.log.warning("Login details incorrect, or Aspen is having issues, please try again later!");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public Connection.Response login(String username, String password) {
    try {
      String loginUrl = Configs.aspenBaseUrl + "/logon.do";
      Connection.Response loginPageResponse =
              Jsoup.connect(loginUrl)
                      .userAgent(Configs.WEB_USER_AGENT)
                      .timeout(10 * 1000)
                      .followRedirects(true)
                      .execute();
      
      AspenCheck.log.finer("Fetched login page");
      
      Map<String, String> mapLoginPageCookies = loginPageResponse.cookies();
      Map<String, String> mapParams = new HashMap<>();
      mapParams.put("deploymentId", "ma-melrose");
      mapParams.put("userEvent", "930");
      mapParams.put("username", username);
      mapParams.put("password", password);
      mapParams.put("mobile", "false");
      
      if (username == null || password == null) {
        throw new InvalidCredentialsException("Unspecified Username or Password");
      }
      
      Connection.Response responsePostLogin = Jsoup.connect(loginUrl)
              .referrer(loginUrl)
              .userAgent(Configs.WEB_USER_AGENT)
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
