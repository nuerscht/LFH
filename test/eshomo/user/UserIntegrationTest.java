package eshomo.user;

import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import static org.fest.assertions.Assertions.*;

import java.util.Random;

import org.junit.Test;

import controllers.routes;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestBrowser;
import eshomo.EshomoTest;

/**
 * testing for user functionality
 * @author boe
 */
public class UserIntegrationTest extends EshomoTest {
	
	/**
	 * checks if user data site is empty if user is NOT logged in
	 * @author boe
	 */
	@Test
	public void showDataWithoutLoginContent() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                String url = "http://localhost:3333" + routes.User.showDataCurrent();
                
                TestBrowser browser = getBrowser();
                browser.goTo(url);
                
                assertThat(browser.pageSource().contains("No logged in user found."));
            }
        }); 
	}
	
	/**
	 * checks if a user of type customer can login and see the user data site
	 * @author boe
	 */
	@Test
	public void showDataWithLoginCustomerHttpContent() {
        showDataWithLoginContent("user2@students.ffhs.ch", "ffhs2011", 6);
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
        FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	TestBrowser browser = getBrowser();
                browser.goTo("http://localhost:3333");
                
                browser.fill("#email").with(user);
                browser.fill("#password").with(password);
                browser.submit("#signin");
                
                //assertThat(browser.pageSource()).contains("Ausloggen");

                browser.goTo("http://localhost:3333" + routes.User.showData(userid));

                assertThat(browser.pageSource()).contains("Benutzerdaten");
                assertThat(browser.pageSource()).contains("Adresse");
                assertThat(browser.pageSource()).contains("Aktualisieren");
            }
        });
	}
	
	/**
	 * checks if a user of type customer can update his data
	 * @author boe
	 */
	@Test
	public void updateDataWithLoginCustomerHttpContent() {
		updateDataWithLoginContent("user2@students.ffhs.ch", "ffhs2011", 6);
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
	    FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	TestBrowser browser = getBrowser();
                browser.goTo("http://localhost:3333");
                
                browser.fill("#email").with(user);
                browser.fill("#password").with(password);
                browser.submit("#signin");
                
                //assertThat(browser.pageSource()).contains("Ausloggen");

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
	 * checks if transcation site is empty if user is NOT logged in
	 * @author boe
	 */
	@Test
	public void showTransactionsWithoutLoginContent() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                int userid = 1;
                String url = "http://localhost:3333" + routes.User.showTransactions(userid);
                
                TestBrowser browser = getBrowser();
                browser.goTo(url);
                
                assertThat(browser.pageSource().contains("No logged in user found."));
            }
        }); 
	}
	
	/**
	 * checks if a user of type customer can login and see the user data site
	 * @author boe
	 */
	@Test
	public void showTransactionsWithLoginCustomerHttpContent() {
		showTransactionsWithLoginContent("user2@students.ffhs.ch", "ffhs2011", 6);
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
        FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	TestBrowser browser = getBrowser();
                browser.goTo("http://localhost:3333");
                
                browser.fill("#email").with(user);
                browser.fill("#password").with(password);
                browser.submit("#signin");
                
                //assertThat(browser.pageSource()).contains("Ausloggen");

                browser.goTo("http://localhost:3333" + routes.Cart.history());

                assertThat(browser.pageSource()).contains("Es sind keine Bestellungen vorhanden.");
            }
        });
	}
	
	/**
	 * the main procedure to check if an url returns an empty result if user is NOT logged in
	 * @param url
	 */
	private void withoutLoginContent(final String url) {
        FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	TestBrowser browser = getBrowser();
                browser.goTo(url);
                
                assertThat(browser.pageSource()).isEmpty();
            }
        });		
	}

    /**
     * the procedure to check deleting an user
     * @param user 
     * @param password
     */
    @Test
    public void checkUserDelete() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	TestBrowser browser = getBrowser();
                browser.goTo("http://localhost:3333");
                
                browser.fill("#email").with("patrick.boesch@students.ffhs.ch");
                browser.fill("#password").with("ffhs2011");
                browser.submit("#signin");
                
                assertThat(browser.pageSource()).contains("Ausloggen");
                
                browser.goTo("http://localhost:3333" + routes.User.list());
                
                assertThat(browser.pageSource()).contains("user1@students.ffhs.ch");

                browser.goTo("http://localhost:3333" + routes.User.delete(5));
               
                assertThat(browser.pageSource()).doesNotContain("user1@students.ffhs.ch");
            }
        });
    }

    /**
     * the procedure to check user type change
     * @param user 
     * @param password
     */
    @Test
    public void checkUserTypeChange() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	TestBrowser browser = getBrowser();
                browser.goTo("http://localhost:3333");
                
                browser.fill("#email").with("patrick.boesch@students.ffhs.ch");
                browser.fill("#password").with("ffhs2011");
                browser.submit("#signin");
                
                assertThat(browser.pageSource()).contains("Ausloggen");
                
                browser.goTo("http://localhost:3333" + routes.User.list());
                
                assertThat(browser.pageSource()).contains("user1@students.ffhs.ch");

                browser.goTo("http://localhost:3333" + routes.User.delete(2));
                browser.goTo("http://localhost:3333" + routes.User.delete(3));
                browser.goTo("http://localhost:3333" + routes.User.delete(4));
                browser.goTo("http://localhost:3333" + routes.User.delete(5));
                browser.goTo("http://localhost:3333" + routes.User.delete(6));
               
                assertThat(browser.pageSource()).doesNotContain("user1@students.ffhs.ch");
                

                assertThat(browser.pageSource()).contains("customer");
                
                browser.goTo("http://localhost:3333" + routes.User.changeUserType(7));
                assertThat(browser.pageSource()).doesNotContain("customer");
                browser.goTo("http://localhost:3333" + routes.User.changeUserType(7));
                assertThat(browser.pageSource()).contains("customer");
                
            }
        });
    }

    /**
     * the procedure to check user status change
     * @param user 
     * @param password
     */
    @Test
    public void checkUserStatusChange() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	TestBrowser browser = getBrowser();
                browser.goTo("http://localhost:3333");
                
                browser.fill("#email").with("patrick.boesch@students.ffhs.ch");
                browser.fill("#password").with("ffhs2011");
                browser.submit("#signin");
                
                assertThat(browser.pageSource()).contains("Ausloggen");
                
                browser.goTo("http://localhost:3333" + routes.User.list());
                
                assertThat(browser.pageSource()).contains("user1@students.ffhs.ch");

                browser.goTo("http://localhost:3333" + routes.User.delete(2));
                browser.goTo("http://localhost:3333" + routes.User.delete(3));
                browser.goTo("http://localhost:3333" + routes.User.delete(4));
                browser.goTo("http://localhost:3333" + routes.User.delete(5));
                browser.goTo("http://localhost:3333" + routes.User.delete(6));
               
                assertThat(browser.pageSource()).doesNotContain("user1@students.ffhs.ch");
                

                assertThat(browser.pageSource()).doesNotContain("Passiv");
                
                browser.goTo("http://localhost:3333" + routes.User.changeStatus(7));
                assertThat(browser.pageSource()).contains("Passiv");
                browser.goTo("http://localhost:3333" + routes.User.changeStatus(7));
                assertThat(browser.pageSource()).doesNotContain("Passiv");
                
            }
        });
    }

    /**
     * the procedure to check user search
     * @param user 
     * @param password
     */
    @Test
    public void checkUserSearch() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	TestBrowser browser = getBrowser();
                browser.goTo("http://localhost:3333");

                browser.fill("#email").with("patrick.boesch@students.ffhs.ch");
                browser.fill("#password").with("ffhs2011");
                browser.submit("#signin");
                
                assertThat(browser.pageSource()).contains("Ausloggen");
                
                browser.goTo("http://localhost:3333" + routes.User.list());

                assertThat(browser.pageSource()).contains("user1@students.ffhs.ch");
                browser.fill("#keyword").with("patrick.boesch");
                browser.submit("#search-user");
                assertThat(browser.pageSource()).contains("patrick.boesch@students.ffhs.ch");
                assertThat(browser.pageSource()).doesNotContain("user1@students.ffhs.ch");

                browser.fill("#keyword").with("2");
                browser.submit("#search-user");
                assertThat(browser.pageSource()).contains("andy.villiger@students.ffhs.ch");
                assertThat(browser.pageSource()).doesNotContain("user1@students.ffhs.ch");

                browser.fill("#keyword").with("Alder");
                browser.submit("#search-user");
                assertThat(browser.pageSource()).contains("jonas.alder@students.ffhs.ch");
                assertThat(browser.pageSource()).doesNotContain("user1@students.ffhs.ch");

                browser.fill("#keyword").with("Sandro");
                browser.submit("#search-user");
                assertThat(browser.pageSource()).contains("sandro.dallo@students.ffhs.ch");
                assertThat(browser.pageSource()).doesNotContain("user1@students.ffhs.ch");
                
            }
        });
    }
}
