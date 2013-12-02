/**
 * 
 */
package controllers;

import models.Address;
import models.User;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.mvc.Controller;
import views.html.account.login;
import views.html.account.loggedin;

/**
 * @author boe
 *
 */
public class Eshomo extends Controller {
	protected static Html getLoginContent() {
		if ("1".equals(session("loggedin")))
			return loggedin.render();
		else 
			return login.render();
	}
	
	protected static Boolean isLoggedIn() {
		if ("1".equals(session("loggedin")))
			return Boolean.TRUE;
		else 
			return Boolean.FALSE;
	}
	
	protected static Integer getLoggedInUserId() {
		return Integer.decode(session("userid"));
	}

	protected static String validatePassword(Address address, User user, DynamicForm bindedForm) {
		String message = "";
		
		//check if passwords are equal
		if (bindedForm.get("password") != null && !bindedForm.get("password").equals(bindedForm.get("passwordRepeat"))) {
			message = "Die eingegebenen Passwörter stimmen nicht überein.";
		}

		//check if password is at least 8 chars
		if (bindedForm.get("password") != null && bindedForm.get("password").length() < 8) {
			message = "Passwort muss mindestens 8 Zeichen haben.";
		}
		
		return message;
	}
}
