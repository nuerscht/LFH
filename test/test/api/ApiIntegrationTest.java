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
import java.util.zip.GZIPInputStream;

import models.User;
import models.UserType;

import org.junit.Test;
import org.yaml.snakeyaml.reader.StreamReader;

import play.libs.WS;
import play.libs.WS.WSRequestHolder;
import play.test.FakeApplication;
import play.test.Helpers;
import eshomo.EshomoTest;

public class ApiIntegrationTest extends EshomoTest {

	// private constant fields
	private final String customers_url = "/customers";
	private final String orders_url = "/orders";
	private final String articles_url = "/articles";
	private final String version_url = "/version";
	private final String server_url = "http://localhost:3333";

	/**
	 * Checks if call to the /customers succeeds if user is active and an admin.
	 * 
	 * @author dal
	 */
	@Test
	public void checkTokenLoginValidUser_Customer() {
		User user = getNewUser(true, true);
		String[] params = new String[] { "id", "all", "token", user.getToken() };
		checkTokenLogin(customers_url, params, 200, "<customers>");
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
		checkTokenLogin(articles_url, params, 200, "<articles>");
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
		checkTokenLogin(orders_url, params, 404,
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
		checkTokenLogin(version_url, params, 200, "1.0");
	}

	/**
	 * Checks if call to the /customers fails if user is active but not an admin.
	 * 
	 * @author dal
	 */
	@Test
	public void checkTokenLoginNonAdmin_Customer() {
		User user = getNewUser(false, true);
		String[] params = new String[] { "id", "all", "token", user.getToken() };
		checkTokenLogin(customers_url, params, 401,
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
		checkTokenLogin(articles_url, params, 401,
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
		checkTokenLogin(orders_url, params, 401, "Kein gültiges Token gefunden");
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
		checkTokenLogin(version_url, params, 401,
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
		checkTokenLogin(customers_url, params, 401,
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
		checkTokenLogin(articles_url, params, 401,
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
		checkTokenLogin(orders_url, params, 401, "Kein gültiges Token gefunden");
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
		checkTokenLogin(version_url, params, 401,
				"Kein gültiges Token gefunden");
	}

	/**
	 * Checks if call to the /customers fails if user is inactive and is not an admin.
	 * 
	 * @author dal
	 */
	@Test
	public void checkTokenLoginNonActiveAdmin_Customer() {
		User user = getNewUser(false, false);
		String[] params = new String[] { "id", "all", "token", user.getToken() };
		checkTokenLogin(customers_url, params, 401,
				"Kein gültiges Token gefunden");
	}

	/**
	 * Checks if call to the /articles fails if user is inactive and is not an admin.
	 * 
	 * @author dal
	 */
	@Test
	public void checkTokenLoginNonActiveAdmin_Articles() {
		User user = getNewUser(false, false);
		String[] params = new String[] { "id", "all", "token", user.getToken() };
		checkTokenLogin(articles_url, params, 401,
				"Kein gültiges Token gefunden");
	}

	/**
	 * Checks if call to the /orders fails if user is inactive and is not an admin.
	 * 
	 * @author dal
	 */
	@Test
	public void checkTokenLoginNonActiveAdmin_Orders() {
		User user = getNewUser(false, false);
		String[] params = new String[] { "id", "all", "token", user.getToken() };
		checkTokenLogin(orders_url, params, 401, "Kein gültiges Token gefunden");
	}

	/**
	 * Checks if call to the /version fails if user is inactive and is not an admin.
	 * 
	 * @author dal
	 */
	@Test
	public void checkTokenLoginNonActiveAdmin_Version() {
		User user = getNewUser(false, false);
		String[] params = new String[] { "id", "all", "token", user.getToken() };
		checkTokenLogin(version_url, params, 401,
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
		checkTokenLogin(customers_url, params, 400,
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
		checkTokenLogin(articles_url, params, 400,
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
		checkTokenLogin(orders_url, params, 400, "Kein gültiges Token gefunden");
	}

	/**
	 * Checks if call to the API /version fails if no token is supplied.
	 * 
	 * @author dal
	 */
	@Test
	public void checkTokenLoginNoToken_Version() {
		String[] params = new String[] { "id", "all" };
		checkTokenLogin(version_url, params, 400,
				"Kein gültiges Token gefunden");
	}

	/**
	 * Calls the API with the passed parameters and verifies the http status and content.
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
				String[] headers = new String[] { "Accept-Encoding", "gzip" };
				WS.Response response = callApi(articles_url, params, headers);
				// Check if status is okay and content is correct
				String msg = unzipBody(response.getBodyAsStream());
				assertThat(response.getStatus()).isEqualTo(200);
				assertThat(response.getHeader("Content-Type")).isEqualTo(
						"text/xml");
				assertThat(response.getHeader("Content-Encoding")).isEqualTo(
						"gzip");
				assertThat(response.getHeader("Etag")).isNotEmpty();

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
	private WS.Response callApi(final String url, final String[] queryParams,
			final String[] headers) {
		WSRequestHolder result = WS.url(server_url + url);
		for (int i = 0; i + 1 < queryParams.length; i += 2) {
			result.setQueryParameter(queryParams[i], queryParams[i + 1]);
		}
		for (int i = 0; i + 1 < headers.length; i += 2) {
			result.setHeader(headers[i], headers[i + 1]);
		}

		return result.get().get(10000);
	}

	private String unzipBody(InputStream str) {
		InputStreamReader sr = null;
		StringWriter writer = null;
		GZIPInputStream gis = null;
		try {
			char[] buffer = new char[10240];
			gis = new GZIPInputStream(str);
			sr = new InputStreamReader(gis,"UTF-8");
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
			if(writer != null)
				 writer.close();
			 if(sr != null)
				 sr.close();
			 if(gis != null)
				 gis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return null;
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
