package eshomo;

import java.util.List;
import java.util.Map;

import models.CartStatus;
import models.Product;

import org.junit.After;
import org.junit.Before;

import play.libs.Yaml;
import play.test.FakeApplication;
import play.test.Helpers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.MySqlPlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;

public class EshomoTest {

    public static FakeApplication app;
    public static DdlGenerator ddl; 
    
    @After
    public void stopApp() {
        Helpers.stop(app);
    }

    @Before
    public void  onStart() {
        app = Helpers.fakeApplication(Helpers.inMemoryDatabase("test"));
        Helpers.start(app);
        
        ServerConfig config = new ServerConfig();
        ddl = new DdlGenerator();
        ddl.setup((SpiEbeanServer) Ebean.getServer("default"), new MySqlPlatform(), config);
        
        String dropScript = ddl.generateDropDdl();
        ddl.runScript(false, dropScript);
        
        String createScript = ddl.generateCreateDdl();
        ddl.runScript(false, createScript);
        
        // Load initial data to the database
        loadInitData();

        // Load test data to the database
        loadTestData();
        
    }
    
    private void loadInitData() {
        if(Ebean.find(CartStatus.class).findRowCount() == 0){
            @SuppressWarnings("unchecked")
            Map<String,List<Object>> data = (Map<String,List<Object>>)Yaml.load("data-initial.yml");
            Ebean.save(data.get("cartstatus"));
            Ebean.save(data.get("usertypes"));
            Ebean.save(data.get("users"));
            Ebean.save(data.get("addresses"));
        }
        
    }


    private void loadTestData() {
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
}
