package com.test.lengyel.gui;


import com.test.lengyel.testcase.TestContext;

public interface IFrameworkGuiActions {

	public abstract void useCorrectValue(TestContext testContext, GuiElement guiElement);

	public abstract void applyValue(TestContext testContext, GuiElement guiElement);

	public abstract void clearInput(TestContext testContext, GuiElement guiElement);

	public abstract void select(TestContext testContext, GuiElement guiElement, String value);

	public abstract void sendText(TestContext testContext, GuiElement guiElement, String value);

	public abstract void click(TestContext testContext, GuiElement guiElement);

	public abstract void useSpecifiedValue(TestContext testContext, GuiElement guiElement, String value);

	public abstract boolean isElementPresent(TestContext testContext, GuiElement guiElement);

	public abstract String getCurrentText(TestContext testContext, GuiElement guiElement);

	public abstract void sendTab(TestContext testContext, GuiElement guiElement);

	public abstract void submit(TestContext testContext, GuiElement guiElement);

	public abstract void waitForElementVisible(TestContext testContext, GuiElement guiElement, int timeout);

	public abstract void waitForElementInvisible(TestContext testContext, GuiElement guiElement, int timeout);

	public abstract boolean isElementEnabled(TestContext testContext, GuiElement guiElement);

	void useCorrectValue(TestContext testContext, GuiElement guiElement, String id);
	
	public String resolveTestDataForGuiElement(TestContext testContext, GuiElement guiElement);
	
	public String resolveTestDataForGuiElement(TestContext testContext, String valueName);
	
	public String resolveTestDataForGuiElement(TestContext testContext, String valueName, String id);
	
	public String resolveTestDataForGuiElement(TestContext testContext, GuiElement guiElement, String id);

	public void waitForFrameToBeAvailableAndSwitchToIt(TestContext testContext, String frameName, int timeout);

	public void waitForPageToLoad(TestContext testContext, int timeout);

	public void scrollToMiddle(TestContext testContext, GuiElement guiElement);

	public void scrollToTop(TestContext testContext);

	public void setInputValue(TestContext testContext, GuiElement guiElement, String value);

	public void waitForElementWithAttribute(TestContext testContext, GuiElement guiElement,
                                            String AttributeName, String attributeValue, int timeout);

	public void waitForElementContainsAttribute(TestContext testContext, GuiElement guiElement,
                                                String attributeName, String attributeValue, int timeout);

	public void waitForElementText(TestContext testContext, GuiElement guiElement, String text,
                                   int timeout);

	public void scrollBy(TestContext testContext, int x, int y);

	public void waitForFrameByAttributeAndSwitchToIt(TestContext testContext, GuiElement guiElement,
                                                     int timeout);

	public void sendKeyUp(TestContext testContext, GuiElement guiElement, int count);
}