package eshomo.product;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import models.*;

import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import play.libs.F.Callback;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestBrowser;
import controllers.routes;
import eshomo.EshomoTest;

public class ProductIntegrationTest extends EshomoTest {
	/**
	 * Test search with a basic query 
	 */
	@Test
	public void testBasicSearch() {
        FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), new Runnable() {
			public void run() {
				TestBrowser browser = getBrowser();
				browser.goTo("http://localhost:3333");
                
				browser.fill("#search").with("HP");
				browser.submit("#search-button");
                
                assertThat(browser.pageSource()).contains("Compaq 6305 Pro");
                assertThat(browser.pageSource()).doesNotContain("Apple");
            }
        });
	}
	
	/**
	 * Test list of all products
	 */
	@Test
	public void testProductList() {
		FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	TestBrowser browser = getBrowser();
                browser.goTo("http://localhost:3333");
                
                assertThat(browser.pageSource()).contains("Compaq 6305 Pro");
                assertThat(browser.pageSource()).contains("Apple");
            }
        });
	}
	
	/**
	 * Test the details page of a product
	 */
	@Test
	public void testProductDetails() {
		
		FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	Integer productId = 1;
            	Product product = Product.find.byId(productId);
            	TestBrowser browser = getBrowser();
            	
                browser.goTo("http://localhost:3333" + routes.Product.details(productId, 0));
                
                assertThat(browser.pageSource()).contains("Preis");
                assertThat(browser.pageSource()).contains(product.getPrice().toString());
                assertThat(browser.pageSource()).contains(product.getEan().toString());
                assertThat(browser.pageSource()).contains(product.getId().toString());
                assertThat(browser.pageSource()).contains(product.getDescription());
                assertThat(browser.pageSource()).contains("Bewertungen");
            }
        });
	}
	
	/**
	 * Test the rating a product
	 */
	@Test
	public void testProductRating() {
		FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	Integer productId = 1;
            	String comment = "This is my comment";
            	User user = User.find.byId(1);
            	TestBrowser browser = getBrowser();
            	
            	browser.goTo("http://localhost:3333");
            	
            	if(!browser.pageSource().contains("Ausloggen")){
            		browser.fill("#email").with(user.getEmail());
            		browser.fill("#password").with("ffhs2011");
            		browser.submit("#signin");
				}
            	
                assertThat(browser.pageSource()).contains("Ausloggen");
                
                browser.goTo("http://localhost:3333" + routes.Product.details(productId, 0));
                
                browser.click("#rate_5");
                browser.fill("#comment").with(comment);
                browser.submit("#submit-rating");

                assertThat(browser.pageSource()).contains("Kommentar");
                assertThat(browser.pageSource()).contains(comment);
                assertThat(browser.pageSource()).contains(user.getAdress().getFirstname());
            }
        });
	}
	
	/**
	 * Add a product and check values
	 */
	@Test
	public void testProductAdd(){
		FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	TestBrowser browser = getBrowser();
            	String title = "Test Product";
            	String description = "This is my comment";
            	String ean = "1231231231239";
            	String price = "12.33";
            	String attribute = "Very very long";
            	String tag = "NewTag";
            	User user = User.find.byId(1);
            	
            	browser.goTo("http://localhost:3333");
            	
            	if(!browser.pageSource().contains("Ausloggen")){
            		browser.fill("#email").with(user.getEmail());
            		browser.fill("#password").with("ffhs2011");
            		browser.submit("#signin");
				}
            	
                assertThat(browser.pageSource()).contains("Produkte bearbeiten");
                
                browser.goTo("http://localhost:3333" + routes.Product.add());
                
                browser.fill("#title").with(title);
                browser.fill("#description").with(description);
                browser.fill("#ean").with(ean);
                browser.fill("#price").with(price);
                browser.fill("#attributes_0__value").with(attribute);
                browser.fill("#tags_0__id").with(tag);
                browser.submit("#save-product");
                
                Product product = Product.find.where().eq("title", title).findUnique();
                
                assertThat(product != null);
                
                browser.goTo("http://localhost:3333" + routes.Product.details(product.getId(), 0));

                assertThat(browser.pageSource()).contains(title);
                assertThat(browser.pageSource()).contains(description);
                assertThat(browser.pageSource()).contains(ean);
                assertThat(browser.pageSource()).contains(price);
                assertThat(browser.pageSource()).contains(attribute);
                assertThat(browser.pageSource()).contains(tag);
                
                browser.goTo("http://localhost:3333" + routes.Product.productsByTag(tag));
                assertThat(browser.pageSource()).contains(title);
            }
        });
	}
	
	/**
	 * Edit an existing product
	 */
	@Test
	public void testProductEdit(){
		FakeApplication fakeApp = Helpers.fakeApplication();
		running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
            	TestBrowser browser = getBrowser();
            	Integer productId = 1;
            	String title = "Test Product";
            	String description = "This is my comment";
            	String ean = "1231231231239";
            	String price = "12.33";
            	String attribute = "Very very long";
            	User user = User.find.byId(1);
            	
            	browser.goTo("http://localhost:3333");
            	
            	if(!browser.pageSource().contains("Ausloggen")){
            		browser.fill("#email").with(user.getEmail());
            		browser.fill("#password").with("ffhs2011");
            		browser.submit("#signin");
				}
            	
                assertThat(browser.pageSource()).contains("Produkte bearbeiten");
                
                browser.goTo("http://localhost:3333" + routes.Product.edit(productId));
                
                browser.fill("#title").with(title);
                browser.fill("#description").with(description);
                browser.fill("#ean").with(ean);
                browser.fill("#price").with(price);
                browser.fill("#attributes_0__value").with(attribute);
                browser.submit("#save-product");
                
                browser.goTo("http://localhost:3333" + routes.Product.details(productId, 0));

                assertThat(browser.pageSource()).contains(title);
                assertThat(browser.pageSource()).contains(description);
                assertThat(browser.pageSource()).contains(ean);
                assertThat(browser.pageSource()).contains(price);
                assertThat(browser.pageSource()).contains(attribute);
            }
        });
	}
}
