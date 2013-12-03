import java.util.List;
import java.util.Map;

import models.*;
import play.*;
import play.libs.Yaml;

import com.avaje.ebean.*;

public class Global extends GlobalSettings {
	/**
	 * Is executed at application start.
	 * 
	 * @author Sandro Dallo
	 */
	public void  onStart(Application app){
		// Load initial data to the database
		loadInitData(app);		
	}


	private void loadInitData(Application app) {
		if(Ebean.find(Product.class).findRowCount() == 0){			
			@SuppressWarnings("unchecked")
			Map<String,List<Object>> data = (Map<String,List<Object>>)Yaml.load("initial-data.yml");
			Ebean.save(data.get("products"));
			Ebean.save(data.get("attributes"));
			Ebean.save(data.get("tags"));
			Ebean.save(data.get("prodTags"));
			Ebean.save(data.get("usertypes"));
			Ebean.save(data.get("users"));
			Ebean.save(data.get("adresses"));
		}
		
	}

}
