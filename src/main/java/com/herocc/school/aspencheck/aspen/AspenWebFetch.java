package com.herocc.school.aspencheck.aspen;

import com.herocc.school.aspencheck.AspenCheck;
import com.herocc.school.aspencheck.GenericWebFetch;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AspenWebFetch extends GenericWebFetch {
  private String aspenBaseUrl;
  public String username;
  public String districtName;
  
  private Connection.Response courseListPage;
  private Connection.Response schedulePage;
  
  public AspenWebFetch(String dName, String username, String password) {
    this.aspenBaseUrl = "https://" + dName + ".myfollett.com/aspen";
    this.username = username;
    this.districtName = dName;
    this.login(username, password);
  }
  
  public Boolean areCredsCorrect() {
    try {
      return getPage(aspenBaseUrl + "/home.do").statusCode() == 200;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }
  
  public Connection.Response getCourseListPage() {
    if (courseListPage != null) return courseListPage;
    try {
      courseListPage = getPage(aspenBaseUrl + "/portalClassList.do?navkey=academics.classes.list&maximized=true");
      return courseListPage;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public Connection.Response getCourseInfoPage(String courseId) {
    Map<String, String> map = new HashMap<>();
    map.put("selectedStudentOid", courseId);
    try {
      return getPage(aspenBaseUrl + "/portalClassDetail.do?navkey=academics.classes.list.detail&maximized=true", map);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public Connection.Response getCourseAssignmentsPage(String courseId) {
    Map<String, String> map = new HashMap<>();
    map.put("oid", courseId);
    try {
      return getPage(aspenBaseUrl + "/portalAssignmentList.do?navkey=academics.classes.list.gcd&maximized=true", map);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public Connection.Response getSchedulePage() {
    if (schedulePage != null) return schedulePage;
    try {
      schedulePage = getPage(aspenBaseUrl + "/studentScheduleContextList.do?navkey=myInfo.sch.list");
      return schedulePage;
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
  
      if (responsePostLogin.statusCode() == 500) AspenCheck.log.warning("Username or Pass incorrect");
      
      return responsePostLogin;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
