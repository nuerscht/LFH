package controllers;

import java.util.List;

import com.avaje.ebean.Ebean;

import models.*;
import play.data.Form;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;
import play.mvc.Result;
import views.html.details;
import views.html.products;
import views.html.search;

public class Product extends Eshomo {
	
	public static final int NUM_PRODUCTS_PER_PAGE = 20;
	final static Form<Rating> ratingForm = Form.form(Rating.class);
	
	/**
     * @return Product overview
     */
    public static Result products() {
    	List<models.Product> productList = models.Product.find.all();
    	return ok(products.render(productList, getLoginContent()));
    }
    
    /**
     * @param id Id of Produt to show
     * @return Product detail page or error
     */
    public static Result productDetails(Integer id) {
    	Form<Rating> form = Form.form(Rating.class);
    	models.Product product = models.Product.find.byId(id);
    	if (product != null) {
            return ok(details.render(product, Rating.find.where().eq("product", product).findList(), form, getLoginContent()));
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
    	return ok(search.render(models.Product.page(page, NUM_PRODUCTS_PER_PAGE, sortBy, order, query), getLoginContent()));
    }
    
    /**
     * @param id of the product to rate
     * @return product details with errors if any
     */
    public static Result submitRating(Integer id){
    	models.Product product = models.Product.find.byId(id);
    	if( product == null){
    		return badRequest("Poduct not found!");
    	}
    	
    	Form<Rating> form = Form.form(Rating.class).bindFromRequest();
    	
    	
    	if(form.hasErrors()) {
            return badRequest(details.render(product, Rating.find.where().eq("product", product).findList(), form, getLoginContent()));
        } else {
        	Rating rating = form.get();
        	//Map data = form.get(); Ebean.find(Rating.class), Rating.find.where().eq("Product", product).findList()
            return ok(
            		details.render(product, Rating.find.where().eq("product", product).findList(), ratingForm, getLoginContent())
            );
        }
    }
}
