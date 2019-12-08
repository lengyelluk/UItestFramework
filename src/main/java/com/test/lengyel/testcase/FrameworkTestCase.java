package com.test.lengyel.testcase;

import com.test.lengyel.actions.FrameworkAssertActions;
import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.helper.FrameworkResourceMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public abstract class FrameworkTestCase {
	protected TestContext testContext;

	private static final Logger logger = LogManager.getLogger(FrameworkTestCase.class);

	@BeforeSuite
	public void setUpLogDirectory(ITestContext ctx) {
		String logDir = "";
		if (System.getProperty(FrameworkConstants.SGT_INSTALLATION_DIR) != null) {
			logDir = System.getProperty(FrameworkConstants.SGT_INSTALLATION_DIR) + "\\logs\\";
		} else {
			logDir = (String) TestContextFactory.getTechnicalProperty(FrameworkConstants.LOGFILE_PROPERTY);
		}
		String logPath = logDir + getCurrentTimeStamp();
		String logFile = logPath + "\\sgt.log";
		System.setProperty(FrameworkConstants.CURRENT_LOGFILE_PROPERTY, logFile);
		storeTechnicalProperty(FrameworkConstants.CURRENT_LOGPATH_PROPERTY, logPath);
		LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
		logContext.reconfigure();
	}

	public void checkVerificationErrors() {
		FrameworkAssertActions.checkForVerificationErrors();
	}
	
	public void activateShadowMode(){
		System.setProperty(FrameworkConstants.SHADOW_MODE, "true");
	}

	private String getCurrentTimeStamp() {
		DateFormat formatter = new SimpleDateFormat("YYYYMMdd HHmmss");
		Date currentDate = new Date();
		return formatter.format(currentDate);
	}

	@AfterClass(alwaysRun = true)
	public void tearDown(ITestContext ctx) {
		logger.info("Ending TestCase " + this.getClass().getSimpleName());
		String numberCacheAddition = getProperty(FrameworkConstants.NUMBERCACHE_ADDITION_PROPERTY);
		if (numberCacheAddition == null) {
			numberCacheAddition = "";
		}
	}

	public String getTechnicalProperty(String propertyName) {
		if (testContext == null || testContext.getTechnicalSettings() == null) {
			return TestContextFactory.getTechnicalProperty(propertyName);
		}
		return testContext.getTechnicalSettings().getProperty(propertyName);
	}

	public String getProperty(String propertyName) {
		if (System.getProperty(propertyName) != null) {
			return System.getProperty(propertyName);
		}
		if (testContext == null || testContext.getFuntionalSettings() == null) {
			return TestContextFactory.getFunctionalProperty(propertyName);
		}
		return testContext.getFuntionalSettings().getProperty(propertyName);
	}

	public void storeProperty(String propertyName, String propertyValue) {
		if(testContext != null){
			testContext.getFuntionalSettings().setProperty(propertyName, propertyValue);
			File propertiesFile = FrameworkResourceMapper.getFileFromDir(FrameworkConstants.PROPERTIES_PATH,
					FrameworkConstants.FUNCTIONAL_PROPERTIES_NAME);
			try {
				testContext.getFuntionalSettings().store(new FileOutputStream(propertiesFile), "");
			} catch (Exception e) {
				logger.catching(e);
			}
		}
	}

	public void storeTechnicalProperty(String propertyName, String propertyValue) {
		Properties technicalProperties = TestContextFactory.loadTechnicalSetting();
		technicalProperties.setProperty(propertyName, propertyValue);
		System.setProperty(propertyName, propertyValue);
		File propertiesFile = FrameworkResourceMapper.getFileFromDir(FrameworkConstants.PROPERTIES_PATH,
				FrameworkConstants.TECHNICAL_PROPERTIES_NAME);
		try {
			technicalProperties.store(new FileOutputStream(propertiesFile), "");
		} catch (Exception e) {
			logger.catching(e);
		}
	}

	public void setTestData(String testDataName) {
		if(testDataName.endsWith(".xml")){
			try {
				testDataName = testDataName.substring(0, testDataName.indexOf(".xml"));
				
			} catch (Exception e) {
			}
		}
		testContext.setTestData(TestContextFactory.loadTestDataForTestCase(testDataName));
		testContext.setTestDataName(testDataName);
		try {
		testDataName = testDataName.substring(0, testDataName.indexOf("#"));
		} catch (Exception e) {
		}
		testContext.setTestChainID(getTestChainID(testDataName));
		
	}

	protected String getTestChainID(String testDataName) {
		String[] values = testDataName.split("_");
		String returnValue = "";
		for (int i = 1; i < values.length; i++)
			returnValue += trySetValue(values, i);
		return returnValue;
	}

	private String trySetValue(String[] values, int i) {
		try {
			return values[i];
		} catch (Exception e) {
			return "";
		}
	}

	public TestContext getTestContext() {
		return testContext;
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}

	public int compareTo(FrameworkTestCase o) {
		return this.toString().compareTo(o.toString());
	}


}
