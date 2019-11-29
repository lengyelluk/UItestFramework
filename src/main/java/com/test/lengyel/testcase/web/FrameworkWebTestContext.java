package com.test.lengyel.testcase.web;

import com.test.lengyel.gui.web.FrameworkDriverMetaData;
import com.test.lengyel.testcase.TestContext;
import org.openqa.selenium.WebDriver;

public class FrameworkWebTestContext extends TestContext {

	private WebDriver driver;
	private FrameworkDriverMetaData driverMetaData;

	public FrameworkWebTestContext(TestContext generalContext) {
		setTestData(generalContext.getTestData());
		setTechnicalSettings(generalContext.getTechnicalSettings());
		setFuntionalSettings(generalContext.getFuntionalSettings());
		setAssertActions(generalContext.getAssertActions());
	}

	public FrameworkWebTestContext() {
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public FrameworkDriverMetaData getDriverMetaData() {
		return driverMetaData;
	}

	public void setDriverMetaData(FrameworkDriverMetaData metaData) {
		this.driverMetaData = metaData;
	}

	public String extractFromTestDataName(String key) {
		String testDataName = getTestDataName().toLowerCase();
		String start = "_" + key + "-";
		if (testDataName.contains(start)) {
			String value = testDataName.substring(testDataName.indexOf(start) + start.length());
			// regular option
			int endIndex = value.indexOf("_");
			// lastOption
			if (endIndex == -1) {
				endIndex = value.length();
			}
			value = value.substring(0, endIndex);
			return value;
		}
		return null;
	}

}
