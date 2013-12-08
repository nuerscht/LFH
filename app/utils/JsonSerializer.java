package utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;

import models.Address;
import models.Attribute;
import models.Cart;
import models.CartHasProduct;
import models.Product;
import models.User;
import play.i18n.Messages;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Entities serializer for the API controller.
 * @author Sandro Dallo
 *
 */
public class JsonSerializer {
	
	private static JsonSerializer serializer;
	private ObjectMapper mapper;

	
	
	private JsonSerializer(){}
	
	/**
	 * Gets an instance of the JsonSerializer for the API
	 * @return Returns the instance
	 */
	public static JsonSerializer getInstance(){
		if(serializer == null)
			serializer = new JsonSerializer();
		return serializer;
	}
	
	private ObjectMapper getMapper(){
		if(mapper == null)
			mapper = new ObjectMapper();
		return mapper;
	}

	/**
	 * Serialize database entities to the corresponding API json object
	 * @param list A generic list with database entities
	 * @param listNode A Name for the property that contains the array of the db entities
	 * @return A corresponding json object
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public <T> JsonNode getJsonObject(List<T> list, String listNode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		ObjectNode node = Json.newObject();
		ArrayNode aNode = new ArrayNode(null);
		for (T item : list) {
			aNode.add(convert(item));
		}
		node.put(listNode, aNode);
		return node;
	}
	/**
	 * Serialize database entities to the corresponding API json object
	 * @param element A database entity
	 * @return A corresponding json object
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public <T> JsonNode convert(T element) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		try
		{
			Method m = JsonSerializer.class.getDeclaredMethod("convert", element.getClass());
			JsonNode node = (JsonNode) m.invoke(this, element);
			return node;
		} catch (NoSuchMethodException | SecurityException ex){}

		return convertObject((Object) element);
	}
	
	private JsonNode convertObject(Object element){
		ObjectNode node = Json.newObject();		
		node.putPOJO(element.getClass().getName(),element);
		return node;
	}
	
	@SuppressWarnings("unused")
	private JsonNode convert(Product element){
		
		ObjectNode node = Json.newObject();
		node.put("id", element.getId());
		node.put("title", element.getTitle());
		node.put("description", element.getDescription());
		ArrayNode aNode = getMapper().createArrayNode();
		for (Attribute ad : element.getAttributes()) {
			aNode.add(ad.getValue());	
		}
		node.put("attributes", aNode);
		DecimalFormat df = new DecimalFormat("0.00");
		node.put("price", df.format(element.getPrice()));
		node.put("currency", Messages.get("api.request.currency"));
		return node;
	}
	
	@SuppressWarnings("unused")
	private JsonNode convert(Cart element){
		
		ObjectNode node = Json.newObject();
		node.put("id", element.getId());
		node.put("customer", element.getUser().getId());		
		node.put("billingaddress", element.getAddress() != null ? element.getAddress().getId() : null );
		node.put("shippingaddress", element.getAddress() != null ? element.getAddress().getId() : null );
		node.put("status", element.getStatus().getId());
		ArrayNode aNode = getMapper().createArrayNode();
		for (CartHasProduct cp : element.getCartHasProduct()) {
			aNode.add(convert(cp));	
		}
		node.put("positions", aNode);
		return node;
	}
	
	private JsonNode convert(CartHasProduct element){
		
		ObjectNode node = Json.newObject();
		node.put("article", element.getProduct().getId());
		node.put("amount", element.getAmount());
		node.put("discount", element.getDiscount());
		return node;
	}
	
	@SuppressWarnings("unused")
	private JsonNode convert(User element){
		List<Address> address = element.getAddresses();
		ObjectNode node = Json.newObject();
		if(address.size() == 0)
			return node;
		node.put("id", element.getId());
		node.put("name", address.get(0).getLastname());
		node.put("firstname", address.get(0).getFirstname());
		ArrayNode aNode = getMapper().createArrayNode();
		for (Address ad : address) {
			aNode.add(convert(ad));	
		}
		node.put("addresses", aNode);
		node.put("email", address.get(0).getEmail());
		node.put("birthday", address.get(0).getBirthday().getTime() / 1000L);
		return node;
	}
	
	private JsonNode convert(Address element){
		
		ObjectNode node = Json.newObject();
		node.put("id", element.getId());
		node.put("type", 1);
		node.put("city", element.getPhone());
		node.put("street", element.getStreet());
		node.put("postcode", element.getZip());
		node.put("country", 
				element.getCountry() != null 
				? element.getCountry().getName()
						: null);
		return node;
	}

}
