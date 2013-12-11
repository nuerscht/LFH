package customactions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.LogApi;
import models.LogLogin;
import models.User;
import play.Logger;
import play.Logger.ALogger;
import play.mvc.Http.Context;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.Request;
import utils.SessionSerializer;

/**
 * Simple class to log to db and file
 *
 * @author Sandro Dallo
 */
public class CustomLogger {

    private final ALogger logger = Logger.of("application");
    private final int MAX_INFO = 255;
    private final int MAX_PARAMS = 255;
    private final int MAX_URL = 255;

    private User extractUser(final Context ctx) {
        User user = (User) ctx.args.get("token_user");
        if (user == null && ctx.session().get("user") != null)
            user = SessionSerializer.<User>deserialize(ctx.session().get("user").getBytes());
        return user;
    }

    private String getHeaderString(final Map<String, String[]> headers,
                                   final String delimiter) {
        final StringBuilder sb = new StringBuilder();
        for (final Entry<String, String[]> entry : headers.entrySet()) {
            sb.append(String.format("%s -> Value: %s " + delimiter,
                entry.getKey(), getString(entry.getValue())));
        }

        String retVal = sb.toString();
        if (retVal.lastIndexOf(delimiter) > 0)
            retVal = retVal.substring(0, retVal.lastIndexOf(delimiter));
        return retVal;
    }

    private String getParameters(final Request request, final String delimiter) {
        final StringBuilder sb = new StringBuilder();
        final Map<String, String[]> map = new HashMap<String, String[]>();
        map.putAll(request.queryString());
        List<FilePart> files = null;

        if (request.method().equalsIgnoreCase("post")) {
            final String cType = request.getHeader("Content-Type");
            if (request.body() != null) {
                if (cType.equalsIgnoreCase("application/x-www-form-urlencoded")) {
                    final Map<String, String[]> body = request.body()
                        .asFormUrlEncoded();
                    if (body != null)
                        map.putAll(body);
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
        }

        for (final Entry<String, String[]> entry : map.entrySet()) {
            sb.append(String.format("%s -> Value: %s " + delimiter,
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

    /**
     * Write an entry to the api log.
     *
     * @param ctx Http context
     */
    public void logToApiDb(final Context ctx) {
        try {
            final User user = extractUser(ctx);
            logToApiDb(user, getHeaderString(ctx.request().headers(), ", "),
                getParameters(ctx.request(), ", "), ctx.request().path());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Write an entry to the api log.
     *
     * @param ctx  Http context
     * @param info The message to write to the db
     */
    public void logToApiDb(final Context ctx, final String info) {
        try {
            final LogApi log = new LogApi();
            log.setUser(extractUser(ctx));
            log.setInfo(getMaxStringLength(info, MAX_INFO));
            log.setRequestUri(getMaxStringLength(ctx.request().path(), MAX_URL));
            log.setParams(getMaxStringLength(getParameters(ctx.request(), " "),
                MAX_PARAMS));
            log.save();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Write an entry to the api log.
     *
     * @param user         The calling user
     * @param info         The message to write to the db
     * @param params       Request parameters
     * @param requestedUrl Requested url
     */
    public void logToApiDb(final User user, final String info,
                           final String params, final String requestedUrl) {
        try {

            final LogApi log = new LogApi();
            log.setUser(user);
            log.setInfo(getMaxStringLength(info, MAX_INFO));
            log.setRequestUri(getMaxStringLength(requestedUrl, MAX_URL));
            log.setParams(getMaxStringLength(params, MAX_PARAMS));
            log.save();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Write generic entry to the specified log
     *
     * @param ctx   Http context
     * @param level Log level of the message
     * @param log   A configured log file name
     */
    public void logToFile(final Context ctx, final LogLevel level,
                          final String log) {
        final String msg = String
            .format("Requested URL: %s \nPassed parameters:\n%s \nUsername: %s \n\nHeaders:\n%s",
                ctx.request().path(),
                getParameters(ctx.request(), "\n"),
                ctx.session().get("username"),
                getHeaderString(ctx.request().headers(), "\n"));
        logToFile(msg, level, log);

    }

    /**
     * Write entry to the specified log
     *
     * @param msg   The message to write
     * @param level Log level of the message
     * @param log   A configured log file name
     */
    public void logToFile(final String msg, final LogLevel level,
                          final String log) {
        final ALogger clogger = Logger.of(log);

        switch (level) {
            case DEBUG:
                clogger.debug(msg);
                break;
            case ERROR:
                clogger.error(msg);
                break;
            case INFO:
                clogger.info(msg);
                break;
            case TRACE:
                clogger.trace(msg);
                break;
            case WARN:
                clogger.warn(msg);
                break;
            default:
                break;
        }
    }

    /**
     * Write an entry to the login log
     *
     * @param ctx Http context
     */
    public void logToLoginDb(final Context ctx) {
        try {
            final User user = extractUser(ctx);
            logToLoginDb(user, String.format("URL: %s | IP: %s | Host: %s ",
                ctx.request().path(), ctx.request().remoteAddress(), ctx
                .request().host()));
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void logToLoginDb(final Context ctx, String msg) {
        try {
            final User user = extractUser(ctx);
            logToLoginDb(user, msg);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Write an entry to the login log
     *
     * @param user The calling user
     * @param info The message to write to the db
     */
    public void logToLoginDb(final User user, final String info) {
        try {
            final LogLogin log = new LogLogin();
            log.setUser(user);
            log.setInfo(getMaxStringLength(info, MAX_INFO));
            log.save();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String getMaxStringLength(String msg, int max) {
        String ret = "";
        try {
            if (msg.length() > max)
                ret = msg.substring(0, max);
            else
                ret = msg;

        } catch (Exception e) {
            logger.error("Error while truncate string.", e);
        }
        return ret;
    }

}
