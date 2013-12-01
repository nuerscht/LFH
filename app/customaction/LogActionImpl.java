/**
 * Package with all custom play actions for eshomo web shop.
 */
package customaction;


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
	
	 private ALogger appLogger = Logger.of("application");
		
	/**
	 * Writes a log entry to the specified application log as configured in the annotation. 
	 */
	@Override
	public Promise<SimpleResult> call(Context ctx) throws Throwable {
		
		logToFile(ctx);
		if(configuration.value().equalsIgnoreCase("api"))
			logToApiDb(ctx);
		if(configuration.value().equalsIgnoreCase("login"))
			logToLoginDb(ctx);
		
		return delegate.call(ctx);
	}

	private void logToLoginDb(Context ctx) {
		try {
			LogLogin log = new LogLogin();
			User user = User.find.where()
					.eq("email", ctx.session()
							.get("username").toString())
					.findUnique();
			log.setUser(user);
			log.setInfo(String.format("URL: %s | IP: %s | Host: %s ",
					ctx.request().path(),
					ctx.request().remoteAddress(),
					ctx.request().host())
					);
			log.save();
		} catch (Exception e) {
			appLogger.error(e.getMessage(),e);
		}		
	}

	private void logToApiDb(Context ctx) {
		try {
			LogApi log = new LogApi();
			User user = User.find.where()
					.eq("email", ctx.session()
							.get("username"))
					.findUnique();
			log.setUser(user);
			log.setInfo(getHeaderString(ctx.request().headers(), ", "));
			log.setRequestUri(ctx.request().path());	
			log.setParams(getParameters(ctx.request(), ", "));
			log.save();
		} catch (Exception e) {
			appLogger.error(e.getMessage(),e);
		}		
		
	}

	private void logToFile(Context ctx) {
		ALogger logger = Logger.of(configuration.value());
		LogLevel level = configuration.logLevel();
		String msg = String.format("Requested URL: %s \nPassed parameters:\n%s \nUsername: %s \n\nHeaders:\n%s", 
				ctx.request().path(), getParameters(ctx.request(),"\n"),ctx.session().get("username"),getHeaderString(ctx.request().headers(),"\n"));
		
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

	private String getParameters(Request request, String delimiter) {
		StringBuilder sb = new StringBuilder();
		Map<String,String[]> map = request.queryString();
		List<FilePart> files = null;
		
		if(request.method().equalsIgnoreCase("post")){
			String cType = request.getHeader("Content-Type");
			if(cType.equalsIgnoreCase("application/x-www-form-urlencoded")){
					map.putAll(request.body().asFormUrlEncoded());			
			}
			if(cType.equalsIgnoreCase("multipart/form-data")){
				MultipartFormData body = request.body().asMultipartFormData();
				if(body != null){
					map.putAll(body.asFormUrlEncoded());
					files = body.getFiles();
				}
			}
		}
		
		for (Entry<String, String[]> entry : map.entrySet()) {
			sb.append(String.format("Parameter: %s -> Value: %s " + delimiter, 
					entry.getKey(),
					getString(entry.getValue())
					));
		}
		
		if(files != null){
			for (FilePart filePart : files) {
				sb.append(String.format("File: %s \n", filePart.getFilename()));
			}
		}
		
		String retVal = sb.toString();
		
		return retVal.substring(0, retVal.lastIndexOf(delimiter));
	}


	private String getHeaderString(Map<String, String[]> headers, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String[]> entry : headers.entrySet()) {
			sb.append(String.format("Header: %s -> Value: %s " + delimiter, 
					entry.getKey(),
					getString(entry.getValue())
					));
		}
		
		String retVal = sb.toString();		
		return retVal.substring(0, retVal.lastIndexOf(delimiter));
	}
	
	private Object getString(String[] value) {
		StringBuilder sb = new StringBuilder();
		for (String string : value) {
			sb.append(String.format("%s ", string));
		}
		return sb.toString();
	}

}
