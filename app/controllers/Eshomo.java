package controllers;

import models.UserType;

import play.mvc.Controller;

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
     * @param
     * @throws
     * @author boe
     */
    protected static void userLogin(final models.User user) {
        session("loggedin", "1");
        session("user_id", user.getId().toString());
        long unixTime = System.currentTimeMillis() / 1000L;
        session("logintime", Long.toString(unixTime));
    }

    /**
     * clears the session if an user log out
     */
    protected static void userLogout() {
        loginMessage = "";
        session().clear();
    }

    /**
     * checks if the user is logged in
     *
     * @return
     */
    public static Boolean isLoggedIn() {
        return "1".equals(session("loggedin"));
    }

    /**
     * checks if the user is admin
     *
     * @return
     */
    protected static Boolean isAdminUser() {
        return getLoggedInUser().isAdmin();
    }

    /**
     * returns the id of the current logged in user
     *
     * @return
     */
    protected static Integer getLoggedInUserId() {
        String userIdString = session("user_id");
        if (userIdString != null) {
            return Integer.parseInt(userIdString);
        }

        return -1;
    }

    /**
     * load user object from database
     *
     * @return
     */
    protected static models.User getLoggedInUser() {
        Integer userId = getLoggedInUserId();

        if (userId < 0) {
            throw new RuntimeException("No logged in user found.");
        }

        return models.User.find.byId(userId);
    }

    protected static boolean isLoggedInUser(models.User user) {
        return user.getId().equals(getLoggedInUser().getId());
    }
}
