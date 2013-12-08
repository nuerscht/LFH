package controllers;

import java.util.List;

import com.avaje.ebean.Ebean;

import models.*;
import play.data.Form;
import play.mvc.Result;
import views.html.product.*;

public class Product extends Eshomo {
	
	public static final int NUM_PRODUCTS_PER_PAGE = 20;
	// An unbound rating form
	final static Form<Rating> ratingForm = Form.form(Rating.class);
	final static Form<models.Product> productForm = Form.form(models.Product.class);
	
	/**
     * @return Product overview
     */
    public static Result products() {
    	List<models.Product> productList = models.Product.find.all();
    	return ok(products.render(productList));
    }
    
    /**
     * @param id Id of Produt to show
     * @return Product detail page or error
     */
    public static Result details(Integer id) {
    	Form<Rating> form = Form.form(Rating.class);
    	models.Product product = models.Product.find.byId(id);
    	
    	if (product != null) {
            return ok(details.render(product, product.getImages(), product.getRatings(), form));
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
    	return ok(search.render(models.Product.page(page, NUM_PRODUCTS_PER_PAGE, sortBy, order, query)));
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
            return badRequest(details.render(product, product.getImages(), product.getRatings(), form));
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
            		details.render(product, product.getImages(), product.getRatings(), ratingForm)
            );
        }
    }
    
    public static Result list(){
    	return ok(adminlist.render(models.Product.find.all()));
    }
    
    public static Result add(){
    	Form<models.Product> productForm = Form.form(models.Product.class);
    	return ok(productform.render(productForm, 0, "Produkt erfassen", ""));
    }
    
    public static Result edit(Integer id){
    	models.Product product = models.Product.find.byId(id);
    	Form<models.Product> productForm = Form.form(models.Product.class).fill(product);
    	return ok(productform.render(productForm, product.getId(), "Produkt editieren", ""));
    }
    
    public static Result save(Integer id){
    	Form<models.Product> form = Form.form(models.Product.class).bindFromRequest();
    	
    	if(!(isLoggedIn() && isAdminUser())){
    		return forbidden();
    	}
    	
    	if(form.hasErrors()){
    		return badRequest(productform.render(form, id, "Produkt editieren", "Formular ungültig"));
    	} else {
    		models.Product product = form.get();

    		Ebean.beginTransaction();
    		try{
    			if(!(id == 0)){
    				product.setId(id);
    				Ebean.update(product);
        		}else{
        			Ebean.save(product);
        		}
        		Ebean.commitTransaction();
    		} catch (Exception e){
    			System.out.println(e.toString());
        	}finally{
        		Ebean.endTransaction();
        	}
    	}
    	return list();
    }

    /**
     * Adds a product to a existing or new cart
     * 
     * @param id of the product
     * @return redirect to product details
     */
    public static Result addToCart(Integer id) {
        models.Product product = models.Product.find.byId(id);

        if (product != null) {
            models.Cart cart = models.Cart.fetchOrCreateOpenCart(getUserObj());
            product.addToCart(cart);
        }

        return redirect(controllers.routes.Product.details(id));
    }
}
