package controllers;

import play.mvc.*;

import views.html.*;

public class Application extends Eshomo {

    public static Result index() {
    	models.User user = models.User.find.byId(1);
        if (user != null) {
            return ok(index.render(user.getEmail() + ": " + user.getType().getDescription(), getLoginContent()));
        } else {
            return ok(index.render("Welcome to the LFH shop", getLoginContent()));
        }
    }
}
