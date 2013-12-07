package test.api;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.running;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.api.test.FakeApplication;
import static play.test.Helpers.*;
import eshomo.EshomoTest;
import com.avaje.ebean.*;

public class ApiIntegrationTest  {

	// private constant fields
	private User valid_user = null;
	private final String customers_url = "/customers";
	private final String orders_url = "/orders";
	private final String articles_url = "/articles";
	private final String version_url = "/version";

	@Test
	public void checkTokenLogin() {
		Logger.of("play").debug("asdasdasd");
		assertThat("bla");
	}

	@Before
	public void initialize() {
		running(fakeApplication(inMemoryDatabase("test")), new Runnable() {

			@Override
			public void run() {
				valid_user = User.find.where().eq("id", 1).findUnique();
				Logger.of("play").debug("asdasdasd");
				final String s = " ASD";
			}
		});
	}

}
