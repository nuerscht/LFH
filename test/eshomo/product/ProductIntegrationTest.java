package eshomo.product;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import models.Product;
import models.User;

import org.junit.Test;

import play.libs.F.Callback;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestBrowser;
import controllers.routes;
import eshomo.EshomoTest;

public class ProductIntegrationTest extends EshomoTest {
	/**
	 * 
	 * @param search 
	 */
	@Test
	public void testBasicSearch() {
        FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");
                
                browser.fill("#search").with("HP");
                browser.submit("#search");
                
                assertThat(browser.pageSource()).contains("Compaq 6305 Pro");
                assertThat(browser.pageSource()).doesNotContain("Apple");
            }
        });
	}
	
	public void testProductList() {
		FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");
                
                assertThat(browser.pageSource()).contains("Compaq 6305 Pro");
                assertThat(browser.pageSource()).contains("Apple");
            }
        });
	}
	
	@Test
	public void testProductDetails() {
		
		FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
            	Integer productId = 1;
            	Product product = Product.find.byId(productId);
            	
                browser.goTo("http://localhost:3333" + routes.Product.details(productId));
                
                assertThat(browser.pageSource()).contains("Preis");
                assertThat(browser.pageSource()).contains(product.getPrice().toString());
                assertThat(browser.pageSource()).contains(product.getEan().toString());
                assertThat(browser.pageSource()).contains(product.getId().toString());
                assertThat(browser.pageSource()).contains(product.getDescription());
                assertThat(browser.pageSource()).contains("Bewertungen");
            }
        });
	}
	
	@Test
	public void testProductRating() {
		FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
            	Integer productId = 1;
            	String comment = "This is my comment";
            	User user = User.find.byId(1);
            	
            	browser.goTo("http://localhost:3333");
            	
            	if(!browser.pageSource().contains("Ausloggen")){
            		browser.fill("#email").with(user.getEmail());
            		browser.fill("#password").with("ffhs2011");
            		browser.submit("#signin");
				}
            	
                assertThat(browser.pageSource()).contains("Ausloggen");
                
                browser.goTo("http://localhost:3333" + routes.Product.details(productId));
                
                browser.click("#rate_5");
                browser.fill("#comment").with(comment);
                browser.submit("#submit-rating");

                assertThat(browser.pageSource()).contains("Kommentar");
                assertThat(browser.pageSource()).contains(comment);
                // As long as we don't have stars to rate we check the value
                assertThat(browser.pageSource()).contains("5");
                assertThat(browser.pageSource()).contains(user.getEmail());
            }
        });
	}
}
