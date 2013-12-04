package controllers;

import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;

import models.Address;
import models.Attribute;
import models.Cart;
import models.Product;
import models.User;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.xml.xml.products;
import customactions.CustomLogger;
import customactions.LogAction;
import customactions.LogLevel;
import customactions.TokenAuthenticator;


public class Api extends Controller {


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
		return getItems(id, datetime, Product.class);
	}

	@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result customers(final String id, final String datetime) {
		return getItems(id, datetime, User.class);
	}

	private static Result getItems(final String id, final String datetime,
			final Class exType) {
		// Is necessary because type is valid scala type and hence the route definition causes a compilation error
		String type = request().getQueryString("type") == null ? "xml" : request().getQueryString("type") ;
		String etag = request().getHeader("If-None-Match");
		// Return if request type is unsupported
		if (!(type.equals("xml") || type.equals("json")))
				return badRequest(Messages.get("api.request.unsupported"));
		Date dt;
		int getId;
		try {
			 getId = id.equalsIgnoreCase("all") ? ALL_ITEMS : Integer.parseInt(id);
			 dt = datetime == null ? null : new Date(1000*Long.parseLong(datetime));
			 
			 List items = getItemsFromDb(getId,dt,exType);
			 
			 String currentTag = getEtagOfItems(items,exType);			 
			 
			 return null;
			 
		} catch (NumberFormatException e) {
			logger.logToApiDb(Controller.ctx() , String.format(Messages.get("api.request.unsupported"),id,datetime));
			return badRequest( String.format(Messages.get("api.request.unsupported"),id,datetime));
		}	
			
	}



	private static long getEtagOfItems(List items,Class exType) {
		long tag = Long.MAX_VALUE;
		for (Object item : items) {
			long id = (long) exType.getField("id").get(item);
			Date cDate = (Date) exType.getField("created_at").get(item);
			Date uDate = (Date) exType.getField("updated_at").get(item);
			tag = tag ^ id;
			if(cDate != null)
				tag = tag ^ (cDate.getTime() << 32);
			if(uDate != null)
				tag = tag ^ (uDate.getTime() >> 32);
			
			if(exType == Product.class){
				tag = tag ^ getEtagOfItems(((Product)item).getAttributes(), Attribute.class);
			}
			if(exType == User.class){
				tag = tag ^ getEtagOfItems(((User)item).getAddresses(), Address.class);
			}
			if(exType == Cart.class){
				tag = tag ^ getEtagOfItems(((Cart)item).get, Address.class);
			}
				
			
				
		}
		return null;
	}

	@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)	
	public static Result orders(final String id, final String datetime) {
		return getItems(id, datetime, Cart.class);
	}
	
	//@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result version() {
		//return ok(VERSION);
		response().setHeader(CONTENT_TYPE, "text/xml");
		return ok(products.render("test").toString().replaceFirst("\\s+\\n", ""));
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List getItemsFromDb(int getId, Date dt, Class exType) {
		if(getId == ALL_ITEMS && dt == null)
			return Ebean.find(exType).findList();
		
		ExpressionList query = Ebean.find(exType).where();
		if(getId != ALL_ITEMS)
			query = query.eq("id", getId);
		if(dt != null)
			query  = query.ge("created_at", dt);
		return query.findList();
	}

}
