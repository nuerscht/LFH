package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Api extends Controller {
	
	public static Result customers(String id, String datetime){
		 // Is necessary because type is valid scala type and hence the route definition causes a compilation error
		String type = request().getQueryString("type");
		type = type == null ? "xml" : type;

		if(datetime == null)
			datetime = "null";
		return ok(id + " " + type + " " + datetime);
	}
}
