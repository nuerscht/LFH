package controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import models.UserType;

import org.apache.commons.codec.binary.Base64;

import play.api.templates.Html;
import play.mvc.Controller;
import utils.SessionSerializer;
import views.html.account.login;
import views.html.account.loggedin;

/**
 * basic class for eshomo controllers
 *
 * @author boe
 */
public class Eshomo extends Controller {
    /**
     * holds the message if a problem with the login exists
     */
    protected static String loginMessage = "";

    protected static void setLoginMessage(final String message) {
        loginMessage = message;
    }

    /**
     * stores user login in session
     *
     * @param user
     * @throws
     * @author boe
     */
    protected static void userLogin(final models.User user) {
        setUserObj(user);
        session("loggedin", "1");
        long unixTime = System.currentTimeMillis() / 1000L;
        session("logintime", Long.toString(unixTime));
    }

    /**
     * clears the session if an user log out
     */
    protected static void userLogout() {
        session().clear();
    }

    /**
     * checks if the user is logged in
     *
     * @return
     */
    public static Boolean isLoggedIn() {
        if ("1".equals(session("loggedin")))
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }

    /**
     * checks if the user is admin
     *
     * @return
     */
    protected static Boolean isAdminUser() {
        if (getUserObj().getType().getId().equals(UserType.ADMIN))
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }

    /**
     * returns the id of the current logged in user
     *
     * @return
     */
    protected static Integer getLoggedInUserId() {
        models.User user = getUserObj();

        if (user != null)
            return user.getId();
        return -1;
    }

    /**
     * deserialize user object from session
     *
     * @return
     */
    protected static models.User getUserObj() {
        return SessionSerializer.<models.User>deserialize(session("user").getBytes());
    }

    /**
     * serialize user object and save it into session
     *
     * @param user
     */
    protected static void setUserObj(final models.User user) {
        session("user", SessionSerializer.serialize(user));
    }
}
