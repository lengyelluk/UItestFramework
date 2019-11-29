package com.test.lengyel.gui;

import com.test.lengyel.testcase.TestContext;

public class ShadowGuiActions implements IFrameworkGuiActions {

	private String getTag(GuiElement guiElement) {
		return "<" + guiElement.getName() + "></" + guiElement.getName() + ">";
	}

	private String getTag(String guiElement) {
		return "<" + guiElement + "></" + guiElement + ">";
	}

	@Override
	public void useCorrectValue(TestContext testContext, GuiElement guiElement) {
		System.err.println(getTag(guiElement));
	}

	@Override
	public void useCorrectValue(TestContext testContext, GuiElement guiElement, String id) {
		System.err.println(getTag(guiElement));
	}

	@Override
	public String resolveTestDataForGuiElement(TestContext testContext, GuiElement guiElement) {
		System.err.println(getTag(guiElement));
		return null;
	}

	@Override
	public String resolveTestDataForGuiElement(TestContext testContext, String valueName) {
		System.err.println(getTag(valueName));
		return null;
	}

	@Override
	public String resolveTestDataForGuiElement(TestContext testContext, GuiElement guiElement, String id) {
		System.err.println(getTag(guiElement));
		return null;
	}

	@Override
	public void waitForFrameToBeAvailableAndSwitchToIt(TestContext testContext, String frameName, int timeout) {

	}

	@Override
	public String resolveTestDataForGuiElement(TestContext testContext, String valueName, String id) {
		System.err.println(getTag(valueName));
		return null;
	}

	@Override
	public void applyValue(TestContext testContext, GuiElement guiElement) {
	}

	@Override
	public void clearInput(TestContext testContext, GuiElement guiElement) {
	}

	@Override
	public void select(TestContext testContext, GuiElement guiElement, String value) {
	}

	@Override
	public void sendText(TestContext testContext, GuiElement guiElement, String value) {
	}

	@Override
	public void click(TestContext testContext, GuiElement guiElement) {
	}

	@Override
	public void useSpecifiedValue(TestContext testContext, GuiElement guiElement, String value) {
	}

	@Override
	public boolean isElementPresent(TestContext testContext, GuiElement guiElement) {
		return false;
	}

	@Override
	public String getCurrentText(TestContext testContext, GuiElement guiElement) {
		return null;
	}

	@Override
	public void sendTab(TestContext testContext, GuiElement guiElement) {
	}

	@Override
	public void submit(TestContext testContext, GuiElement guiElement) {
	}

	@Override
	public void waitForElementVisible(TestContext testContext, GuiElement guiElement, int timeout) {
	}

	@Override
	public void waitForElementInvisible(TestContext testContext, GuiElement guiElement, int timeout) {
	}

	@Override
	public boolean isElementEnabled(TestContext testContext, GuiElement guiElement) {
		return false;
	}

	@Override
	public void waitForPageToLoad(TestContext testContext, int timeout) {
	}
	
	@Override
	public void scrollToMiddle(TestContext testContext, GuiElement guiElement) {	
	}
	
	@Override
	public void scrollToTop(TestContext testContext) {
	}
	
	@Override
	public void setInputValue(TestContext testContext, GuiElement guiElement, String value) {
	}

	@Override
	public void waitForElementWithAttribute(TestContext testContext, GuiElement guiElement, 
			String attributeName, String attributeValue, int timeout){}
	
	@Override
	public void waitForElementContainsAttribute(TestContext testContext, GuiElement guiElement,
			String attributeName, String attributeValue, int timeout) {}
	
	@Override
	public void waitForElementText(TestContext testContext, GuiElement guiElement, String text,
			int timeout) {}
	
	@Override
	public void scrollBy(TestContext testContext, int x, int y) {}
	
	@Override
	public void waitForFrameByAttributeAndSwitchToIt(TestContext testContext, GuiElement guiElement,
			int timeout) {}
	
	@Override
	public void sendKeyUp(TestContext testContext, GuiElement guiElement, 
			int count) {};
}
