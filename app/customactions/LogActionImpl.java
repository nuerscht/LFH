/**
 * Package with all custom play actions for eshomo web shop.
 */
package customactions;

import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.SimpleResult;

/**
 * Custom Action to log Controller calls.
 * @author Sandro Dallo
 * 
 */
public class LogActionImpl extends Action<LogAction> {

	private final CustomLogger logger = new CustomLogger();

	/**
	 * Writes a log entry to the specified application log as configured in the
	 * annotation.
	 */
	@Override
	public Promise<SimpleResult> call(final Context ctx) throws Throwable {
		try{
		logger.logToFile(ctx, configuration.logLevel(), configuration.value());
		if (configuration.value().equalsIgnoreCase("api"))
			logger.logToApiDb(ctx);
		if (configuration.value().equalsIgnoreCase("login"))
			logger.logToLoginDb(ctx);
		} catch (Exception e){
			// Should never get here, but in case catch exception
			// Action handlers must not throw an exception
			logger.logToFile(e.getMessage() + " " + e.getClass().getName(), LogLevel.ERROR, "application");
			StringBuilder sb = new StringBuilder();
			for (StackTraceElement stack : e.getStackTrace()) {
				sb.append(stack.toString() + " " + stack.getLineNumber() + "\n");
			}
			logger.logToFile(sb.toString(), LogLevel.ERROR, "application");
		}
		return delegate.call(ctx);
	}

}
