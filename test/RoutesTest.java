import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.H2Platform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.fasterxml.jackson.databind.JsonNode;

import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

/**
 * 
 * Simple (JUnit) tests that can call all parts of a play app. If you are
 * interested in mocking a whole application, see the wiki for more details.
 * 
 */
public class RoutesTest {

	@Test
	public void checkIndex() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				Result result = routeAndCall(fakeRequest(GET, "/"));
				assertThat(contentType(result)).isEqualTo("text/html");
				assertThat(status(result)).isEqualTo(200);
			}
		});
	}

	@Test
	public void checkProducts() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				Result result = routeAndCall(fakeRequest(GET, "/products"));
				assertThat(contentType(result)).isEqualTo("text/html");
				assertThat(status(result)).isEqualTo(200);
			}
		});
	}

	@Test
	public void checkSearch() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				Result result = routeAndCall(fakeRequest(GET, "/search"));
				assertThat(contentType(result)).isEqualTo("text/html");
				assertThat(status(result)).isEqualTo(200);
			}
		});
	}
	
	@Test
	public void checkSearchComplex() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				Result result = routeAndCall(fakeRequest(GET, "/search?q=Test&s=Description&o=desc&p=1"));
				assertThat(contentType(result)).isEqualTo("text/html");
				assertThat(status(result)).isEqualTo(200);
			}
		});
	}

	@Test
	public void checkDetails() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				Result result = routeAndCall(fakeRequest(GET, "/details/1"));
				assertThat(contentType(result)).isEqualTo("text/html");
				assertThat(status(result)).isEqualTo(200);
			}
		});
	}
}
