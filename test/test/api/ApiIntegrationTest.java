package test.api;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import models.Address;
import models.Attribute;
import models.Cart;
import models.CartHasProduct;
import models.Product;
import models.User;
import models.UserType;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.reader.StreamReader;

import akka.dispatch.AbstractNodeQueue.Node;
import play.libs.WS;
import play.libs.WS.WSRequestHolder;
import play.test.FakeApplication;
import play.test.Helpers;
import eshomo.EshomoTest;

public class ApiIntegrationTest extends EshomoTest {

    // private constant fields
    private static final String CUSTOMERL_URL = "/customers";
    private static final String ORDERS_URL = "/orders";
    private static final String ARTICLES_URL = "/articles";
    private static final String VERSION_URL = "/version";
    private static final String SERVER_URL = "http://localhost:3333";
    protected static final String CURRENCY = "Chf";

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
        checkTokenLogin(ORDERS_URL, params, 404,
                "Angeforderte Ressource nicht gefunden");
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
        checkTokenLogin(CUSTOMERL_URL, params, 401,
                "Kein gültiges Token gefunden");
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
        checkTokenLogin(ARTICLES_URL, params, 401,
                "Kein gültiges Token gefunden");
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
        checkTokenLogin(VERSION_URL, params, 401,
                "Kein gültiges Token gefunden");
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
        checkTokenLogin(CUSTOMERL_URL, params, 401,
                "Kein gültiges Token gefunden");
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
        checkTokenLogin(ARTICLES_URL, params, 401,
                "Kein gültiges Token gefunden");
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
        checkTokenLogin(VERSION_URL, params, 401,
                "Kein gültiges Token gefunden");
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
        checkTokenLogin(CUSTOMERL_URL, params, 401,
                "Kein gültiges Token gefunden");
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
        checkTokenLogin(ARTICLES_URL, params, 401,
                "Kein gültiges Token gefunden");
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
        checkTokenLogin(VERSION_URL, params, 401,
                "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the API /customers fails if no token is supplied.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNoToken_Customer() {
        String[] params = new String[] { "id", "all" };
        checkTokenLogin(CUSTOMERL_URL, params, 400,
                "Kein gültiges Token gefunden");
    }

