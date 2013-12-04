package controllers;

import models.Address;
import models.User;
import play.data.DynamicForm;

/**
 * contains shared user funcionality
 * @author boe
 *
 */
public class UserData extends Eshomo {
	/**
	 * check if the entered password is valid
	 * @param address
	 * @param user
	 * @param bindedForm
	 * @return message if an error occured
	 */
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
