package controllers;

import customaction.LogAction;
import customaction.LogLevel;
import play.Logger;
import play.Logger.ALogger;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

public class Api extends Controller {

	private static final short CUSTOMERS = 1;
	private static final short ORDERS = 2;
	private static final short ARTICLES = 4;
	private static final String VERSION = "1.0";
	private static final ALogger logger = Logger.of("api");

	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result articles(final String id, final String datetime) {
		return getItems(id, datetime, ARTICLES);
	}

	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result customers(final String id, final String datetime) {
		return getItems(id, datetime, CUSTOMERS);
	}

	private static Result getItems(final String id, final String datetime,
			final short exType) {
		// Is necessary because type is valid scala type and hence the route definition causes a compilation error
		String type = request().getQueryString("type");
		request().getHeader("If-None-Match");
		// Return if request type is unsupported
		if (type != null && !(type.equals("xml") || type.equals("json")))
				return badRequest(Messages.get("api.request.unsupported"));

		return ok("Api geht");
	}

	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result orders(final String id, final String datetime) {
		return getItems(id, datetime, ORDERS);
	}

	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result version() {
		return ok(VERSION);
	}
	
}
