package customactions;

import models.User;
import play.i18n.Messages;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.SimpleResult;

import com.avaje.ebean.Ebean;

/**
 * Validates user token returns with 401 if no valid user is found.
 *
 * @author Sandro Dallo
 */
public class TokenAuthenticatorImpl extends Action<TokenAuthenticator> {

    private final CustomLogger logger = new CustomLogger();

    @Override
    public Promise<SimpleResult> call(final Context ctx) throws Throwable {
        final String token = ctx.request().getQueryString("token");
        // Return if no token is found
        if (token == null || token.length() < 1) {
            logger.logToLoginDb(ctx, Messages.get("api.request.invalid"));
            return F.Promise.pure((SimpleResult) badRequest(Messages
                .get("api.request.invalid")));
        }
        final User user = Ebean.find(User.class)
            .where()
            .eq("token", token)
            .eq("type_id", "admin")
            .findUnique();
        // Break if no user has been found
        if (user == null)
            return F.Promise.pure((SimpleResult) unauthorized((Messages
                .get("api.request.invalid"))));

        ctx.args.put("token_user", user);
        logger.logToLoginDb(ctx, Messages.get("api.request.authenticated"));
        return delegate.call(ctx);

    }

}
