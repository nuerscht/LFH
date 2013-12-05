package controllers;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

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
			 
			 // Get items from the database
			 List items = getItemsFromDb(getId,dt,exType);
			 if(items.size() == 0)
				 return notFound(Messages.get("api.request.resourcenotfound"));
			 
			 // Get etag and set headers
			 String currentTag = getEtagOfItems(items,exType);
			 response().setHeader(ETAG, currentTag);
			 if(etag != null && etag.equals(currentTag))
				 return status(304,Messages.get("api.request.notmodifiedfound"));
			 
			 // Return the appropriate result 
			 if(type.equals("json"))
				 return jsonResult(items,exType);
			 else
				 return xmlResult(items,exType);
			 
		} catch (Exception e) {
			logger.logToApiDb(Controller.ctx() , String.format(Messages.get("api.request.unsupported"),id,datetime));
			logger.logToFile(e.getMessage(), LogLevel.ERROR, "api");
			return internalServerError(String.format(Messages.get("api.request.unsupported"),id,datetime));
		}	
			
	}



	private static Result xmlResult(List items, Class exType) {
		// Set headers
		response().setHeader(CONTENT_TYPE, "text/xml");
		String result;
		if(exType == Product.class)
			result = products.render((List<Product>) items).toString();
		
		
		return null;
	}

	private static Result jsonResult(List items, Class exType) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String getEtagOfItems(List items, Class exType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		String eTag = exType.getSimpleName(); 
		eTag = getNewestDateOfItems(items, exType).toString() + eTag;		 
		return DigestUtils.shaHex(eTag);
	}

	private static Date getNewestDateOfItems(List items,Class exType)
	throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
		Date tag = new Date(0);
		for (Object item : items) {
			
			Date cDate = (Date) exType.getMethod("getCreatedAt").invoke(item);
			Date uDate = (Date) exType.getMethod("getUpdatedAt").invoke(item);
			
			if( cDate != null && cDate.after(tag))
				tag = cDate;
			if( uDate != null && uDate.after(tag))
				tag = uDate;
			
			Date childDate;
			if(exType == Product.class){
				childDate = getNewestDateOfItems(((Product)item).getAttributes(), Attribute.class);
				tag = childDate.after(tag) ? childDate : tag;
			}
			if(exType == User.class){
				childDate = getNewestDateOfItems(((User)item).getAddresses(), Address.class);
				tag = childDate.after(tag) ? childDate : tag;
			}
			if(exType == Cart.class){
				childDate = getNewestDateOfItems(((Cart)item).getCartHasProduct(), CartHasProduct.class);
				tag = childDate.after(tag) ? childDate : tag;
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
		return ok(VERSION);
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
