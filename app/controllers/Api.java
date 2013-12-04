package controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;

import models.Address;
import models.Attribute;
import models.Cart;
import models.CartHasProduct;
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
	//@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result articles(final String id, final String datetime) {
		return getItems(id, datetime, Product.class);
	}

	//@TokenAuthenticator
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
			 
			 long currentTag = getEtagOfItems(items,exType);			 
			 
			 return ok(String.valueOf(currentTag));
			 
		} catch (Exception e) {
			logger.logToApiDb(Controller.ctx() , String.format(Messages.get("api.request.unsupported"),id,datetime));
			logger.logToFile(e.getMessage(), LogLevel.ERROR, "api");
			return badRequest( String.format(Messages.get("api.request.unsupported"),id,datetime));
		}	
			
	}



	private static int getEtagOfItems(List items,Class exType)
	throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
		int tag = Integer.MAX_VALUE;
		for (Object item : items) {
			int id = (int) exType.getMethod("getId").invoke(item);
			Date cDate = (Date) exType.getMethod("getCreatedAt").invoke(item);
			Date uDate = (Date) exType.getMethod("getUpdatedAt").invoke(item);
			tag = tag + id;
			if(cDate != null)
				tag = tag + cDate.hashCode();
			if(uDate != null)
				tag = tag + uDate.hashCode();
			
			if(exType == Product.class){
				tag = tag + 17 * getEtagOfItems(((Product)item).getAttributes(), Attribute.class);
			}
			if(exType == User.class){
				tag = tag + 17 * getEtagOfItems(((User)item).getAddresses(), Address.class);
			}
			if(exType == Cart.class){
				tag = tag + 17 * getEtagOfItems(((Cart)item).getCartHasProduct(), CartHasProduct.class);
			}				
		}
		return tag;
	}
	

	//@TokenAuthenticator
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
