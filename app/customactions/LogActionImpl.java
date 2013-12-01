/**
 * Package with all custom play actions for eshomo web shop.
 */
package customactions;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.LogApi;
import models.LogLogin;
import models.User;
import play.Logger;
import play.Logger.ALogger;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.Request;
import play.mvc.SimpleResult;

/**
 * @author Sandro Dallo
 * 
 */
public class LogActionImpl extends Action<LogAction> {

	private final ALogger appLogger = Logger.of("application");

	/**
	 * Writes a log entry to the specified application log as configured in the
	 * annotation.
	 */
	@Override
	public Promise<SimpleResult> call(final Context ctx) throws Throwable {

		logToFile(ctx);
		if (configuration.value().equalsIgnoreCase("api"))
			logToApiDb(ctx);
		if (configuration.value().equalsIgnoreCase("login"))
			logToLoginDb(ctx);

		return delegate.call(ctx);
	}

	private String getHeaderString(final Map<String, String[]> headers,
			final String delimiter) {
		final StringBuilder sb = new StringBuilder();
		for (final Entry<String, String[]> entry : headers.entrySet()) {
			sb.append(String.format("Header: %s -> Value: %s " + delimiter,
					entry.getKey(), getString(entry.getValue())));
		}

		String retVal = sb.toString();
		if (retVal.lastIndexOf(delimiter) > 0)
			retVal = retVal.substring(0, retVal.lastIndexOf(delimiter));
		return retVal;
	}

	private String getParameters(final Request request, final String delimiter) {
		final StringBuilder sb = new StringBuilder();
		final Map<String, String[]> map = request.queryString();
		List<FilePart> files = null;

		if (request.method().equalsIgnoreCase("post")) {
			final String cType = request.getHeader("Content-Type");
			if (cType.equalsIgnoreCase("application/x-www-form-urlencoded")) {
				map.putAll(request.body().asFormUrlEncoded());
			}
			if (cType.equalsIgnoreCase("multipart/form-data")) {
				final MultipartFormData body = request.body()
						.asMultipartFormData();
				if (body != null) {
					map.putAll(body.asFormUrlEncoded());
					files = body.getFiles();
				}
			}
		}

		for (final Entry<String, String[]> entry : map.entrySet()) {
			sb.append(String.format("Parameter: %s -> Value: %s " + delimiter,
					entry.getKey(), getString(entry.getValue())));
		}

		if (files != null) {
			for (final FilePart filePart : files) {
				sb.append(String.format("File: %s " + delimiter,
						filePart.getFilename()));
			}
		}

		String retVal = sb.toString();
		if (retVal.lastIndexOf(delimiter) > 0)
			retVal = retVal.substring(0, retVal.lastIndexOf(delimiter));
		return retVal;
	}

	private Object getString(final String[] value) {
		final StringBuilder sb = new StringBuilder();
		for (final String string : value) {
			sb.append(String.format("%s ", string));
		}
		return sb.toString();
	}

	private void logToApiDb(final Context ctx) {
		try {
			final LogApi log = new LogApi();
			final User user = User.find.where()
					.eq("email", ctx.session().get("username")).findUnique();
			log.setUser(user);
			log.setInfo(getHeaderString(ctx.request().headers(), ", "));
			log.setRequestUri(ctx.request().path());
			log.setParams(getParameters(ctx.request(), ", "));
			log.save();
		} catch (final Exception e) {
			appLogger.error(e.getMessage(), e);
		}

	}

	private void logToFile(final Context ctx) {
		final ALogger logger = Logger.of(configuration.value());
		final LogLevel level = configuration.logLevel();
		final String msg = String
				.format("Requested URL: %s \nPassed parameters:\n%s \nUsername: %s \n\nHeaders:\n%s",
						ctx.request().path(),
						getParameters(ctx.request(), "\n"),
						ctx.session().get("username"),
						getHeaderString(ctx.request().headers(), "\n"));

		switch (level) {
		case DEBUG:
			logger.debug(msg);
			break;
		case ERROR:
			logger.error(msg);
			break;
		case INFO:
			logger.info(msg);
			break;
		case TRACE:
			logger.trace(msg);
			break;
		case WARN:
			logger.warn(msg);
			break;
		default:
			break;
		}
	}

	private void logToLoginDb(final Context ctx) {
		try {
			final LogLogin log = new LogLogin();
			final User user = User.find.where()
					.eq("email", ctx.session().get("username").toString())
					.findUnique();
			log.setUser(user);
			log.setInfo(String.format("URL: %s | IP: %s | Host: %s ", ctx
					.request().path(), ctx.request().remoteAddress(), ctx
					.request().host()));
			log.save();
		} catch (final Exception e) {
			appLogger.error(e.getMessage(), e);
		}
	}

}
