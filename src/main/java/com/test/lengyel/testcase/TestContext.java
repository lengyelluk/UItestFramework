package com.test.lengyel.testcase;

import com.test.lengyel.actions.FrameworkAssertActions;
import org.w3c.dom.Element;

import java.util.Properties;

public class TestContext {

	private Element testData;
	private Properties funtionalSettings;
	private Properties technicalSettings;
	private FrameworkAssertActions assertActions;
	private String testChainID;
	private String testDataName;

	public String getTestDataName(){
		return testDataName;
	}
	
	public void setTestDataName(String testDataName){
		this.testDataName = testDataName;
	}
	
	public Properties getFuntionalSettings() {
		return funtionalSettings;
	}

	public void setFuntionalSettings(Properties funtionalSettings) {
		this.funtionalSettings = funtionalSettings;
	}

	public Element getTestData() {
		return testData;
	}

	public void setTestData(Element testDataRootElement) {
		this.testData = testDataRootElement;
	}

	public Properties getTechnicalSettings() {
		return technicalSettings;
	}

	public void setTechnicalSettings(Properties technicalSettings) {
		this.technicalSettings = technicalSettings;
	}

	public FrameworkAssertActions getAssertActions() {
		return assertActions;
	}

	public void setAssertActions(FrameworkAssertActions assertActions) {
		this.assertActions = assertActions;
	}

	public String getTestChainID() {
		return testChainID;
	}

	public void setTestChainID(String testChainID) {
		this.testChainID = testChainID;
	}

}
