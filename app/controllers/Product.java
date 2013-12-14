package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;

import org.apache.commons.io.FileUtils;

import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.product.*;

public class Product extends Eshomo {

    public static final int NUM_PRODUCTS_PER_PAGE = 20;
    // An unbound rating form
    final static Form<Rating> ratingForm = Form.form(Rating.class);
    final static Form<models.Product> productForm = Form.form(models.Product.class);
    final static Form<Image> imageForm = Form.form(Image.class);
    
    /**
     * @return Product overview
     */
    public static Result products() {
        List<models.Product> productList = models.Product.find.all();
        return ok(views.html.product.products.render(productList));
    }

    /**
     * @return Product overview by tag
     */
    public static Result productsByTag(String tagId) {
        models.Tag tag = models.Tag.find.byId(tagId);
        List<models.Product> products = tag.getProducts();
        return ok(views.html.product.products.render(products));
    }

    /**
     * @param id Id of Produt to show
     * @return Product detail page or error
     */
    public static Result details(Integer id, Integer imageIndex) {
        Form<Rating> form = Form.form(Rating.class);
        models.Product product = models.Product.find.byId(id);

        if (product != null && (imageIndex == 0 || product.getImages().size() > imageIndex)) {
            return ok(details.render(product, imageIndex, product.getRatings(), form));
        } else {
            return badRequest();
        }
    }

    /**
     * @param page   page to display
     * @param sortBy field name to sort by
     * @param order  asc or desc ordering
     * @param query  filter text
     * @return A page with a list of products found
     */
    public static Result search(int page, String sortBy, String order, String query) {
        return ok(search.render(models.Product.page(page, NUM_PRODUCTS_PER_PAGE, sortBy, order, query)));
    }

    /**
     * @param id of the product to rate
     * @return product details with errors if any
     */
    public static Result submitRating(Integer id) {
        models.Product product = models.Product.find.byId(id);
        if (product == null) {
            return badRequest();
        }

        Form<Rating> form = Form.form(Rating.class).bindFromRequest();

        if (form.hasErrors() || !isLoggedIn()) {
            return badRequest(details.render(product, 0, product.getRatings(), form));
        } else {
            Rating rating = form.get();
            rating.setProduct(product);
            rating.setUser(getUserObj());
            try {
                Ebean.beginTransaction();
                Ebean.save(rating);
                Ebean.commitTransaction();
            } finally {
                Ebean.endTransaction();
            }
            return ok(
                details.render(product, 0, product.getRatings(), ratingForm)
            );
        }
    }

    /**
     * @return admin list view of products
     */
    public static Result list() {
    	String keyword = Form.form().bindFromRequest().get("keyword");
    	List<models.Product> products;
    	if(keyword != null){
    		products = models.Product.find.where(Expr.like("Title", "%" + keyword + "%")).findList();
    	} else{
    		products = models.Product.find.all();
    	}
        return ok(adminlist.render(products));
    }

    /**
     * @return Empty form to add new product
     */
    public static Result add() {
        Form<models.Product> productForm = Form.form(models.Product.class);

        return ok(productform.render(productForm, imageForm, 0, null, "Produkt erfassen", ""));
    }

    /**
     * @param id of the product
     * @return filled form to edit product
     */
    public static Result edit(Integer id) {
        Form<Image> imageForm;
        models.Product product = models.Product.find.byId(id);
        Form<models.Product> productForm = Form.form(models.Product.class).fill(product);
        //Form<Tags> tagForm = Form.form(Tags.class).fill(new Tags(product.getTags()));
        if (product.hasImage()) {
            imageForm = Form.form(Image.class).fill(product.getImages().get(0));
        } else {
            imageForm = Product.imageForm;
        }

        return ok(productform.render(productForm, imageForm, product.getId(), product, "Produkt editieren", ""));
    }

    /**
     * Save or update product
     *
     * @param id of the product
     * @return
     */
    public static Result save(Integer id) {
        // Get form from request
        Form<models.Product> form = Form.form(models.Product.class).bindFromRequest();
        Form<Image> imageForm = Form.form(Image.class).bindFromRequest();
        DynamicForm dynForm = Form.form().bindFromRequest();
        String message = "";

        // Get the models and data of the form
        models.Product product = form.get();
        FilePart imageFile = request().body().asMultipartFormData().getFile("image");
        Image image = null;

        models.Product oldProduct = models.Product.find.byId(id);

        if (!(isLoggedIn() && isAdminUser())) {
            return forbidden();
        }

        if (form.hasErrors()) {
            return badRequest(productform.render(form, imageForm, id, product, "Produkt editieren", "Formular ungültig"));
        } else {

            Ebean.beginTransaction();
            try {
            	// Process tags
            	List<models.Tag> formTags = product.getTags();
            	product.setTags(new ArrayList<models.Tag>());
            	
            	for(models.Tag tag : formTags){
            		if((tag.getId() != null) && !(tag.getId().equals(""))){
            			product.addTag(models.Tag.getOrCreate(tag));
            		}
            	}
            	
                if (imageFile != null) {
                    String extension = imageFile.getFilename().substring(imageFile.getFilename().lastIndexOf("."));
                    image = new Image();
                    image.setName(dynForm.get("name"));
                    image.setDescription(dynForm.get("image-description"));
                    image.setExtension(extension);
                    product.addImage(image);

                    // Update old image if existing
                } else if (oldProduct != null && oldProduct.getImages().size() > 0) {
                    Image oldImage = oldProduct.getImages().get(0);
                    oldImage.setName(dynForm.get("name"));
                    oldImage.setDescription(dynForm.get("image-description"));
                    product.addImage(oldImage);
                }

                if (id != 0) {
                    product.setId(id);
                    Ebean.update(product);
                } else {
                    Ebean.save(product);
                }
                product.saveManyToManyAssociations("tags");

                //Finally move image as we know the id
                if (image != null) {
                	try{
                		FileUtils.moveFile(imageFile.getFile(), new File("public/" + play.Play.application().configuration()
                				.getString("eshomo.upload.directory"), image.getId() + image.getExtension()));
                	}catch(IOException e){
                		return internalServerError();
                	}
                }

                Ebean.commitTransaction();
            } finally {
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
            cart.addProduct(product);
        }

        return redirect(routes.Product.details(id, 0));
    }
}
