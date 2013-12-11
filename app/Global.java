import java.io.File;
import java.util.List;
import java.util.Map;

import models.*;
import play.*;
import play.libs.F.Promise;
import play.libs.Yaml;
import play.api.Play;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.mvc.*;
import play.mvc.Http.*;
import views.html.error.message;
import views.html.error.notFound;

import com.avaje.ebean.*;

import static play.mvc.Results.*;

public class Global extends GlobalSettings {
	/**
	 * Is executed at application start.
	 * 
	 * @author Sandro Dallo
	 */
	public void  onStart(Application app){
		// Load initial data to the database
		loadInitData(app);

        // Load test data to the database
        loadTestData(app);
        
        // Create upload directory
        createUploadDir();
	}

    /**
     * global exception handler. renders error page
     */
	@Override
	public Promise<SimpleResult> onError(RequestHeader request, Throwable t) {
        return Promise.<SimpleResult>pure(internalServerError(
            message.render(t)
        ));
    }
	
	/**
	 * global page not found handler. renders page not found page
	 */
	@Override
	public Promise<SimpleResult> onHandlerNotFound(RequestHeader request) {
        return Promise.<SimpleResult>pure(notFound(
            notFound.render(request)
        ));
    }
	
	/**
	 * global bad request handler. 
	 */
	@Override
    public Promise<SimpleResult> onBadRequest(RequestHeader request, String error) {
        return Promise.<SimpleResult>pure(badRequest(notFound.render(request)));
    }

    @SuppressWarnings("unchecked")
	public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{GzipFilter.class};
    }


	private void loadInitData(Application app) {
		if(Ebean.find(CartStatus.class).findRowCount() == 0){
			@SuppressWarnings("unchecked")
			Map<String,List<Object>> data = (Map<String,List<Object>>)Yaml.load("data-initial.yml");
            Ebean.save(data.get("cartstatus"));
			Ebean.save(data.get("usertypes"));
            Ebean.save(data.get("users"));
            Ebean.save(data.get("countries"));
			Ebean.save(data.get("addresses"));
		}
		
	}


    private void loadTestData(Application app) {
        if(Ebean.find(Product.class).findRowCount() == 0){
            @SuppressWarnings("unchecked")
            Map<String,List<Object>> data = (Map<String,List<Object>>)Yaml.load("data-test.yml");
            Ebean.save(data.get("products"));
            Ebean.save(data.get("attributes"));
            Ebean.save(data.get("tags"));
            Ebean.save(data.get("prodTags"));
            Ebean.save(data.get("users"));
            Ebean.save(data.get("addresses"));
        }

    }
    
    private void createUploadDir(){
    	File directory = new File("public/" + play.Play.application().configuration().getString("eshomo.upload.directory"));
    	if(!directory.isDirectory()){
    		directory.mkdir();
    	}
    	
    }

}
