package user;

import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import static org.fest.assertions.Assertions.*;

import java.util.Random;

import org.junit.Test;

import controllers.routes;
import play.libs.WS;
import play.libs.F.Callback;
import play.test.TestBrowser;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.avaje.ebean.Ebean;

import models.Address;
import models.Cart;
import models.CartHasProduct;
import models.CartStatus;
import play.data.DynamicForm;
import play.mvc.Result;
import views.html.user.transaction;
import views.html.user.userdata;

/**
 * testing for user functionality
 * @author boe
 */
public class UserIntegrationTest {
	/**
	 * checks if user data site is returning http status 403 if user is NOT logged in
	 * @author boe
	 */
	@Test
	public void showDataWithoutLoginHttpStatus(final int userid) {
		withoutLoginHttpStatus("http://localhost:3333" + routes.User.showData(userid));
	}
	
	/**
	 * checks if user data site is emptry if user is NOT logged in
	 * @author boe
	 */
	@Test
	public void showDataWithoutLoginContent(final int userid) {
		withoutLoginContent("http://localhost:3333" + routes.User.showData(userid));
	}
	
	/**
	 * checks if a user of type customer can login and see the user data site
	 * @author boe
	 */
	@Test
	public void showDataWithLoginCustomerHttpContent() {
        showDataWithLoginContent("user1@students.ffhs.ch", "ffhs2011", 5);
	}

	/**
	 * checks if a user of type admin can login and see the user data site
	 * @author boe
	 */
	@Test
	public void showDataWithLoginAdminHttpContent() {
		showDataWithLoginContent("patrick.boesch@students.ffhs.ch", "ffhs2011", 1);
	}
	
	/**
	 * the main procedure to check if an logged in user can see the user data site
	 * @param user 
	 * @param password
	 */
	private void showDataWithLoginContent(final String user, final String password, final int userid) {
		running(testServer(3333), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");
                
                browser.fill("#email").with(user);
                browser.fill("#password").with(password);
                browser.submit("#signin");
                
                assertThat(browser.pageSource()).contains("Ausloggen");

                browser.goTo("http://localhost:3333" + routes.User.showData(userid));

                assertThat(browser.pageSource()).contains("Benutzerdaten");
                assertThat(browser.pageSource()).contains("Adresse");
                assertThat(browser.pageSource()).contains("Aktualisieren");
                assertThat(browser.pageSource()).doesNotContain("Login");
            }
        });
	}
	
	/**
	 * checks if a user of type customer can update his data
	 * @author boe
	 */
	@Test
	public void updateDataWithLoginCustomerHttpContent() {
		updateDataWithLoginContent("user1@students.ffhs.ch", "ffhs2011", 5);
	}

	/**
	 * checks if a user of type admin can update his data
	 * @author boe
	 */
	@Test
	public void updateDataWithLoginAdminHttpContent() {
		updateDataWithLoginContent("patrick.boesch@students.ffhs.ch", "ffhs2011", 1);
	}

	/**
	 * the main procedure to check if an logged in user can update the user data
	 * @param user 
	 * @param password
	 */
	private void updateDataWithLoginContent(final String user, final String password, final int userid) {
		running(testServer(3333), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");
                
                browser.fill("#email").with(user);
                browser.fill("#password").with(password);
                browser.submit("#signin");
                
                assertThat(browser.pageSource()).contains("Ausloggen");

                browser.goTo("http://localhost:3333" + routes.User.showData(userid));
                
                String plz = Integer.toString(new Random().nextInt(9000)+1000);
                
                browser.fill("#zip").with(plz);
                browser.fill("#firstname").with(browser.$("#firstname").getValue());
                browser.submit("#update-user-data");

                assertThat(browser.pageSource()).contains("Ihr Daten wurde erfolgreich aktualisiert");
                assertThat(browser.pageSource()).contains(plz);
                assertThat(browser.$("#zip").getValue()).isEqualTo(plz);
                                
            }
        });
	}
	
	/**
	 * checks if transaction site is returning http status 403 if user is NOT logged in
	 * @author boe
	 */
	@Test
	public void showTransactionsWithoutLoginHttpStatus(final int userid) {
		withoutLoginHttpStatus("http://localhost:3333" + routes.User.showTransactions(userid));
	}
	
	/**
	 * checks if transcation site is empty if user is NOT logged in
	 * @author boe
	 */
	@Test
	public void showTransactionsWithoutLoginContent(final int userid) {
		withoutLoginContent("http://localhost:3333" + routes.User.showTransactions(userid));
	}
	
	/**
	 * checks if a user of type customer can login and see the user data site
	 * @author boe
	 */
	@Test
	public void showTransactionsWithLoginCustomerHttpContent() {
		showTransactionsWithLoginContent("user1@students.ffhs.ch", "ffhs2011", 5);
	}

	/**
	 * checks if a user of type admin can login and see the user data site
	 * @author boe
	 */
	@Test
	public void showTransactionsWithLoginAdminHttpContent() {
		showTransactionsWithLoginContent("patrick.boesch@students.ffhs.ch", "ffhs2011", 1);
	}
	
	/**
	 * the main procedure to check if an logged in user can see the user data site
	 * @param user 
	 * @param password
	 */
	private void showTransactionsWithLoginContent(final String user, final String password, final int userid) {
		running(testServer(3333), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");
                
                browser.fill("#email").with(user);
                browser.fill("#password").with(password);
                browser.submit("#signin");
                
                assertThat(browser.pageSource()).contains("Ausloggen");

                browser.goTo("http://localhost:3333" + routes.User.showTransactions(userid));

                assertThat(browser.pageSource()).contains("Bestellungen");
                assertThat(browser.pageSource()).contains("Status");
                assertThat(browser.pageSource()).contains("Total");
                assertThat(browser.pageSource()).doesNotContain("Login");
            }
        });
	}
	
	/**
	 * the main procedure to check if an url returns status 403 if user is NOT logged in
	 * @param url
	 */
	public void withoutLoginHttpStatus(final String url) {	
        running(testServer(3333, fakeApplication()), new Runnable() {
			
			@Override
			public void run() {
				assertThat(WS.url(url).get().get().getStatus()).isEqualTo(403);
			}
		});
	}
	
	/**
	 * the main procedure to check if an url returns an empty result if user is NOT logged in
	 * @param url
	 */
	private void withoutLoginContent(final String url) {
		running(testServer(3333), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo(url);
                
                assertThat(browser.pageSource()).isEmpty();
            }
        });		
	}
}
