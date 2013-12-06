package controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import models.Address;
import models.Attribute;
import models.Cart;
import models.CartHasProduct;
import models.Product;
import models.User;

import org.apache.commons.codec.digest.DigestUtils;

import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import utils.JsonSerializer;
import views.xml.xml.customers;
import views.xml.xml.orders;
import views.xml.xml.products;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.databind.JsonNode;

import customactions.CustomLogger;
import customactions.LogAction;
import customactions.LogLevel;
import customactions.TokenAuthenticator;

/**
 * API interface to LFH Mave Project.
 * 
 * @author Sandro Dallo
 * 
 */
public class Api extends Controller {

	private static final int ALL_ITEMS = Integer.MIN_VALUE;
	private static final String VERSION = "1.0";
	private static final CustomLogger logger = new CustomLogger();

	/**
	 * Returns a list of articles or only one article.
	 * 
	 * @param id
	 *            Id of the article or string 'all'
	 * @param since
	 *            Get all articles since the passed date (unix timestamp)
	 * @return XML or Json list of articles (depends on query parameter type)
	 */
	@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result articles(final String id, final String since) {
		return getItems(id, since, Product.class);
	}

	@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result customers(final String id, final String since) {
		return getItems(id, since, User.class);
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	private static String getEtagOfItems(final List items, final Class exType)
			throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException {
		String eTag = exType.getSimpleName();
		eTag = getNewestDateOfItems(items, exType).toString() + eTag;
		return DigestUtils.shaHex(eTag);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Result getItems(final String id, final String since,
			final Class exType) {
		// Is necessary because type is valid scala type and hence the route definition causes a compilation error
		final String type = request().getQueryString("type") == null ? "xml"
				: request().getQueryString("type");
		final String etag = request().getHeader("If-None-Match");
		// Return if request type is unsupported
		if (!(type.equals("xml") || type.equals("json")))
			return badRequest(Messages.get("api.request.unsupported"));
		Date dt;
		int getId;
		try {
			getId = id.equalsIgnoreCase("all") ? ALL_ITEMS : Integer
					.parseInt(id);
			dt = since == null ? null : new Date(1000 * Long.parseLong(since));

			// Get items from the database
			final List items = getItemsFromDb(getId, dt, exType);
			if (items.size() == 0)
				return notFound(Messages.get("api.request.resourcenotfound"));

			// Get etag and set headers
			final String currentTag = getEtagOfItems(items, exType);
			response().setHeader(ETAG, currentTag);
			response().setHeader(CONTENT_LANGUAGE, "UTF-8");
			if (etag != null && etag.equals(currentTag))
				return status(304, Messages.get("api.request.notmodifiedfound"));

			// Return the appropriate result
			if (type.equals("json"))
				return jsonResult(items, exType);
			else
				return xmlResult(items, exType);

		} catch (final Exception e) {
			logger.logToApiDb(Controller.ctx(), String.format(
					Messages.get("api.request.unsupported"), id, since));
			logger.logToFile(
					e.getMessage() + " => "
							+ Arrays.toString(e.getStackTrace()),
					LogLevel.ERROR, "api");
			return internalServerError(String.format(
					Messages.get("api.request.unsupported"), id, since));
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List getItemsFromDb(final int getId, final Date dt,
			final Class exType) {
		if (getId == ALL_ITEMS && dt == null)
			return Ebean.find(exType).findList();

		ExpressionList query = Ebean.find(exType).where();
		if (getId != ALL_ITEMS)
			query = query.eq("id", getId);
		if (dt != null)
			query = query.ge("updated_at", dt);
		return query.findList();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Date getNewestDateOfItems(final List items,
			final Class exType) throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException {
		Date tag = new Date(0);
		for (final Object item : items) {

			final Date cDate = (Date) exType.getMethod("getCreatedAt").invoke(
					item);
			final Date uDate = (Date) exType.getMethod("getUpdatedAt").invoke(
					item);

			if (cDate != null && cDate.after(tag))
				tag = cDate;
			if (uDate != null && uDate.after(tag))
				tag = uDate;

			// Poor performance, must be optimized. Maybe update parent date in db
			Date childDate;
			if (exType == Product.class) {
				childDate = getNewestDateOfItems(
						((Product) item).getAttributes(), Attribute.class);
				tag = childDate.after(tag) ? childDate : tag;
			}
			if (exType == User.class) {
				childDate = getNewestDateOfItems(((User) item).getAddresses(),
						Address.class);
				tag = childDate.after(tag) ? childDate : tag;
			}
			if (exType == Cart.class) {
				childDate = getNewestDateOfItems(
						((Cart) item).getCartHasProduct(), CartHasProduct.class);
				tag = childDate.after(tag) ? childDate : tag;
			}
		}
		return tag;
	}

	@SuppressWarnings("rawtypes")
	private static <T> Result jsonResult(final List<T> items, final Class exType)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		response().setHeader(CONTENT_TYPE, "application/json");
		JsonNode result = null;
		if (exType == Product.class)
			result = JsonSerializer.getInstance().getJsonObject(items,
					"articles");
		if (exType == User.class)
			result = JsonSerializer.getInstance().getJsonObject(items,
					"customers");
		if (exType == Cart.class)
			result = JsonSerializer.getInstance()
					.getJsonObject(items, "orders");
		return ok(result);
	}

	@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result orders(final String id, final String since) {
		return getItems(id, since, Cart.class);
	}

	@TokenAuthenticator
	@LogAction(value = "api", logLevel = LogLevel.DEBUG)
	public static Result version() {
		return ok(VERSION);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Result xmlResult(final List items, final Class exType) {
		// Set headers
		response().setHeader(CONTENT_TYPE, "text/xml");
		String result = "";
		if (exType == Product.class)
			result = products.render(items).toString();
		if (exType == User.class)
			result = customers.render(items).toString();
		if (exType == Cart.class)
			result = orders.render(items).toString();

		return ok(result.replaceFirst("\\s+\\n", ""));
	}

}
