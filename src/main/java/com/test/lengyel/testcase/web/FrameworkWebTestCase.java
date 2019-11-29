package com.test.lengyel.testcase.web;

import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.gui.web.FrameworkDriverFactory;
import com.test.lengyel.testcase.FrameworkTestCase;
import com.test.lengyel.testcase.TestContextFactory;
import com.test.lengyel.testcase.listener.FrameworkScreenshotOnFailureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

@Listeners(FrameworkScreenshotOnFailureListener.class)
public class FrameworkWebTestCase extends FrameworkTestCase {

	public boolean useDriverPool = true;
	public boolean keepBrowserAlive = false;

	private Map<String, WebDriver> driverStore;

	private static final Logger logger = LogManager.getLogger(FrameworkWebTestCase.class);

	@BeforeClass
	public void setUp(ITestContext ctx) {
		logger.info("Starting TestCase " + this.getClass().getSimpleName());
		testContext = TestContextFactory.initializeWebContext(this, ctx);
	}

	@AfterMethod(alwaysRun = true)
	public void tearDownMethod(ITestContext ctx) {
		if (useDriverPool) {
			FrameworkDriverFactory.putDriverInPool(getTestContext().getDriverMetaData(), getTestContext().getDriver());
			getTestContext().setDriver(null);
		} else {
			if (!keepBrowserAlive) {
				getTestContext().getDriver().quit();
				getTestContext().setDriver(null);
			}
		}
	}

	public FrameworkWebTestContext getTestContext() {
		return (FrameworkWebTestContext) testContext;
	}

	@BeforeMethod(alwaysRun = true)
	public void setUpMethod(ITestContext ctx) {
		if (getTestContext().getDriver() == null) {
			getTestContext().setDriver(FrameworkDriverFactory.getDriverViaMetaData(getTestContext().getDriverMetaData()));
		}
	}

	@AfterSuite(alwaysRun = true)
	public void tearDownSuite() {
		if (!keepBrowserAlive) {
			FrameworkDriverFactory.quit();
		} else {
			//do nothing
		}
	}

	public void storeDriver(String driverName) {
		if (driverStore == null) {
			driverStore = new HashMap<String, WebDriver>();
		}
		driverStore.put(driverName, getTestContext().getDriver());
	}

	public void createNewDriver(String driverName) {	
		if (driverStore == null) {
			driverStore = new HashMap<String, WebDriver>();
		}
		driverStore.put(driverName, FrameworkDriverFactory.getDriverViaMetaData(getTestContext().getDriverMetaData()));	
	}
	
	public void createNewIEDriver(String driverName) {
		if (driverStore == null) {
			driverStore = new HashMap<String, WebDriver>();
		}
		
		if (System.getProperty(FrameworkConstants.IE_DRIVER_PROPERTY) == null)
			System.setProperty(FrameworkConstants.IE_DRIVER_PROPERTY, "c:\\work\\sgt" + FrameworkConstants.IE_DRIVER_PATH);
		
		DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
		caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		caps.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
		
		WebDriver mockDriver = new InternetExplorerDriver(caps);
		
		driverStore.put(driverName, mockDriver);
	}
	

	public void switchToStoredDriver(String driverName) {
		if(driverStore == null){
			throw new RuntimeException("No Driverstore available");
		}
		WebDriver driverToSet = driverStore.get(driverName);
		if(driverToSet == null){
			throw new RuntimeException("No stored Driver "+ driverName +" available");
		}
		getTestContext().setDriver(driverToSet);
	}

	public void closeCurrentDriver() {
		getTestContext().getDriver().close();
	}

}
