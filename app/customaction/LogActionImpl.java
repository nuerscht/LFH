/**
 * Package with all custom play actions for eshomo web shop.
 */
package customaction;


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		
	/**
	 * Writes a log entry to the specified application log as configured in the annotation. 
	 */
	@Override
	public Promise<SimpleResult> call(Context ctx) throws Throwable {
		
		ALogger logger = Logger.of(configuration.value());
		LogLevel level = configuration.logLevel();
		String msg = String.format("Requested URL: %s \nPassed parameters:\n%s \nUsername: %s \n\nHeaders:\n%s", 
				ctx.request().path(), getParameters(ctx.request()),ctx.request().username(),getHeaderString(ctx.request().headers()));
		
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

		return delegate.call(ctx);
	}

	private String getParameters(Request request) {
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
			sb.append(String.format("Parameter: %s -> Value: %s \n", 
					entry.getKey(),
					getString(entry.getValue())
					));
		}
		
		if(files != null){
			for (FilePart filePart : files) {
				sb.append(String.format("File: %s \n", filePart.getFilename()));
			}
		}
		
		return sb.toString();
	}


	private String getHeaderString(Map<String, String[]> headers) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String[]> entry : headers.entrySet()) {
			sb.append(String.format("Header: %s -> Value: %s \n", 
					entry.getKey(),
					getString(entry.getValue())
					));
		}
		
		return sb.toString();
	}
	
	private Object getString(String[] value) {
		StringBuilder sb = new StringBuilder();
		for (String string : value) {
			sb.append(String.format("%s ", string));
		}
		return sb.toString();
	}

}
