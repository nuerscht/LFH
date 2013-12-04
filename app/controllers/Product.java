package controllers;

import java.util.List;

import com.avaje.ebean.Ebean;

import models.*;
import play.data.Form;
import play.mvc.Result;
import views.html.product.*;

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
    public static Result details(Integer id) {
    	Form<Rating> form = Form.form(Rating.class);
    	models.Product product = models.Product.find.byId(id);
    	
    	if (product != null) {
            return ok(details.render(product, product.getImages(), product.getRatings(), form, getLoginContent(), isLoggedIn()));
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
    	
    	if(form.hasErrors() || !isLoggedIn()) {
            return badRequest(details.render(product, product.getImages(), product.getRatings(), form, getLoginContent(), isLoggedIn()));
        } else {
        	Rating rating = form.get();
        	rating.setProduct(product);
        	rating.setUser(getUserObj());
        	try{
        		Ebean.beginTransaction();
        		Ebean.save(rating);
        		Ebean.commitTransaction();
        	}finally{
        		Ebean.endTransaction();
        	}
            return ok(
            		details.render(product, product.getImages(), product.getRatings(), ratingForm, getLoginContent(), isLoggedIn())
            );
        }
    }
}
