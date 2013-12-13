package test.api;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import models.Address;
import models.Attribute;
import models.Cart;
import models.CartHasProduct;
import models.CartStatus;
import models.Product;
import models.User;
import models.UserType;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.WS;
import play.libs.WS.WSRequestHolder;
import play.test.FakeApplication;
import play.test.Helpers;
import eshomo.EshomoTest;

/**
 * Integration tests for API functionality.
 * @author dal
 *
 */
public class ApiIntegrationTest extends EshomoTest {

    // private constant fields
    private static final String CUSTOMERL_URL = "/customers";
    private static final String ORDERS_URL = "/orders";
    private static final String ARTICLES_URL = "/articles";
    private static final String VERSION_URL = "/version";
    private static final String SERVER_URL = "http://localhost:3333";
    protected static final String CURRENCY = "Chf";
    protected static final String VERSION = "1.0";

    /**
     * Checks if call to the /customers succeeds if user is active and an admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginValidUser_Customer() {
        User user = getNewUser(true, true);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(CUSTOMERL_URL, params, 200, "<customers>");
    }

    /**
     * Checks if call to the /articles succeeds if user is active and an admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginValidUser_Articles() {
        User user = getNewUser(true, true);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(ARTICLES_URL, params, 200, "<articles>");
    }

    /**
     * Checks if call to the /orders succeeds if user is active and an admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginValidUser_Orders() {
        User user = getNewUser(true, true);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(ORDERS_URL, params, 404, "Angeforderte Ressource nicht gefunden");
    }

    /**
     * Checks if call to the /version succeeds if user is active and an admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginValidUser_Version() {
        User user = getNewUser(true, true);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(VERSION_URL, params, 200, "1.0");
    }

    /**
     * Checks if call to the /customers fails if user is active but not an
     * admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonAdmin_Customer() {
        User user = getNewUser(false, true);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(CUSTOMERL_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the /articles fails if user is active but not an admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonAdmin_Articles() {
        User user = getNewUser(false, true);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(ARTICLES_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the /orders fails if user is active but not an admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonAdmin_Orders() {
        User user = getNewUser(false, true);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(ORDERS_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the /version fails if user is active but not an admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonAdmin_Version() {
        User user = getNewUser(false, true);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(VERSION_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the /customers fails if user is admin but not active.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonActive_Customer() {
        User user = getNewUser(true, false);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(CUSTOMERL_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the /articles fails if user is admin but not active.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonActive_Articles() {
        User user = getNewUser(true, false);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(ARTICLES_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the /orders fails if user is admin but not active.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonActive_Orders() {
        User user = getNewUser(true, false);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(ORDERS_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the /version fails if user is admin but not active.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonActive_Version() {
        User user = getNewUser(true, false);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(VERSION_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the /customers fails if user is inactive and is not an
     * admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonActiveAdmin_Customer() {
        User user = getNewUser(false, false);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(CUSTOMERL_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the /articles fails if user is inactive and is not an
     * admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonActiveAdmin_Articles() {
        User user = getNewUser(false, false);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(ARTICLES_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the /orders fails if user is inactive and is not an
     * admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonActiveAdmin_Orders() {
        User user = getNewUser(false, false);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(ORDERS_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the /version fails if user is inactive and is not an
     * admin.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNonActiveAdmin_Version() {
        User user = getNewUser(false, false);
        String[] params = new String[] { "id", "all", "token", user.getToken() };
        checkTokenLogin(VERSION_URL, params, 401, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the API /customers fails if no token is supplied.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNoToken_Customer() {
        String[] params = new String[] { "id", "all" };
        checkTokenLogin(CUSTOMERL_URL, params, 400, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the API /articles fails if no token is supplied.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNoToken_Articles() {
        String[] params = new String[] { "id", "all" };
        checkTokenLogin(ARTICLES_URL, params, 400, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the API /orders fails if no token is supplied.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNoToken_Orders() {
        String[] params = new String[] { "id", "all" };
        checkTokenLogin(ORDERS_URL, params, 400, "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the API /version fails if no token is supplied.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNoToken_Version() {
        String[] params = new String[] { "id", "all" };
        checkTokenLogin(VERSION_URL, params, 400, "Kein gültiges Token gefunden");
    }

    /**
     * Calls the API with the passed parameters and verifies the http status and
     * content.
     * 
     * @author dal
     * @param url
     *            The API Url
     * @param queryParams
     *            An array of parameters (key,value,key,value,...)
     * @param expectedStatus
     *            The expected status code
     * @param expectedContent
     *            The expected content
     */
    private void checkTokenLogin(final String url, final String[] queryParams,
            final int expectedStatus, final String expectedContent) {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                WS.Response response = callApi(url, queryParams, new String[] {});
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(expectedStatus);
                assertThat(response.getBody()).contains(expectedContent);
            }
        });
    }
    
    
    /**
     * Checks if the API returns all articles and if the xml document is well formed.
     * 
     * @author dal
     */
    @Test
    public void getAllArticelsXml() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(ARTICLES_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                NodeList nodes = dom.getElementsByTagName("article");
                int count = Product.find.all().size();
                assertThat(nodes.getLength()).isEqualTo(count);

            }
        });
    }

    /**
     * Checks if the API returns a 304 if no article has changed since the 
     * last service call.
     * 
     * @author dal
     */
    @Test
    public void getAllArticelsXml_NotModified() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(ARTICLES_URL, params, null);
                // Call service and get the etag
                String etag = response.getHeader("Etag");
                // Call service again with the etag
                String[] headers = new String[] { "If-None-Match", etag };
                response = callApi(ARTICLES_URL, params, headers);
                // Check response status
                assertThat(response.getStatus()).isEqualTo(304);
                assertThat(response.getBody()).isEmpty();

            }
        });
    }
    
    /**
     * Checks if the API returns the article specified by the
     * query parameter 'id' and verifies the values with the one
     * from the database.
     * 
     * @author dal
     */
    @Test
    public void getOneArticleXml() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                Product p = Product.find.byId(1);
                String[] params = new String[] { "id", "1", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(ARTICLES_URL, params, null);
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                Element node = (Element) dom.getElementsByTagName("article").item(0);
                DecimalFormat df = new DecimalFormat("0.00");
                assertThat(node.getAttribute("id")).isEqualTo(p.getId().toString());
                assertThat(node.getElementsByTagName("title").item(0).getTextContent()).isEqualTo(
                        p.getTitle());
                assertThat(node.getElementsByTagName("description").item(0).getTextContent())
                        .isEqualTo(p.getDescription());
                assertThat(node.getElementsByTagName("ean").item(0).getTextContent()).isEqualTo(
                        p.getEan().toString());
                assertThat(node.getElementsByTagName("price").item(0).getTextContent()).isEqualTo(
                        df.format(p.getPrice()).toString());
                assertThat(node.getElementsByTagName("currency").item(0).getTextContent())
                        .isEqualTo(CURRENCY);
                NodeList list = node.getElementsByTagName("attribute");
                int attrMatches = 0;
                for (int i = 0; i < list.getLength(); i++) {
                    for (Attribute att : p.getAttributes()) {
                        if (list.item(i).getTextContent().equalsIgnoreCase(att.getValue())) {
                            attrMatches++;
                            break;
                        }
                    }
                }
                assertThat(attrMatches).isEqualTo(p.getAttributes().size());

            }
        });
    }

    /**
     * Checks if API returns a 304 if the article hasn't changed.
     * 
     * @author dal
     */
    @Test
    public void getOneArticleXml_NotModified() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                String[] params = new String[] { "id", "1", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(ARTICLES_URL, params, null);
                // Call service and get the etag
                String etag = response.getHeader("Etag");
                // Call service again with the etag
                String[] headers = new String[] { "If-None-Match", etag };
                response = callApi(ARTICLES_URL, params, headers);
                // Check response status
                assertThat(response.getStatus()).isEqualTo(304);
                assertThat(response.getBody()).isEmpty();

            }
        });
    }

    /**
     * Checks if the API returns only new or changed articles as 
     * specified by the query parameter 'since', means updated_at newer
     * as the passed timestamp.
     * 
     * @author dal
     */
    @Test
    public void getAllArticelsXml_Since() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                // Wait and create new product
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                }
                long since = (new Date()).getTime() / 1000L;
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                }
                createNewProduct();

                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken(), "since", String.valueOf(since) };
                WS.Response response = callApi(ARTICLES_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                NodeList nodes = dom.getElementsByTagName("article");
                assertThat(nodes.getLength()).isEqualTo(1);

            }
        });
    }
    
    /**
     * Checks if the API returns all customers and if the xml document is well formed.
     * 
     * @author dal
     */
    @Test
    public void getAllCustomersXml() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(CUSTOMERL_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                NodeList nodes = dom.getElementsByTagName("customer");
                int count = User.find.all().size();
                assertThat(nodes.getLength()).isEqualTo(count);

            }
        });
    }

    /**
     * Checks if the API returns a 304 if no customer has changed.
     * 
     * @author dal
     */
    @Test
    public void getAllCustomerXml_NotModified() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(CUSTOMERL_URL, params, null);
                // Call service and get the etag
                String etag = response.getHeader("Etag");
                // Call service again with the etag
                String[] headers = new String[] { "If-None-Match", etag };
                response = callApi(CUSTOMERL_URL, params, headers);
                // Check response status
                assertThat(response.getStatus()).isEqualTo(304);
                assertThat(response.getBody()).isEmpty();

            }
        });
    }
    
    /**
     * Checks if the API returns the specified customer by the
     * query parameter 'id' and has the same values as the db 
     * entity.
     * 
     * @author dal
     */
    @Test
    public void getOneCustomerXml() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                User user = User.find.byId(1);
                Address address = user.getAddresses().get(0);
                String[] params = new String[] { "id", "1", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(CUSTOMERL_URL, params, null);
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                Element node = (Element) dom.getElementsByTagName("customer").item(0);

                assertThat(node.getAttribute("id")).isEqualTo(user.getId().toString());
                assertThat(node.getElementsByTagName("name").item(0).getTextContent()).isEqualTo(
                        address.getLastname());
                assertThat(node.getElementsByTagName("firstname").item(0).getTextContent())
                        .isEqualTo(address.getFirstname());
                assertThat(node.getElementsByTagName("email").item(0).getTextContent()).isEqualTo(
                        address.getEmail());
                assertThat(node.getElementsByTagName("birthdate").item(0).getTextContent())
                        .isEqualTo(String.valueOf(address.getBirthday().getTime() / 1000L));

                node = (Element) node.getElementsByTagName("address").item(0);
                assertThat(node.getAttribute("id")).isEqualTo(address.getId().toString());
                assertThat(node.getAttribute("type")).isEqualTo("1");
                assertThat(node.getElementsByTagName("city").item(0).getTextContent()).isEqualTo(
                        address.getPlace());
                assertThat(node.getElementsByTagName("street").item(0).getTextContent()).isEqualTo(
                        address.getStreet());
                assertThat(node.getElementsByTagName("postcode").item(0).getTextContent())
                        .isEqualTo(address.getZip().toString());
                assertThat(node.getElementsByTagName("country").item(0).getTextContent())
                        .isEqualTo(address.getCountry().getName());

            }
        });
    }

    /**
     * Checks if the API returns a 304 if the customer hasn't changed.
     * 
     * @author dal
     */
    @Test
    public void getOneCustomerXml_NotModified() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                String[] params = new String[] { "id", "1", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(CUSTOMERL_URL, params, null);
                // Call service and get the etag
                String etag = response.getHeader("Etag");
                // Call service again with the etag
                String[] headers = new String[] { "If-None-Match", etag };
                response = callApi(CUSTOMERL_URL, params, headers);
                // Check response status
                assertThat(response.getStatus()).isEqualTo(304);
                assertThat(response.getBody()).isEmpty();

            }
        });
    }

    /**
     * Checks if the API returns only new or changed customer since the 
     * specified unix timestamp passed by the query string.
     * 
     * @author dal
     */
    @Test
    public void getAllCustomerXml_Since() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                // Wait and create new product
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                }
                long since = (new Date()).getTime() / 1000L;
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                }
                // create a user
                User user = new User();
                user.setEmail("jabba@thehutt.com");
                user.setIsActive(true);
                user.setPassword("ihatesolo");
                user.setType(UserType.find.byId("admin"));
                user.setIsActive(true);
                user.save();
                
                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken(), "since", String.valueOf(since) };
                WS.Response response = callApi(CUSTOMERL_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                NodeList nodes = dom.getElementsByTagName("customer");
                assertThat(nodes.getLength()).isEqualTo(1);

            }
        });
    }

    /**
     * Check if API returns all orders and if the xml format is well formed.
     * 
     * @author dal
     */
    @Test
    public void getAllOrdersXml() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                createCarts();
                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(ORDERS_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for orders
                Document dom = response.asXml();
                NodeList nodes = dom.getElementsByTagName("order");
                int count = Cart.find.all().size();
                assertThat(nodes.getLength()).isEqualTo(count);

            }
        });
    }

    /**
     * Checks if the API returns a 304 if no order has changed.
     * 
     * @author dal
     */
    @Test
    public void getAllOrdersXml_NotModified() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                createCarts();
                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(ORDERS_URL, params, null);
                // Call service and get the etag
                String etag = response.getHeader("Etag");
                // Call service again with the etag
                String[] headers = new String[] { "If-None-Match", etag };
                response = callApi(ORDERS_URL, params, headers);
                // Check response status
                assertThat(response.getStatus()).isEqualTo(304);
                assertThat(response.getBody()).isEmpty();

            }
        });
    }
    
    /**
     * Checks if the return order has the correct xml format and
     * the same values as the db entity.
     * 
     * @author dal
     */
    @Test
    public void getOneOrderXml() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                createCarts();
                Cart cart = Cart.find.byId(1);
                Address address = cart.getAddress();
                List<CartHasProduct> chp = cart.getCartHasProduct();
                User user = cart.getUser();
                String[] params = new String[] { "id", "1", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(ORDERS_URL, params, null);
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                Element node = (Element) dom.getElementsByTagName("order").item(0);

                assertThat(node.getAttribute("id")).isEqualTo(cart.getId().toString());
                assertThat(node.getAttribute("customer")).isEqualTo(user.getId().toString());
                assertThat(node.getAttribute("billaddress")).isEqualTo(address.getId().toString());
                assertThat(node.getAttribute("shippingaddress")).isEqualTo(address.getId().toString());
                assertThat(node.getAttribute("status")).isEqualTo(cart.getStatus().getId().toString());
                
                NodeList nodeList = node.getElementsByTagName("position");
                assertThat(nodeList.getLength()).isEqualTo(chp.size());
                
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element el = (Element) nodeList.item(i);
                    CartHasProduct c = chp.get(i);
                    assertThat(el.getAttribute("article")).isEqualTo(c.getProduct().getId().toString());
                    assertThat(el.getAttribute("amount")).isEqualTo(c.getAmount().toString());
                    assertThat(el.getAttribute("discount")).isEqualTo(c.getDiscount().toString());
                }
                

            }
        });
    }
    /**
     * Checks if the API returns a 304 if the order hasn't changed.
     * 
     *  @author dal
     */
    @Test
    public void getOneOrderXml_NotModified() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                createCarts();
                String[] params = new String[] { "id", "1", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(ORDERS_URL, params, null);
                // Call service and get the etag
                String etag = response.getHeader("Etag");
                // Call service again with the etag
                String[] headers = new String[] { "If-None-Match", etag };
                response = callApi(ORDERS_URL, params, headers);
                // Check response status
                assertThat(response.getStatus()).isEqualTo(304);
                assertThat(response.getBody()).isEmpty();

            }
        });
    }
    /**
     * Checks if the API calls returns only changed or new orders as the specified
     * unix timestamp in the query parameter.
     * 
     *  @author dal
     */
    @Test
    public void getAllOrdersXml_Since() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                // Wait and create new order
                createCarts();
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                }
                long since = (new Date()).getTime() / 1000L;
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                }
                // Create cart
                createCart(new int[][]{{1,3,1}},User.find.byId(1), CartStatus.OPEN);
                // Get carts
                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken(), "since", String.valueOf(since) };
                WS.Response response = callApi(ORDERS_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                NodeList nodes = dom.getElementsByTagName("order");
                assertThat(nodes.getLength()).isEqualTo(1);

            }
        });
    }
    
    /**
     * Checks if API returns the version as xml document.
     * 
     * @author dal
     */
    @Test
    public void getVersionXml() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                
                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken()};
                WS.Response response = callApi(VERSION_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("text/xml");

                // Get Xml dom and check for products
                Document dom = response.asXml();
                NodeList nodes = dom.getElementsByTagName("version");
                assertThat(nodes.getLength()).isEqualTo(1);
                assertThat(nodes.item(0).getTextContent()).isEqualTo(VERSION);
                

            }
        });
    }
    
    /**
     * Checks if API returns the version as xml document.
     * 
     * @author dal
     */
    @Test
    public void getVersionJson() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                
                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken(),"type","json"};
                WS.Response response = callApi(VERSION_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("application/json");

                // Get json object and check for version
                JsonNode node = response.asJson();
                assertThat(node.path("version").asText()).isEqualTo(VERSION);
                
            }
        });
    }
    
    /**
     * Verifies the json behavior of the API. Only one test per url because
     * the xml and json service call, shares the same code base.
     */
    @Test
    public void getOneArticlesJson() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                createCarts();
                Product product = Product.find.byId(1);
                String[] params = new String[] { "id", "1",
                        "type","json","token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(ARTICLES_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("application/json");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get json object and check for articles
                JsonNode node = response.asJson();
                node = node.get("articles").get(0);
                assertThat(node.path("id").asInt()).isEqualTo(product.getId());
                assertThat(node.path("title").asText()).isEqualTo(product.getTitle());
                assertThat(node.path("description").asText()).isEqualTo(product.getDescription());
                assertThat(node.path("price").asDouble()).isEqualTo(product.getPrice());
                assertThat(node.path("currency").asText()).isEqualTo(CURRENCY);
                // Check Attributes

                assertThat(node.path("attributes").size()).isEqualTo(product.getAttributes().size());
                int cnt = 0;
                for (JsonNode n : node.path("attributes")) {
                    Attribute att = product.getAttributes().get(cnt++);                    
                    assertThat(n.asText()).isEqualTo(att.getValue());
                }             
            }
        });
    }

    /**
     * Verifies the json behavior of the API. Only one test per url because
     * the xml and json service call, shares the same code base.
     */
    @Test
    public void getOneCustomerJson() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                createCarts();
                User user = User.find.byId(1);
                String[] params = new String[] { "id", "1",
                        "type","json","token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(CUSTOMERL_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("application/json");
                assertThat(response.getHeader("Etag")).isNotEmpty();
                
                Address addr = user.getAddresses().get(0);

                // Get json object and check for articles
                JsonNode node = response.asJson();
                node = node.get("customers").get(0);
                assertThat(node.path("id").asInt()).isEqualTo(user.getId());
                assertThat(node.path("name").asText()).isEqualTo(addr.getLastname());
                assertThat(node.path("firstname").asText()).isEqualTo(addr.getFirstname());
                assertThat(node.path("email").asText()).isEqualTo(addr.getEmail());
                assertThat(node.path("birthday").asLong()).isEqualTo(addr.getBirthday().getTime() / 1000L);
                // Check Addresses
                assertThat(node.path("addresses").size()).isEqualTo(user.getAddresses().size());
                int cnt = 0;
                for (JsonNode n : node.path("addresses")) {
                    Address a = user.getAddresses().get(cnt++);                    
                    assertThat(n.path("id").asInt()).isEqualTo(a.getId());
                    assertThat(n.path("type").asInt()).isEqualTo(1);
                    assertThat(n.path("city").asText()).isEqualTo(a.getPlace());
                    assertThat(n.path("street").asText()).isEqualTo(a.getStreet());
                    assertThat(n.path("postcode").asText()).isEqualTo(a.getZip());
                    assertThat(n.path("country").asText()).isEqualTo(a.getCountry().getName());
                }             
            }
        });
    }

    /**
     * Verifies the json behavior of the API. Only one test per url because
     * the xml and json service call, shares the same code base.
     */
    @Test
    public void getOneOrderJson() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {
                createCarts();
                Cart order = Cart.find.byId(1);
                String[] params = new String[] { "id", "1",
                        "type","json","token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(ORDERS_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo("application/json");
                assertThat(response.getHeader("Etag")).isNotEmpty();
                
                Address addr = order.getAddress();
                User user = order.getUser();

                // Get json object and check for articles
                JsonNode node = response.asJson();
                node = node.get("orders").get(0);
                assertThat(node.path("id").asInt()).isEqualTo(order.getId());
                assertThat(node.path("customer").asInt()).isEqualTo(user.getId());
                assertThat(node.path("billaddress").asInt()).isEqualTo(addr.getId());
                assertThat(node.path("shippingaddress").asInt()).isEqualTo(addr.getId());
                assertThat(node.path("status").asText()).isEqualTo(order.getStatus().getId());
                // Check positions
                assertThat(node.path("positions").size()).isEqualTo(order.getCartHasProduct().size());
                int cnt = 0;
                for (JsonNode n : node.path("positions")) {
                    CartHasProduct prod = order.getCartHasProduct().get(cnt++);                    
                    assertThat(n.path("article").asInt()).isEqualTo(prod.getProduct().getId());
                    assertThat(n.path("amount").asInt()).isEqualTo(prod.getAmount());
                    assertThat(n.path("discount").asDouble()).isEqualTo(prod.getDiscount());
                }             
            }
        });
    }
    
    /**
     * Calls the API with the given parameters and returns a response.
     * 
     * @param url
     *            API url to call
     * @param queryParams
     *            A String array with the query parameters
     * @param headers
     *            A String Array with the headers
     * @return The response of the service call
     */
    private WS.Response callApi(final String url, final String[] queryParams, final String[] headers) {
        WSRequestHolder result = WS.url(SERVER_URL + url);
        if (queryParams != null)
            for (int i = 0; i + 1 < queryParams.length; i += 2) {
                result.setQueryParameter(queryParams[i], queryParams[i + 1]);
            }
        if (headers != null)
            for (int i = 0; i + 1 < headers.length; i += 2) {
                result.setHeader(headers[i], headers[i + 1]);
            }
        return result.get().get(10000);
    }

    @SuppressWarnings("unused")
    private String unzipBody(InputStream str) {
        InputStreamReader sr = null;
        StringWriter writer = null;
        GZIPInputStream gis = null;
        try {
            char[] buffer = new char[10240];
            gis = new GZIPInputStream(str);
            sr = new InputStreamReader(gis, "UTF-8");
            writer = new StringWriter();

            for (int i = 0; (i = sr.read(buffer)) > 0;) {
                writer.write(buffer, 0, i);
            }

            return writer.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
                if (sr != null)
                    sr.close();
                if (gis != null)
                    gis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Create a new product.
     * 
     * @return The new created product
     */
    private Product createNewProduct() {
        Product p = new Product();
        p.setDescription("Test Product");
        p.setEan(9999999999999L);
        p.setPrice(45.50);
        p.setTitle("Super TEST");
        p.save();
        return p;
    }

    /**
     * Get a valid admin user from the db.
     * 
     * @return An admin user
     */
    private User getUserActiveAdminUser() {
        return User.find.where().eq("isActive", true).eq("type", UserType.find.byId("admin"))
                .findList().get(0);
    }

    private void createCarts() {
        int[][] c1 = new int[][] { { 1, 1, 1 }, { 2, 2, 1 }, { 3, 1, 1 } };
        int[][] c2 = new int[][] { { 4, 3, 1 }, { 5, 2, 1 }, { 6, 1, 1 } };
        int[][] c3 = new int[][] { { 7, 1, 1 }, { 8, 2, 1 }, { 9, 3, 1 } };
        int[][] c4 = new int[][] { { 10, 1, 1 }, { 11, 2, 1 }, { 12, 3, 1 } };
        // Create carts
        createCart(c1, User.find.byId(1), CartStatus.OPEN);
        createCart(c2, User.find.byId(2), CartStatus.ORDERED);
        createCart(c3, User.find.byId(1), CartStatus.ORDERED);
        createCart(c4, User.find.byId(3), CartStatus.OPEN);
    }

    private Cart createCart(int[][] products, User user, String status) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setStatus(CartStatus.find.byId(status));
        cart.setAddress(user.getAddresses().get(0));
        cart.save();
        for (int i = 0; i < products.length; i++) {
            CartHasProduct chp = new CartHasProduct();
            chp.setCart(cart);
            Product p = Product.find.byId(products[i][0]);
            chp.setProduct(p);
            chp.setAmount(products[i][1]);
            chp.setDiscount(Float.valueOf(String.valueOf(products[i][2])));
            chp.setPrice(p.getPrice());
            chp.save();
        }
        return cart;
    }

    /**
     * Creates a new user in the DB.
     * 
     * @param isAdmin
     *            User is admin
     * @param isActive
     *            User is activ
     * @return The created user
     */
    private User getNewUser(final Boolean isAdmin, final Boolean isActive) {
        final User user = new User();
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            @Override
            public void run() {
                UserType type;
                if (isAdmin)
                    type = UserType.find.byId("admin");
                else
                    type = UserType.find.byId("customer");
                user.setEmail("jabba@thehutt.com");
                user.setIsActive(true);
                user.setPassword("ihatesolo");
                user.setType(type);
                user.setIsActive(isActive);
                user.save();
            }
        });

        return user;
    }

}
