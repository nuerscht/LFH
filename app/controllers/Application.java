package controllers;

import models.*;
import play.*;
import play.mvc.*;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.config.GlobalProperties;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        User user = User.find.byId(1);
        if (user != null) {
            return ok(index.render(user.getEmail() + ": " + user.getType().getDescription()));
        } else {
            return ok(index.render("Welcome to the LFH shop"));
        }
    }

}
