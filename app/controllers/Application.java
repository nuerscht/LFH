package controllers;

import java.util.List;

import models.*;
import play.*;
import play.data.*;
import play.data.validation.Constraints.*;
import play.mvc.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.config.GlobalProperties;

import views.html.*;

public class Application extends Controller {

	public static final int NUM_PRODUCTS_PER_PAGE = 20;
	final static Form<Rating> ratingForm = Form.form(Rating.class);
	
    /**
     * @return The main page
     */
    public static Result index() {
        User user = User.find.byId(1);
        if (user != null) {
            return ok(index.render(user.getEmail() + ": " + user.getType().getDescription()));
        } else {
            return ok(index.render("Welcome to the LFH shop"));
        }
    }
    
    /**
     * @return Product overview
     */
    public static Result products() {
    	List<Product> productList = Product.find.all();
    	return ok(products.render(productList));
    }
    
    /**
     * @param id Id of Produt to show
     * @return Product detail page or error
     */
    public static Result productDetails(Integer id) {
    	Form<Rating> form = Form.form(Rating.class);
    	Product product = Product.find.byId(id);
    	if (product != null) {
            return ok(details.render(product, form));
        } else {
            return notFound("Product with id " + id + " not found");
        }
    }
    
    /**
     * @param page page to display
     * @param sortBy field name to sort by
     * @param order asc or desc ordering
     * @param query filter text
     * @return A page with a list of products found
     */
    public static Result search(int page, String sortBy, String order, String query){
    	return ok(search.render(Product.page(page, NUM_PRODUCTS_PER_PAGE, sortBy, order, query)));
    }
    
    /**
     * Inline class for rating form
     */
    public static class Rating {
        @Required @Min(1) @Max(5) public Integer rate;
        public String Comment;
    } 
    
    /**
     * @param id of the product to rate
     * @return product details with errors if any
     */
    public static Result submitRating(Integer id){
    	Form<Rating> form = Form.form(Rating.class).bindFromRequest();
    	Product product = Product.find.byId(id);
    	if( product == null){
    		return badRequest("Poduct not found!");
    	}
    	if(form.hasErrors()) {
            return badRequest(details.render(product, form));
        } else {
        	//Map data = form.get();
            return ok(
            		details.render(product, ratingForm)
            );
        }
    }

}
