package controllers;

import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

public class Api extends Controller {

	private static final short CUSTOMERS = 1;
	private static final short ORDERS = 2;
	private static final short ARTICLES = 4;

	public static Result articles(final String id, final String datetime) {
		return getItems(id, datetime, ARTICLES);
	}

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

		return null;
	}

	public static Result orders(final String id, final String datetime) {
		return getItems(id, datetime, ORDERS);
	}
}
