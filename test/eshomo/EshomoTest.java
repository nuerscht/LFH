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
    }
}
