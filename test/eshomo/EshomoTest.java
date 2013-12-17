package eshomo;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestBrowser;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.MySqlPlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;

import org.apache.commons.lang3.SystemUtils;

public class EshomoTest {

    public static FakeApplication app;
    public static DdlGenerator ddl; 
    public static TestBrowser phantomBrowser;
    
    @After
    public void stopApp() {
        Helpers.stop(app);
        // Close browser
        phantomBrowser.quit();
    }

    @Before
    public void  onStart() {
        app = Helpers.fakeApplication();
        Helpers.start(app);
        
        ServerConfig config = new ServerConfig();
        ddl = new DdlGenerator();
        ddl.setup((SpiEbeanServer) Ebean.getServer("default"), new MySqlPlatform(), config);
        
        String dropScript = ddl.generateDropDdl();
        ddl.runScript(false, dropScript);
        
        String createScript = ddl.generateCreateDdl();
        ddl.runScript(false, createScript);
        
        // Create TestBrowser
        phantomBrowser = getBrowser();
    }
    
    private TestBrowser getBrowser(){
        String binPath = "";
        if(SystemUtils.IS_OS_WINDOWS)
            binPath = "test/phantomjs/phantomjs.exe";
        if(SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX)
            binPath = "test/phantomjs/phantomjs_osx";
        if(SystemUtils.IS_OS_LINUX && SystemUtils.OS_ARCH.contains("64"))
            binPath = "test/phantomjs/phantomjs_x64";
        if(SystemUtils.IS_OS_LINUX && SystemUtils.OS_ARCH.contains("86"))
            binPath = "test/phantomjs/phantomjs_x86";
            
        final DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, binPath);
        final PhantomJSDriver driver = new PhantomJSDriver(desiredCapabilities);
        final TestBrowser browser = new TestBrowser(driver, "Tester");
        return browser;
    }
}