    /**
     * Checks if call to the API /articles fails if no token is supplied.
     * 
     * @author dal
     */
    @Test
    public void checkTokenLoginNoToken_Articles() {
        String[] params = new String[] { "id", "all" };
        checkTokenLogin(ARTICLES_URL, params, 400,
                "Kein gültiges Token gefunden");
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
        checkTokenLogin(VERSION_URL, params, 400,
                "Kein gültiges Token gefunden");
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
                WS.Response response = callApi(url, queryParams,
                        new String[] {});
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(expectedStatus);
                assertThat(response.getBody()).contains(expectedContent);
            }
        });
    }

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
                assertThat(response.getHeader("Content-Type")).isEqualTo(
                        "text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                NodeList nodes = dom.getElementsByTagName("article");
                int count = Product.find.all().size();
                assertThat(nodes.getLength()).isEqualTo(count);

            }
        });
    }

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

    @Test
    public void getOneArticleXml() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            @SuppressWarnings("rawtypes")
            public void run() {
                Product p = Product.find.byId(1);
                String[] params = new String[] { "id", "1", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(ARTICLES_URL, params, null);
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo(
                        "text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                Element node = (Element) dom.getElementsByTagName("article")
                        .item(0);
                DecimalFormat df = new DecimalFormat("0.00");
                assertThat(node.getAttribute("id")).isEqualTo(
                        p.getId().toString());
                assertThat(
                        node.getElementsByTagName("title").item(0)
                                .getTextContent()).isEqualTo(p.getTitle());
                assertThat(
                        node.getElementsByTagName("description").item(0)
                                .getTextContent())
                        .isEqualTo(p.getDescription());
                assertThat(
                        node.getElementsByTagName("ean").item(0)
                                .getTextContent()).isEqualTo(
                        p.getEan().toString());
                assertThat(
                        node.getElementsByTagName("price").item(0)
                                .getTextContent()).isEqualTo(
                        df.format(p.getPrice()).toString());
                assertThat(
                        node.getElementsByTagName("currency").item(0)
                                .getTextContent()).isEqualTo(CURRENCY);
                NodeList list = node
                        .getElementsByTagName("attribute");
                int attrMatches = 0;
                for (int i = 0; i < list.getLength(); i++) {
                    for (Attribute att : p.getAttributes()) {
                        if (list.item(i).getTextContent()
                                .equalsIgnoreCase(att.getValue())) {
                            attrMatches++;
                            break;
                        }
                    }
                }
                assertThat(attrMatches).isEqualTo(p.getAttributes().size());

            }
        });
    }

    @Test
    public void getOneArticleXml_NotModified() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            @SuppressWarnings("rawtypes")
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
                Product p = createNewProduct();

                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken(), "since",
                        String.valueOf(since) };
                WS.Response response = callApi(ARTICLES_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo(
                        "text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                NodeList nodes = dom.getElementsByTagName("article");
                assertThat(nodes.getLength()).isEqualTo(1);

            }
        });
    }

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
                assertThat(response.getHeader("Content-Type")).isEqualTo(
                        "text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                NodeList nodes = dom.getElementsByTagName("customer");
                int count = User.find.all().size();
                assertThat(nodes.getLength()).isEqualTo(count);

            }
        });
    }

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

    @Test
    public void getOneCustomerXml() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            @SuppressWarnings("rawtypes")
            public void run() {
                User user = User.find.byId(1);
                Address address = user.getAddresses().get(0);
                String[] params = new String[] { "id", "1", "token",
                        getUserActiveAdminUser().getToken() };
                WS.Response response = callApi(CUSTOMERL_URL, params, null);
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo(
                        "text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                Element node = (Element) dom.getElementsByTagName("customer")
                        .item(0);

                assertThat(node.getAttribute("id")).isEqualTo(
                        user.getId().toString());
                assertThat(
                        node.getElementsByTagName("name").item(0)
                                .getTextContent()).isEqualTo(
                        address.getLastname());
                assertThat(
                        node.getElementsByTagName("firstname").item(0)
                                .getTextContent()).isEqualTo(
                        address.getFirstname());
                assertThat(
                        node.getElementsByTagName("email").item(0)
                                .getTextContent())
                        .isEqualTo(address.getEmail());
                assertThat(
                        node.getElementsByTagName("birthdate").item(0)
                                .getTextContent())
                        .isEqualTo(
                                String.valueOf(address.getBirthday().getTime() / 1000L));

                node = (Element) node.getElementsByTagName("address").item(0);
                assertThat(node.getAttribute("id")).isEqualTo(
                        address.getId().toString());
                assertThat(node.getAttribute("type")).isEqualTo("1");
                assertThat(
                        node.getElementsByTagName("city").item(0)
                                .getTextContent())
                        .isEqualTo(address.getPlace());
                assertThat(
                        node.getElementsByTagName("street").item(0)
                                .getTextContent()).isEqualTo(
                        address.getStreet());
                assertThat(
                        node.getElementsByTagName("postcode").item(0)
                                .getTextContent()).isEqualTo(
                        address.getZip().toString());
                assertThat(
                        node.getElementsByTagName("country").item(0)
                                .getTextContent()).isEqualTo(
                        address.getCountry().getName());

            }
        });
    }

    @Test
    public void getOneCustomerXml_NotModified() {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            @SuppressWarnings("rawtypes")
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

    //@Test
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
                User user = getNewUser(true, true);

                String[] params = new String[] { "id", "all", "token",
                        getUserActiveAdminUser().getToken(), "since",
                        String.valueOf(since) };
                WS.Response response = callApi(CUSTOMERL_URL, params, null);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Content-Type")).isEqualTo(
                        "text/xml");
                assertThat(response.getHeader("Etag")).isNotEmpty();

                // Get Xml dom and check for products
                Document dom = response.asXml();
                NodeList nodes = dom.getElementsByTagName("customer");
                assertThat(nodes.getLength()).isEqualTo(1);

            }
        });
    }

    // TODO: Here

    // TODO: Here

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
    private WS.Response callApi(final String url, final String[] queryParams,
            final String[] headers) {
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
        return User.find.where().eq("isActive", true)
                .eq("type", UserType.find.byId("admin")).findList().get(0);
    }

    private Cart createCart(int[][] products, User user) {
        Cart cart = new Cart();
        cart.setUser(user);
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
