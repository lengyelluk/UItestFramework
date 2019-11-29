package com.test.lengyel.gui.web;

import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.helper.FrameworkResourceMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Class which loads and holds the desired Capabilities of the Driver
 */
public class FrameworkDriverMetaData {

	private DesiredCapabilities capabilities;

	private FirefoxOptions firefoxOptions;
	private ChromeOptions chromeOptions;

	private boolean gridFlag;

	private URL gridUrl;

	private static final Logger logger = LogManager.getLogger(FrameworkDriverMetaData.class);

	/**
	 * Initialize Driver MetaData via BrowserName
	 * 
	 * @param browserName
	 *            - one of {@code FrameworkConstants.FIREFOX_PROPERTY} or
	 *            {@code FrameworkConstants.INTERNETEXPLORER_PROPERTY}
	 * @param debugMode
	 *            - enable / disable debug mode of browser
	 */
	public FrameworkDriverMetaData(String browserName, boolean debugMode) {
		logger.info("Creating new DriverMetaData");
		firefoxOptions = buildFirefoxCapabilities(browserName, debugMode, false, null, null);
	}

	/**
	 * Initialize Driver MetaData from Properties file
	 * 
	 * @param technicalSettings
	 *            - the properties file
	 */
	public FrameworkDriverMetaData(Properties technicalSettings) {
		logger.info("Creating new DriverMetaData from properties file");
		gridFlag = Boolean.parseBoolean(technicalSettings.getProperty(FrameworkConstants.GRIDFLAG_PROPERTY));
		String stringUrl = technicalSettings.getProperty(FrameworkConstants.GRIDURL_PROPERTY);
		if (stringUrl != null && !stringUrl.equals("")) {
			try {
				gridUrl = new URL(stringUrl);
			} catch (MalformedURLException e) {
				logger.error(stringUrl + " is not a valid URL");
				logger.catching(e);
			}
		}
		firefoxOptions = buildFirefoxCapabilities(technicalSettings);
		
		chromeOptions = buildChromeCapabilities(technicalSettings);
	}

	private FirefoxOptions buildFirefoxCapabilities(Properties technicalSettings) {

		String browserName = technicalSettings.getProperty(FrameworkConstants.BROWSERNAME_PROPERTY);
		
		//return null if browser not firefox
		if(!browserName.equalsIgnoreCase("firefox"))
			return null;
		
		String debugMode = technicalSettings.getProperty(FrameworkConstants.DEBUG_MODE_PROPERTY);
		boolean debug = false;
		if (debugMode != null && debugMode.equals("true")) {
			debug = true;
		}

		// retrieve the path to geckodriver from System properties. If System
		// property is null, retrieve from technical.properties
		String geckodriverPath = System.getProperty(FrameworkConstants.PATH_TO_GECKODRIVER);
		logger.info("GECKO PATH from System properties " + geckodriverPath);
		if (geckodriverPath == null) {
			geckodriverPath = technicalSettings.getProperty(FrameworkConstants.PATH_TO_GECKODRIVER);

			logger.info("GECKO PATH from technical.properties " + geckodriverPath);
		}

		// retrieve the path to Firefox from System properties
		String firefoxPath = System.getProperty(FrameworkConstants.PATH_TO_FIREFOX);
		logger.info("FIREFOX PATH from System properties " + firefoxPath);

		// If System property is null, retrieve from technical.properties
		if (firefoxPath == null) {
			firefoxPath = technicalSettings.getProperty(FrameworkConstants.PATH_TO_FIREFOX);

			logger.info("FIREFOX PATH from technical.properties " + firefoxPath);
		}

		return buildFirefoxCapabilities(browserName, debug, true, geckodriverPath, firefoxPath);
	}
	
