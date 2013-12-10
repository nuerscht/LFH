package test.api;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import models.User;
import models.UserType;

import org.junit.Test;

import play.libs.WS;
import play.libs.WS.WSRequestHolder;
import play.test.FakeApplication;
import play.test.Helpers;
import eshomo.EshomoTest;

public class ApiIntegrationTest extends EshomoTest{

    // private constant fields
    private final String customers_url = "/customers";
    private final String orders_url = "/orders";
    private final String articles_url = "/articles";
    private final String version_url = "/version";
    private final String server_url = "http://localhost:3333";

    @Test
    public void checkTokenLoginValidUser_Customer() {
        User user = getNewUser(true, true);
        String[] params = new String[]{"id","all","token",user.getToken()};
        checkTokenLogin(customers_url,params,
                200, "<customers>");
    }
    
    @Test
    public void checkTokenLoginValidUser_Articles() {
        User user = getNewUser(true, true);      
        String[] params = new String[]{"id","all","token",user.getToken()};
        checkTokenLogin(articles_url,params,
                200, "<articles>");
    }
    
    @Test
    public void checkTokenLoginValidUser_Orders() {
        User user = getNewUser(true, true);           
        String[] params = new String[]{"id","all","token",user.getToken()};
        checkTokenLogin(orders_url,params,
                404, "Angeforderte Ressource nicht gefunden");
    }

    private void checkTokenLogin(final String url,final String[] queryParams, final int expectedStatus,
            final String expectedContent) {
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            public void run() {          	
            	WSRequestHolder result = WS.url(server_url + url);
            	for(int i = 0 ; i + 1 < queryParams.length ; i += 2){            		
            		result.setQueryParameter(queryParams[i], queryParams[i+1]);
            	}
                WS.Response response = result.get().get(5000);
                // Check if status is okay and content is correct
                assertThat(response.getStatus()).isEqualTo(expectedStatus);
                assertThat(response.getBody()).contains(expectedContent);                
            }
        });
    }

    private User getNewUser(final Boolean isAdmin, final Boolean isActive) {
        final User user = new User();
        FakeApplication fakeApp = Helpers.fakeApplication();
        running(testServer(3333, fakeApp), new Runnable() {
            @Override
            public void run() {
                UserType type;
                if (isAdmin)
                    type = UserType.find.byId("admin");
                else
                    type = UserType.find.byId("customer");
                user.setEmail("jabba@thehutt.com");
                user.setIsActive(true);
                user.setPassword("ihatesolo");
                user.setType(type);
                user.setIsActive(isActive);
                user.save();             
            }
        });
        
        return user;
    }

}
