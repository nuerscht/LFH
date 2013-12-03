package controllers;

import java.util.List;

import com.avaje.ebean.Ebean;

import models.Address;
import models.User;
import models.UserType;
import play.data.DynamicForm;
import play.mvc.*;
import static play.data.Form.*;
import views.html.index;
import views.html.account.*;

/**
 * controller for user actions
 * @author boe
 *
 */
public class Account extends UserData {
	
	/**
	 * handles user login
	 * @author boe
	 * @return
	 */
	public static Result login() {
		DynamicForm bindedForm = form().bindFromRequest();

		List<User> users = Ebean.find(User.class).where().eq("email", bindedForm.get("email")).where().eq("is_active", 1).findList();
		if (users.size() == 1) {
			User user = users.get(0);
			if (user.isPasswordCorrect(bindedForm.get("password"))) {
				userLogin(user);
			} else {
				userLogout();
			}
		}
		
		return ok(
			index.render("", getLoginContent())
		);
	}
	
	/**
	 * handles user logout
	 * @author boe
	 * @return
	 */
	public static Result logout() {
		userLogout();
		return ok(
			index.render("", getLoginContent())
		);
	}
	
    /**
     * handles registration form submit
     * @author boe
     * @return page for success or error message
     */
    public static Result register() {
    	Address  address  = new Address();
    	User     user     = new User();
    	try {
    		DynamicForm bindedForm = form().bindFromRequest();
	    	fillModels(bindedForm, address, user);
    		if ("on".equals(bindedForm.get("agb"))) {
		    	
    			Result result = validateRegister(address, user, bindedForm);
    			
		    	if (result != null)
					return result;
		    	
		    	Ebean.beginTransaction();
		    	try {
		    		Ebean.save(user);
		    		Ebean.save(address);
		    		Ebean.commitTransaction();
		    	} finally {
		    		Ebean.endTransaction();
		    	}
		
		    	return ok(
		    			register.render(form(User.class).fill(user), form(Address.class).fill(address), "Ihr Konto wurde erfolgreich angelegt.", "success", getLoginContent())
		    			);
    		} else {
    			return ok (
    					register.render(form(User.class).fill(user), form(Address.class).fill(address), "Bitte AGB's aktzeptieren.", "info", getLoginContent())
    			);
    		}
    	} catch (Exception e) {
    		String errorMessage = e.getMessage();
    		if (errorMessage == null)
    			errorMessage = e.toString();
    		return ok(
	    		register.render(form(User.class).fill(user), form(Address.class).fill(address), errorMessage, "error", getLoginContent())
	    	);
    	}
    }
    
    /**
     * check if the filled in data is valid for an user registration
     * @param address
     * @param user
     * @param bindedForm
     * @return null if all is ok or an object if an validate error occured
     */
    private static Result validateRegister(Address address, User user, DynamicForm bindedForm) {
    	String message = "";
	
		//check if an active user with this email address exists
		List<User> users = Ebean.find(User.class).where().eq("email", bindedForm.get("email")).where().eq("is_active", 1).findList();
		
		if (!users.isEmpty()) {
			message = "Der Benutzername ist schon vergeben.";
		}
		
    	message = validatePassword(address, user, bindedForm);
		
		if (!message.isEmpty()) {
			return ok(
				register.render(form(User.class).fill(user), form(Address.class).fill(address), message, "info", getLoginContent())
			);
		}
				
		return null;
    }
	
	/**
	 * returns the registration form
	 * @author boe 
	 * @return page to be displayed
	 */
    public static Result registerIndex() {
		return ok(
				register.render(form(User.class), form(Address.class), "", "", getLoginContent())
		);
    }

    /**
     * fills the entered form data into the model objects
     * @param bindedForm
     * @param address
     * @param user
     */
	protected static void fillModels(DynamicForm bindedForm, Address address, User user) {
			
		user.setEmail(bindedForm.get("email"));
		
		address.setEmail(bindedForm.get("email"));
		address.setFirstname(bindedForm.get("firstname"));
		address.setLastname(bindedForm.get("lastname"));
		address.setPlace(bindedForm.get("place"));
		address.setStreet(bindedForm.get("street"));
		address.setZip(bindedForm.get("zip"));
		address.setIsActive(true);
    	
		//save to database
		user.setEmail(bindedForm.get("email"));
		user.setPassword(bindedForm.get("password"));
		
		UserType userType = UserType.find.byId(UserType.CUSTOMER);
		user.setType(userType);
		
		//set user link on address
		address.setUser(user);
	}
}
