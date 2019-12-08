package com.test.lengyel.page.web;


import com.test.lengyel.gui.GuiElement;
import com.test.lengyel.gui.web.FrameworkWebGuiActions;
import com.test.lengyel.page.FrameworkPage;
import com.test.lengyel.testcase.TestContext;
import com.test.lengyel.testcase.web.FrameworkWebTestContext;
import org.openqa.selenium.WebElement;

import java.util.List;

public class FrameworkWebPage extends FrameworkPage {

	public FrameworkWebPage(TestContext testContext) {
		super(testContext);
		setGuiActions(new FrameworkWebGuiActions());
	}

	//appli tool hack
	public void initializeApplitoolsEyes() {
		((FrameworkWebTestContext) testContext).getDriver();
	}

	public void loadUrl(String url) {
		((FrameworkWebTestContext) testContext).getDriver().get(url);
		((FrameworkWebTestContext) testContext).getDriver().manage().window().maximize();
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
		}
	}

	public boolean elementExists(GuiElement guiElement) {
		return guiActions().elementExists(testContext, guiElement);
	}

	public void executeScript(String script) { guiActions().executeScript(testContext, script);}

	public String getSource() { return guiActions().getSource(testContext); }

	public void clearInputAndSetNewValue(GuiElement guiElement, String value) {
		guiActions().clearInputAndSetNewValue(testContext, guiElement, value);
	}

	private FrameworkWebGuiActions guiActions() {
		return (FrameworkWebGuiActions) getGuiActions();
	}

	public List<WebElement> getAllMatchingElements(GuiElement guiElement) {
		return guiActions().getAllMatchingElements(getTestContext(), guiElement);
	}

	public void switchToFrame(String frameName) {
		guiActions().switchToFrame(testContext, frameName);
	}

	public String getAttributeValue(GuiElement guiElement, String attributeName) {
		return guiActions().getAttributeValue(testContext, guiElement, attributeName);

	}

	public void switchToFrame(int frameNum) {
		guiActions().switchToFrame(testContext, frameNum);
	}

	public void switchToDefaultFrame() {
		guiActions().switchToDefaultFrame(testContext);
	}

	public void switchToWindowTitleContaining(String windowTitle) {
		guiActions().switchToWindowTitleContaining(testContext, windowTitle);
	}

	public void closeWindowTitleContaining(String windowTitle) {
		guiActions().closeWindowTitleContaining(testContext, windowTitle);
	}

	@Override
	public FrameworkWebTestContext getTestContext() {
		return (FrameworkWebTestContext) testContext;
	}

	public void clickDirty(GuiElement guiElement) {
		guiActions().clickDirty(testContext, guiElement);
	}

	public void insertValueInFieldDirty(GuiElement guiElement, String insertValue) {
		guiActions().insertValueInFieldDirty(testContext, guiElement, insertValue);
	}

	public void waitForElementClickable(GuiElement guiElement) {
		guiActions().waitForElementClickable(testContext, guiElement);
	}

	public void waitForElementClickable(GuiElement guiElement, int timeOut) {
		guiActions().waitForElementClickable(testContext, guiElement, timeOut);
	}

	public boolean isChecked(GuiElement guiElement) {
		return guiActions().isChecked(testContext, guiElement);
	}

	public void scrollTo(GuiElement guiElement) {
		guiActions().scrollTo(testContext, guiElement);
	}

	/**
	 * drag a scroll bar until the found element is showed in view
	 */
	public void moveToAnElement(GuiElement dragAbleElement, GuiElement guiElement) {
		guiActions().dragUntilElementIsVisible(testContext, dragAbleElement, guiElement);
	}

	public void refresh() {
		guiActions().reload(testContext);
	}

	/**
	 * clicks the scroll bar and drags it down or up by the number of pixels
	 * 
	 * @param draggableScrollbar               : the scroll bar to drag
	 * @param numberOfPixelsToDragTheScrollbar : the number of pixels to drag. If
	 *                                         the number is positive - drags
	 *                                         downwards, if negative - upwards
	 */
	public void drag(GuiElement draggableScrollbar, int numberOfPixelsToDragTheScrollbar) {
		guiActions().drag(testContext, draggableScrollbar, numberOfPixelsToDragTheScrollbar);
	}

	/**
	 * waits for ajax request to complete
	 * 
	 * @param maxTimeToWait: maximum time to wait
	 */
	public void waitForAjaxToFinish(int maxTimeToWait) {
		guiActions().waitForAjaxToFinish(testContext, maxTimeToWait);
	}

	/**
	 * hovers over Webelement without clicking it
	 */
	public void hoverOverElement(GuiElement guiElement) {
		guiActions().hoverOverElement(testContext, guiElement);
	}

	/**
	 * selects value with the specified index in the
	 * dropdown
	 */
	public void selectByIndex(GuiElement dropdown, int index) {
		guiActions().selectByIndex(testContext, dropdown, index);
	}

	/**
	 * selects value with the specified value in the
	 * dropdown (<option> Element)
	 */
	public void select(GuiElement dropdown, String value) {
		guiActions().select(testContext, dropdown, value);
	}

	/**
	 * sets size for the browser window
	 * 
	 * @param testContext
	 * @param width       of browser window
	 * @param height      of browser window
	 */
	public void setWindowSize(int width, int height) {
		guiActions().setWindowSize(testContext, width, height);
	}

	/**
	 * accepts the currently displayed alert pop up
	 * 
	 * @param testContext
	 */
	public void acceptAlert() {
		guiActions().acceptAlert(testContext);
	}


	public void waitForElementToHide(GuiElement guiElement, int timeout) {
		guiActions().waitForElementToHide(testContext, guiElement, timeout);
	}
	
	/**
	 * different than scrollTo in such a way that it does not
	 * scroll until the element is on the top of the page but it scrolls to
	 * get the element to the bottom of the page
	 * @param guiElement
	 */
	public void scrollToBottom(GuiElement guiElement) {
		guiActions().scrollToBottom(testContext, guiElement);
	}
	
}
