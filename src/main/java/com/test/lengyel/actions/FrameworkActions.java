package com.test.lengyel.actions;

import com.test.lengyel.gui.GuiElement;
import com.test.lengyel.gui.web.FrameworkWebGuiActions;
import com.test.lengyel.page.FrameworkPage;
import com.test.lengyel.page.FrameworkPageFactory;
import com.test.lengyel.testcase.FrameworkPropertiesManager;
import com.test.lengyel.testcase.TestContext;
import com.test.lengyel.testcase.TestContextFactory;
import com.test.lengyel.testcase.web.FrameworkWebTestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FrameworkActions {

	private TestContext testContext;
	private static final Logger logger = LogManager.getLogger(FrameworkActions.class);

	public FrameworkActions(TestContext testContext) {
		setTestContext(testContext);
	}

	public FrameworkActions(TestContext testContext, String testDataName) {
		testContext.setTestData(TestContextFactory.loadTestDataForTestCase(testDataName));
		testContext.setTestChainID(getTestChainID(testDataName));
		setTestContext(testContext);
	}
	
	protected String getTestChainID(String testDataName) {
		try{
		testDataName = testDataName.substring(0, testDataName.indexOf("#"));
		}catch(Exception e){
		}
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

	public void setTestContext(TestContext testContext) {
		this.testContext = testContext;
	}

	public <T extends FrameworkPage> T getPage(Class<T> clazz) {
		return FrameworkPageFactory.getPage(clazz, getTestContext());
	}

	public String getProperty(String propertyName) {
		return FrameworkPropertiesManager.getFunctionalProperty(testContext,
				propertyName, true);
	}

	public void loadUrl(String url) {
		logger.info("loading url: " + url);
		((FrameworkWebTestContext) this.getTestContext()).getDriver().get(url);
		((FrameworkWebTestContext) this.getTestContext()).getDriver().manage()
				.window().maximize();
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
		}
	}

	public String resolveTestDataForGuiElement(GuiElement guiElement) {
		return new FrameworkWebGuiActions().resolveTestDataForGuiElement(getTestContext(),
				guiElement);
	}
	
	public String resolveTestData(String lookupValue) {
		return new FrameworkWebGuiActions().resolveTestDataForGuiElement(getTestContext(),
				lookupValue);
	}
	
	public String resolveTestData(String lookupValue, String id) {
		return new FrameworkWebGuiActions().resolveTestDataForGuiElement(getTestContext(), lookupValue, id);
	}
	
	/**
	 * Resolves the test data value of the lookupValue as boolean 
	 * 
	 * @param lookupValue
	 * @return
	 */
	public boolean resolveTestDataBoolean(String lookupValue) {
		String value = resolveTestData(lookupValue);
		if(value != null && value.equalsIgnoreCase("true")){
			return true;
		} else {
			return false;
		}
	}

	public void storeProperty(String propertyName, String value) {
		FrameworkPropertiesManager.setFunctionalProperty(testContext, propertyName,
				value);
	}


}
