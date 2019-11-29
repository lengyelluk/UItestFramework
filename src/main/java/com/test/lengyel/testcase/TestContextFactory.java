package com.test.lengyel.testcase;

import com.test.lengyel.actions.FrameworkAssertActions;
import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.gui.web.FrameworkDriverFactory;
import com.test.lengyel.gui.web.FrameworkDriverMetaData;
import com.test.lengyel.helper.FrameworkResourceMapper;
import com.test.lengyel.testcase.swing.FrameworkSwingTestCase;
import com.test.lengyel.testcase.swing.FrameworkSwingTestContext;
import com.test.lengyel.testcase.web.FrameworkWebTestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fest.swing.fixture.FrameFixture;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.util.Properties;

public class TestContextFactory {

	private static final Logger logger = LogManager.getLogger(TestContextFactory.class);

	private static TestContext initializeGeneralContext(FrameworkTestCase sgtTestCase, ITestContext ctx) {
		TestContext returnValue = new TestContext();
		returnValue.setTestData(loadTestDataForTestCase(sgtTestCase.getClass()
				.getSimpleName()));
		returnValue.setTechnicalSettings(loadTechnicalSetting());
		returnValue.setFuntionalSettings(loadFunctionalSetting());
		returnValue.setAssertActions(new FrameworkAssertActions());
		String numberCacheAddition = getFunctionalProperty(FrameworkConstants.NUMBERCACHE_ADDITION_PROPERTY);
		if (numberCacheAddition == null) {
			numberCacheAddition = "";
		}

		return returnValue;
	}

	public static FrameworkWebTestContext initializeWebContext(
			FrameworkTestCase sgtTestCase, ITestContext ctx) {
		logger.info("Initializing Web Context for "
				+ sgtTestCase.getClass().getSimpleName());
		FrameworkWebTestContext returnValue = new FrameworkWebTestContext(
				initializeGeneralContext(sgtTestCase, ctx));
		returnValue.setDriverMetaData(new FrameworkDriverMetaData(returnValue
				.getTechnicalSettings()));
		returnValue.setDriver(getDriverViaFactory(returnValue
				.getDriverMetaData()));
		return returnValue;
	}

	public static WebDriver getDriverViaFactory(FrameworkDriverMetaData driverMetaData) {
		return FrameworkDriverFactory.getDriverViaMetaData(driverMetaData);
	}

	public static Properties loadTechnicalSetting() {
		logger.info("Loading technical Settings");
		Properties technicalProperties = FrameworkPropertiesManager
				.loadTechnicalProperties();
		return technicalProperties;
	}

	public static Properties loadFunctionalSetting() {
		logger.info("Loading functional Settings");
		Properties functionalProperties = FrameworkPropertiesManager
				.loadFunctionalProperties();
		return functionalProperties;
	}

	public static Element loadTestDataForTestCase(String testCaseName) {
		logger.info("Loading TestData for " + testCaseName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Element returnValue = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document testData = dBuilder
					.parse(getTestDataForTestCase(testCaseName));
			testData.getDocumentElement().normalize();
			returnValue = testData.getDocumentElement();
		} catch (Exception e) {
			logger.catching(e);
		}
		return returnValue;
	}

	private static File getTestDataForTestCase(String testCaseName) {
		logger.info("Loading TestData for " + testCaseName);
		File testDataFile = FrameworkResourceMapper.getFileFromDir(
				FrameworkConstants.TESTDATA_PATH, testCaseName.concat(".xml"));
		if (testDataFile == null) {
			logger.error("No Testdata found for " + testCaseName);
		}
		return testDataFile;
	}

	public static String getFunctionalProperty(String propertyName) {
		return getFunctionalProperty(propertyName, true);
	}

	public static String getFunctionalProperty(String propertyName,
			boolean useEnvironmentValue) {
		return FrameworkPropertiesManager.getFunctionalProperty(null, propertyName,
				useEnvironmentValue);
	}

	public static String getTechnicalProperty(String propertyName) {
		return FrameworkPropertiesManager.getTechnicalProperty(null, propertyName,
				true);
	}

	public static Properties loadFunctionalSettingValues() {
		return FrameworkPropertiesManager.loadFunctionalSettingValues();
	}

	public static void storeFunctionalProperty(String propertyName,
			String propertyValue) {
		FrameworkPropertiesManager.setFunctionalProperty(null, propertyName,
				propertyValue);
	}

	public static FrameworkSwingTestContext initializeSwingContext(
			FrameworkSwingTestCase sgtSwingTestCase, ITestContext ctx,
			Frame frameForFixture) {
		logger.info("Initializing Swing Context for "
				+ sgtSwingTestCase.getClass().getSimpleName());
		FrameworkSwingTestContext returnValue = new FrameworkSwingTestContext(
				initializeGeneralContext(sgtSwingTestCase, ctx));
		returnValue.setFrameFixture(new FrameFixture(frameForFixture));
		return returnValue;
	}


}
