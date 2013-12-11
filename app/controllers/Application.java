package controllers;

import models.*;
import play.data.*;
import play.mvc.*;

import java.util.*;

import views.html.*;
import models.Product;

public class Application extends Eshomo {

    /**
     * @return The main page
     */
    public static Result index() {
        List<Product> products = Product.find.all();
        return ok(index.render(products));
    }
}