	private ChromeOptions buildChromeCapabilities(Properties technicalSettings) {
		String browserName = technicalSettings.getProperty(FrameworkConstants.BROWSERNAME_PROPERTY);
		
		//return null if browser not firefox
		if(!browserName.equalsIgnoreCase("chrome"))
			return null;
		
		String debugMode = technicalSettings.getProperty(FrameworkConstants.DEBUG_MODE_PROPERTY);
		boolean debug = false;
		if (debugMode != null && debugMode.equals("true")) {
			debug = true;
		}
		
		String chromeDriverPath = System.getProperty(FrameworkConstants.PATH_TO_CHROMEDRIVER);
		logger.info("CHROMEDRIVER PATH from System properties " + chromeDriverPath);
		if (chromeDriverPath == null) {
			chromeDriverPath = technicalSettings.getProperty(FrameworkConstants.PATH_TO_CHROMEDRIVER);

			logger.info("CHROMEDRIVER PATH from technical.properties " + chromeDriverPath);
		}

		// retrieve the path to Chrome from System properties
		String chromePath = System.getProperty(FrameworkConstants.PATH_TO_CHROME);
		logger.info("CHROME PATH from System properties " + chromePath);

		// If System property is null, retrieve from technical.properties
		if (chromePath == null) {
			chromePath = technicalSettings.getProperty(FrameworkConstants.PATH_TO_CHROME);

			logger.info("CHROME PATH from System properties " + chromePath);
		}

		return buildChromeCapabilities(browserName, debug, true, chromeDriverPath, chromePath);
	}

	private FirefoxOptions buildFirefoxCapabilities(String browserName, boolean debugMode, boolean automatedUsage,
            String geckodriverPath, String firefoxPath) {
	        logger.debug("Building new Firefox DesiredCapabilities");
	        
	        //create firefox options as advised in Selenium HQ
	    	FirefoxOptions firefoxOptions = new FirefoxOptions();
	        
	        //choose the browser
	        //if (browserName.equalsIgnoreCase(FrameworkConstants.FIREFOX_PROPERTY)) {
	            // Firefox
	        	logger.info("Building new DesiredCapabilities for FireFox");
	        	
	        	//add geckodriver path from technical properties
	        	//setting geckodriver path
	        	if (geckodriverPath != null && geckodriverPath.trim().length() > 0)
					System.setProperty("webdriver.gecko.driver", geckodriverPath);
	        	
	        	//configure Firefox profile
	        	logger.info("Building profile for FireFox");
	        	//creating new Profile
	            FirefoxProfile profile = new FirefoxProfile();
	            configureProfile(profile, automatedUsage);
	            
	            //add debug mode
	            if (debugMode) {
	            	addXpathExtensionToProfile(profile);
	            }
	            
	            //configure Firefox to use geckodriver
	            firefoxOptions.setCapability("marionette", true);
	            //to disable the push notification pop-up
	            firefoxOptions.addPreference("dom.webnotifications.enabled",false); 
	            
	            if (firefoxPath != null && firefoxPath.trim().length() > 0)
	            	//setting binary path of firefox
					firefoxOptions.setBinary(firefoxPath);
	            
	            firefoxOptions.setCapability(FirefoxDriver.PROFILE, profile);
	           
	            //trace log
	            //firefoxOptions.setLogLevel(FirefoxDriverLogLevel.TRACE);
	            
	            return firefoxOptions;
	            
	           /* else if (browserName.equalsIgnoreCase(FrameworkConstants.INTERNETEXPLORER_PROPERTY)) {
	            // Internet Explorer
	            DesiredCapabilities returnCapabilities = DesiredCapabilities.internetExplorer();
	            returnCapabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
	            returnCapabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
	            returnCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
	                    true);
	            // returnCapabilities.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
	            // returnCapabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
	            return returnCapabilities;
	        } else if (browserName.equalsIgnoreCase(FrameworkConstants.EDGE_PROPERTY)) {
	            // Internet Explorer
	            DesiredCapabilities returnCapabilities = DesiredCapabilities.edge();
	            return returnCapabilities;
	        } else {
	            // Default Chrome
	            return DesiredCapabilities.chrome();
	        }*/
    }

	// firebug and firepath not available with new version of Firefox
	/*
	 * private void addDebugExtensionsToProfile(FirefoxProfile profile) { File
	 * fireBugExtension =
	 * SGTResourceMapper.getFileFromDir(FrameworkConstants.LIB_PATH,
	 * FrameworkConstants.FIREBUG_PATH); File firePathExtension =
	 * SGTResourceMapper.getFileFromDir(FrameworkConstants.LIB_PATH,
	 * FrameworkConstants.FIREPATH_PATH); profile.addExtension(fireBugExtension);
	 * profile.addExtension(firePathExtension); }
	 */
	
	
	private ChromeOptions buildChromeCapabilities(String browserName, boolean debugMode, boolean automatedUsage,
            String chromeDriverPath, String chromePath) {
			logger.debug("Building new Chrome DesiredCapabilities");
        
			//create chrome options as advised in Selenium HQ
			ChromeOptions chromeOptions = new ChromeOptions();
        
			logger.info("Building new WebOptions for Chrome");
        	
			//add geckodriver path from technical properties
        	//setting geckodriver path
        	if (chromeDriverPath != null && chromeDriverPath.trim().length() > 0)
				System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            
            if (chromePath != null && chromePath.trim().length() > 0)
            	//setting binary path of firefox
				chromeOptions.setBinary(chromePath);
            
            //to disable the push notification pop-up
            chromeOptions.addArguments("--disable-notifications");
            //to disable chrome is being controlled by automated test software
            chromeOptions.addArguments("disable-infobars");
           
          return chromeOptions;
	}

