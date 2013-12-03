package controllers;

import customactions.CustomLogger;
import customactions.LogAction;
import customactions.LogLevel;
import customactions.TokenAuthenticator;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

public class Api extends Controller {

	private static final short CUSTOMERS = 1;
	private static final short ORDERS = 2;
	private static final short ARTICLES = 4;
	private static final long NOT_SPECIFIED_DATE = Long.MIN_VALUE;
	private static final int ALL_ITEMS = Integer.MIN_VALUE;
	private static final String VERSION = "1.0";
	private static final CustomLogger logger = new CustomLogger();
	
	/**
	 * Returns a list of articles or only one article.
	 * @param id Id of the article or string 'all'
	 * @param datetime Get all articles since the passed date (unix timestamp)
	 * @return XML or Json list of articles (depends on query parameter type)
	 */	
	@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result articles(final String id, final String datetime) {
		return getItems(id, datetime, ARTICLES);
	}

	@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result customers(final String id, final String datetime) {
		return getItems(id, datetime, CUSTOMERS);
	}

	private static Result getItems(final String id, final String datetime,
			final short exType) {
		// Is necessary because type is valid scala type and hence the route definition causes a compilation error
		String type = request().getQueryString("type") == null ? "xml" : request().getQueryString("type") ;
		String etag = request().getHeader("If-None-Match");
		// Return if request type is unsupported
		if (!(type.equals("xml") || type.equals("json")))
				return badRequest(Messages.get("api.request.unsupported"));
		long dt;
		int getId;
		try {
			 getId = id.equalsIgnoreCase("all") ? ALL_ITEMS : Integer.parseInt(id);
			 dt = datetime == null ? NOT_SPECIFIED_DATE : 1000*Long.parseLong(datetime);
		} catch (NumberFormatException e) {
			logger.logToApiDb(Controller.ctx() , String.format(Messages.get("api.request.unsupported"),id,datetime));
			return badRequest( String.format(Messages.get("api.request.unsupported"),id,datetime));
		}	
		if(exType == ARTICLES)
			return getArticlesResult(etag,getId,dt,type);
		else if(exType == CUSTOMERS)
			return getCustomersResult(etag,getId,dt,type);
		else
			return getOrdersResult(etag,getId,dt,type);
		
	}



	@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)	
	public static Result orders(final String id, final String datetime) {
		return getItems(id, datetime, ORDERS);
	}
	
	@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result version() {
		return ok(VERSION);
	}
	
	private static Result getArticlesResult(String etag, int getId, long dt,
			String type) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Result getOrdersResult(String etag, int getId, long dt,
			String type) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Result getCustomersResult(String etag, int getId, long dt,
			String type) {
		// TODO Auto-generated method stub
		return null;
	}
}
