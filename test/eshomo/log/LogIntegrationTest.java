package eshomo.log;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.util.Random;

import models.LogApi;
import models.LogLogin;
import models.User;
import models.UserType;

import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.filter.Filter;

import static org.fluentlenium.core.filter.FilterConstructor.*;

import org.fluentlenium.core.filter.matcher.MatcherType;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;


import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import play.libs.F.Callback;
import play.mvc.With;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestBrowser;
import controllers.routes;
import eshomo.EshomoTest;

public class LogIntegrationTest extends EshomoTest {

    private final static String USER_NAME = "jabba@thehutt.com";
    private final static String PASSWORD = "ihatesolo";

    @Test
    public void checkGetApiLogs_WithoutLogin() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/" + routes.Log.getApiLogs("", 0));

                assertThat(browser.pageSource()).contains("Zugriff verweigert!");
                assertThat(browser.pageSource())
                        .contains(
                                "Sie verfügen nicht über die nötige Berechtigung um diese Seite anzuzeigen.");

            }
        });
    }

    @Test
    public void checkGetLoginLogs_WithoutLogin() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/" + routes.Log.getLoginLogs("", 0));

                assertThat(browser.pageSource()).contains("Zugriff verweigert!");
                assertThat(browser.pageSource())
                        .contains(
                                "Sie verfügen nicht über die nötige Berechtigung um diese Seite anzuzeigen.");

            }
        });
    }

    @Test
    public void checkGetApiLogs_LoginNoAdmin() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                getNewUser(false, true, USER_NAME, PASSWORD);
                browser.goTo("http://localhost:3333");

                browser.fill("#email").with(USER_NAME);
                browser.fill("#password").with(PASSWORD);
                browser.submit("#signin");

                assertThat(browser.pageSource()).contains("Ausloggen");

                browser.goTo("http://localhost:3333/" + routes.Log.getApiLogs("", 0));
                assertThat(browser.pageSource()).contains("Zugriff verweigert!");
                assertThat(browser.pageSource())
                        .contains(
                                "Sie verfügen nicht über die nötige Berechtigung um diese Seite anzuzeigen.");

            }
        });
    }

    @Test
    public void checkGetLoginLogs_LoginNoAdmin() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                getNewUser(false, true, USER_NAME, PASSWORD);
                browser.goTo("http://localhost:3333");

                browser.fill("#email").with(USER_NAME);
                browser.fill("#password").with(PASSWORD);
                browser.submit("#signin");

                assertThat(browser.pageSource()).contains("Ausloggen");

                browser.goTo("http://localhost:3333/" + routes.Log.getLoginLogs("", 0));
                assertThat(browser.pageSource()).contains("Zugriff verweigert!");
                assertThat(browser.pageSource())
                        .contains(
                                "Sie verfügen nicht über die nötige Berechtigung um diese Seite anzuzeigen.");

            }
        });
    }
    
    @Test
    public void checkGetApiLogs() {
        FakeApplication fakeApp = Helpers.fakeApplication();

        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                TestBrowser browser = getBrowser();        
                getNewUser(true, true, USER_NAME, PASSWORD);
                createLogEntries(LogApi.class);
                browser.goTo("http://localhost:3333");

                browser.fill("#email").with(USER_NAME);
                browser.fill("#password").with(PASSWORD);
                browser.submit("#signin");
                //browser.quit();
                assertThat(browser.pageSource()).contains("Ausloggen");

                browser.goTo("http://localhost:3333/" + routes.Log.getApiLogs("", 0));
                //System.out.println(browser.pageSource());
                FluentList<FluentWebElement> list = browser.find(".paging", (Filter[])null);
                assertThat(list.size()).isEqualTo(1);
               // FluentWebElement element = browser.findFirst("form", with("action").contains("/log/login"));
                //assertThat(element.getText()).contains("Logs durchsuchen");
                //System.out.println(element.getText());
                
             
     
            }
        });

        
    }

    @Test
    public void checkGetLoginLogs() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                getNewUser(true, true, USER_NAME, PASSWORD);
                createLogEntries(LogLogin.class);
                browser.goTo("http://localhost:3333");

                browser.fill("#email").with(USER_NAME);
                browser.fill("#password").with(PASSWORD);
                browser.submit("#signin");

                assertThat(browser.pageSource()).contains("Ausloggen");

                browser.goTo("http://localhost:3333/" + routes.Log.getLoginLogs("", 0));
                assertThat(browser.pageSource()).contains("Zugriff verweigert!");
                assertThat(browser.pageSource())
                        .contains(
                                "Sie verfügen nicht über die nötige Berechtigung um diese Seite anzuzeigen.");

            }
        });
    }

    private User getNewUser(boolean isAdmin, boolean isActive, String username, String password) {
        User user = new User();
        UserType type;
        if (isAdmin)
            type = UserType.find.byId("admin");
        else
            type = UserType.find.byId("customer");
        user.setEmail(username);
        user.setIsActive(true);
        user.setPassword(password);
        user.setType(type);
        user.setIsActive(isActive);
        user.save();

        return user;
    }

    private <T> void createLogEntries(Class<T> clazz) {

        if (clazz == LogApi.class) {
            for (int i = 0; i < 120; i++) {
                LogApi log = new LogApi();
                log.setInfo("info" + i);
                log.setParams("params" + i);
                log.setRequestUri("/uri/some/" + i);
                log.setUser(User.find.where().eq("email", USER_NAME).findUnique());
                log.save();

            }
        }
        if (clazz == LogLogin.class) {
            for (int i = 0; i < 120; i++) {
                LogLogin log = new LogLogin();
                log.setInfo("info" + i);
                log.setUser(User.find.where().eq("email", USER_NAME).findUnique());
                log.save();

            }
        }

    }

}