	private void addXpathExtensionToProfile(FirefoxProfile profile) {
		File tryXpathExtension = FrameworkResourceMapper.getFileFromDir(FrameworkConstants.LIB_PATH, FrameworkConstants.TRYXPATH_PATH);
		profile.addExtension(tryXpathExtension);
	}

	private void configureProfile(FirefoxProfile profile, boolean automatedUsage) {
		if (automatedUsage) {
			profile.setPreference("browser.download.dir",
					System.getProperty(FrameworkConstants.CURRENT_LOGPATH_PROPERTY, ""));
			profile.setPreference("browser.download.folderList", 2);
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
					"application/zip,application/pdf,application/vnd.adobe.xfdf,application/vnd.fdf,application/vnd.adobe.xdp+xml,application/javascript,application/vnd.cups-pdf,application/vnd.sealedmedia.softseal.pdf,application/octet-stream,application/mime,application/rdf+xml,multipart/mixed,text/javascript,text/html,application/xhtml+xml,application/xml,application/gzip");
			// prof.setPreference("plugin.disable_full_page_plugin_for_types",
			// "application/zip,application/pdf,application/vnd.adobe.xfdf,application/vnd.fdf,application/vnd.adobe.xdp+xml,application/javascript,application/vnd.cups-pdf,application/vnd.sealedmedia.softseal.pdf,application/octet-stream,application/mime,application/rdf+xml,multipart/mixed,text/javascript,text/html,application/xhtml+xml,application/xml");
			// pdfjs enabled for documents check
			profile.setPreference("pdfjs.disabled", false);
			// prof.setPreference("plugin.scan.plid.all", false);
			// prof.setPreference("plugin.scan.Acrobat", "99.0");
			profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		}
		// temporary fix for javascript error in pos/fs because of default-cache
		// size 32768
		profile.setPreference("network.buffer.cache.size", 8000);
		// for basic authentication in firefox we have to accept the signon
		// popups by default
		profile.setPreference("signon.autologin.proxy", true);
		// to upload the file
		profile.setPreference("dom.file.createInChild", true);
	}

	public DesiredCapabilities getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(DesiredCapabilities capabilities) {
		this.capabilities = capabilities;
	}

	public FirefoxOptions getFirefoxOptions() {
		return firefoxOptions;
	}

	public void setFirefoxOptions(FirefoxOptions firefoxOptions) {
		this.firefoxOptions = firefoxOptions;
	}
	
	public ChromeOptions getChromeOptions() {
		return chromeOptions;
	}
	
	public void setChromeOptions(ChromeOptions chromeOptions) {
		this.chromeOptions = chromeOptions;
	}

	public boolean isGridFlag() {
		return gridFlag;
	}

	public void setGridFlag(boolean gridFlag) {
		this.gridFlag = gridFlag;
	}

	public URL getGridUrl() {
		return gridUrl;
	}

	public void setGridURL(String gridUrlString) {
		try {
			this.gridUrl = new URL(gridUrlString);
		} catch (MalformedURLException e) {
			logger.error(gridUrlString + " is not a valid URL");
			logger.catching(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((capabilities == null) ? 0 : capabilities.hashCode());
		result = prime * result + (gridFlag ? 1231 : 1237);
		result = prime * result + ((gridUrl == null) ? 0 : gridUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FrameworkDriverMetaData other = (FrameworkDriverMetaData) obj;
		if (capabilities == null) {
			if (other.capabilities != null)
				return false;
		} else if (!capabilities.equals(other.capabilities))
			return false;
		if (gridFlag != other.gridFlag)
			return false;
		if (gridUrl == null) {
			if (other.gridUrl != null)
				return false;
		} else if (!gridUrl.equals(other.gridUrl))
			return false;
		return true;
	}

}