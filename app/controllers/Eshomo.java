package controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.codec.binary.Base64;

import play.api.templates.Html;
import play.mvc.Controller;
import views.html.account.login;
import views.html.account.loggedin;

/**
 * basic class for eshomo controllers
 * @author boe
 */
public class Eshomo extends Controller {
	/**
	 * stores user login in session
	 * @author boe
	 * @param user
	 * @throws  
	 */
	protected static void userLogin(final models.User user)  {
		setUserObj(user);
		session("loggedin",  "1");
		long unixTime = System.currentTimeMillis() / 1000L;
		session("logintime", Long.toString(unixTime));
	}
	
	protected static void userLogout() {
		session().clear();
	}
	
	/**
	 * generates the html for the login box
	 * @author boe
	 * @return returns the html for the @login box
	 */
	protected static Html getLoginContent() {
		if ("1".equals(session("loggedin")))
			return loggedin.render();
		else 
			return login.render();
	}
	
	/**
	 * checks if the user is logged in
	 * @return
	 */
	protected static Boolean isLoggedIn() {
		if ("1".equals(session("loggedin")))
			return Boolean.TRUE;
		else 
			return Boolean.FALSE;
	}
	
	/**
	 * returns the id of the current logged in user
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
	 * @return
	 */
	protected static models.User getUserObj() {
		try {
			byte[] b                = Base64.decodeBase64(session("user").getBytes());
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream    si = new ObjectInputStream(bi);
			return (models.User)si.readObject();
		} catch (ClassNotFoundException |IOException e) {
			System.out.println(e);
		}
		
		return null;
	}
	
	/**
	 * serialize user object and save it into session
	 * @param user
	 */
	private static void setUserObj(final models.User user) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bo);
			out.writeObject(user);
			out.flush();
			session("user",    Base64.encodeBase64String(bo.toByteArray()));
		} catch (IOException e) {
			//this exception should never occur
			System.out.println(e);
		}		
	}
}
