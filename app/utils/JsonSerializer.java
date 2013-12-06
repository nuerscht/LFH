package utils;

import java.util.List;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonSerializer {
	
	private static JsonSerializer serializer;
	
	private JsonSerializer(){}
	
	private static JsonSerializer getInstance(){
		if(serializer == null)
			serializer = new JsonSerializer();
		return serializer;
	}
	
	public <T> JsonNode getJsonObject(List<T> list, String listNode){
	
		ObjectNode node = Json.newObject();
		ArrayNode aNode = new ArrayNode(null);
		for (T item : list) {
			aNode.add(convert((T) item));
		}
		return null;
	}
	
	public <T> JsonNode convert(T element){
		try{
			JsonSerializer.class.getMethod("convert", element.getClass());
			return convert((T) element);
		}
	}
	

}
