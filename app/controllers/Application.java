package controllers;

import java.util.List;

import models.*;
import play.*;
import play.data.*;
import play.mvc.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.config.GlobalProperties;

import views.html.*;

public class Application extends Controller {

	public static final int NUM_PRODUCTS_PER_PAGE = 20;
	final static Form<Rating> ratingForm = form(Rating.class);
	
    public static Result index() {
        User user = User.find.byId(1);
        if (user != null) {
            return ok(index.render(user.getEmail() + ": " + user.getType().getDescription()));
        } else {
            return ok(index.render("Welcome to the LFH shop"));
        }
    }
    
    public static Result products() {
    	List<Product> productList = Product.find.all();
    	return ok(products.render(productList));
    }
    
    public static Result productDetails(Integer id) {
    	Product product = Product.find.byId(id);
    	if (product != null) {
            return ok(details.render(product));
        } else {
            return notFound("Product with id " + id + " not found");
        }
    }
    
    public static Result search(int page, String sortBy, String order, String query){
    	return ok(search.render(Product.page(page, NUM_PRODUCTS_PER_PAGE, sortBy, order, query)));
    }
    
    public static class Hello {
        @Required public String name;
        @Required @Min(1) @Max(100) public Integer repeat;
        public String color;
    } 
    
    public static Result submitRating(Integer id){
    	Form<Hello> form = form(Hello.class).bindFromRequest();
    	if(form.hasErrors()) {
            return badRequest(index.render(form));
        } else {
            Hello data = form.get();
            return ok(
                hello.render(data.name, data.repeat, data.color)
            );
        }
    }

}
