package com.test.lengyel.gui.web;

import com.test.lengyel.gui.FrameworkKindEnum;
import com.test.lengyel.gui.GuiElement;
import com.test.lengyel.gui.IFrameworkGuiActions;

import com.test.lengyel.testcase.FrameworkPropertiesManager;
import com.test.lengyel.testcase.TestContext;
import com.test.lengyel.testcase.web.FrameworkWebTestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.util.Strings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.test.lengyel.constants.FrameworkConstants.*;

public class FrameworkWebGuiActions implements IFrameworkGuiActions {

	private static final Logger logger = LogManager
			.getLogger(FrameworkWebGuiActions.class);

	private By getBy(GuiElement guiElement) {
		//logger.info("guiElemnt",guiElement, guiElement);
		//System.out.println("guiElemnt"+guiElement);
		if (guiElement.getIdentifier().equalsIgnoreCase(
				IDENTIFIER_ID)) {
			if (guiElement.getIdentValue().startsWith("//")) {
				logger.info("identValue contains potential xpath");
				System.err.println("identValue contains potential xpath");
			}
			return By.id(guiElement.getIdentValue());
		} else if (guiElement.getIdentifier().equalsIgnoreCase(
				IDENTIFIER_NAME)) {
			return By.name(guiElement.getIdentValue());
		} else if (guiElement.getIdentifier().equalsIgnoreCase(
				IDENTIFIER_LINKTEXT)) {
			return By.linkText(guiElement.getIdentValue());
		} else if (guiElement.getIdentifier().equalsIgnoreCase(
				IDENTIFIER_XPATH)) {
			return By.xpath(guiElement.getIdentValue());
		} else if (guiElement.getIdentifier().equalsIgnoreCase(
				IDENTIFIER_CSS)) {
			return By.cssSelector(guiElement.getIdentValue());
		} else if (guiElement.getIdentifier().equalsIgnoreCase(
				IDENTIFIER_PARTIALLINKTEXT)) {
			return By.partialLinkText(guiElement.getIdentValue());
		} else if (guiElement.getIdentifier().equalsIgnoreCase(
				IDENTIFIER_TAGNAME)) {
			return By.tagName(guiElement.getIdentValue());
		} else if (guiElement.getIdentifier().equalsIgnoreCase(
				IDENTIFIER_CLASSNAME)) {
			return By.className(guiElement.getIdentValue());
		} else if (guiElement.getIdentifier().equalsIgnoreCase(
				IDENTIFIER_PARTIALID)) {
			return By.xpath("//*[contains(@id,'" + guiElement.getIdentValue()
					+ "')]");
		}
		return null;
	}

	public void clearInputAndSetNewValue(TestContext testContext,
										 GuiElement guiElement, String value) {
		findElement(testContext, guiElement).sendKeys(
				Keys.chord(Keys.CONTROL, "a"));
		findElement(testContext, guiElement).sendKeys("");
		findElement(testContext, guiElement).sendKeys(value);
		if (guiElement.isAjax()) {
			sendTab(testContext, guiElement);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void insertValueInFieldDirty(TestContext testContext,
			GuiElement guiElement, String insertValue) {
		waitForElementClickable(testContext, guiElement);

		switch (guiElement.getIdentifier()) {
		case IDENTIFIER_ID:
			((JavascriptExecutor) ((FrameworkWebTestContext) testContext).getDriver())
					.executeScript("document.getElementById('"
							+ guiElement.getStaticIdentValue() + "').value = '"
							+ insertValue + "';");

			break;
		case IDENTIFIER_XPATH:
			((JavascriptExecutor) ((FrameworkWebTestContext) testContext).getDriver())
					.executeScript("document.evaluate('"
							+ guiElement.getStaticIdentValue() + "').value = '"
							+ insertValue + "';");
			break;
		}

	}

	public void closeWindowTitleContaining(TestContext testContext,
			String windowTitle) {
		Set<String> windows = ((FrameworkWebTestContext) testContext).getDriver()
				.getWindowHandles();
		for (String window : windows) {
			((FrameworkWebTestContext) testContext).getDriver().switchTo()
					.window(window);
			if (((FrameworkWebTestContext) testContext).getDriver().getTitle()
					.contains(windowTitle)) {
				((FrameworkWebTestContext) testContext).getDriver().close();
			}
		}
	}

	private WebElement findElement(TestContext testContext,
			GuiElement guiElement) {
		WebElement element = null;
		element = ((FrameworkWebTestContext) testContext).getDriver()
				.findElement(getBy(guiElement));
		return element;
	}
	
	public String getAttributeValue(TestContext testContext,
			GuiElement guiElement, String attributeName) {
		WebElement element = null;
		String attributeValue = null;
		element = ((FrameworkWebTestContext) testContext).getDriver()
				.findElement(getBy(guiElement));
		attributeValue = element.getAttribute(attributeName);
		/*try {
			element = ((FrameworkWebTestContext) testContext).getDriver()
					.findElement(getBy(guiElement));
			attributeValue = element.getAttribute(attributeName);
		} catch (NoSuchElementException e) {
			element = findElementWithReload(testContext, guiElement);
		}*/
		return attributeValue;
	}

	private WebElement findElementWithReload(TestContext testContext,
			GuiElement guiElement) {
		((FrameworkWebTestContext) testContext).getDriver().navigate().refresh();
		WebElement element = ((FrameworkWebTestContext) testContext).getDriver()
				.findElement(getBy(guiElement));
		return element;
	}

	public void reload(TestContext testContext) {
		((FrameworkWebTestContext) testContext).getDriver().navigate().refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.test.sogeti.gui.web.ISGTGuiActions#useCorrectValue(com.test.sogeti
	 * .testcase.TestContext, com.test.sogeti.gui.GuiElement)
	 */
	@Override
	public void useCorrectValue(TestContext testContext,
			GuiElement guiElement) {
		logger.debug(guiElement);
		resolveTestDataForGuiElement(testContext, guiElement);
		applyValue(testContext, guiElement);
	}

	@Override
	public void useCorrectValue(TestContext testContext,
			GuiElement guiElement, String id) {
		logger.debug(guiElement);
		resolveTestDataForGuiElement(testContext, guiElement, id);
		applyValue(testContext, guiElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.test.sogeti.gui.web.ISGTGuiActions#applyValue(com.test.sogeti.testcase
	 * .TestContext, com.test.sogeti.gui.GuiElement)
	 */
	@Override
	public void applyValue(TestContext testContext, GuiElement guiElement) {
		logger.info("applying value to element: " + guiElement.getName());
		if (guiElement.getValue() == null) {
			return;
		}
		if (isTextField(guiElement)) {
			if (!Strings.isNullOrEmpty(getCurrentText(testContext, guiElement))) {
				clearInput(testContext, guiElement);
				if (guiElement.isAjax() || guiElement.isHttp()) {
					sendTab(testContext, guiElement);
					if (!isPerformanceEnabled()) {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
			}
			sendText(testContext, guiElement, guiElement.getValue());
			if (guiElement.isAjax() || guiElement.isHttp()) {
				sendTab(testContext, guiElement);
			}
		} else if (isSelectWithText(guiElement)) {
			selectWithText(testContext, guiElement, guiElement.getValue());
		} else if (isSelectWithoutText(guiElement)) {
			selectWithoutText(testContext, guiElement, guiElement.getValue());
		} else if (isComboBox(guiElement)) {
			select(testContext, guiElement, guiElement.getValue());
		} else if (isDropDown(guiElement)) {
			selectDropDown(testContext, guiElement, guiElement.getValue());
		} else if (isCheckBox(guiElement)) {
			selectCheckBox(testContext, guiElement, guiElement.getValue());
		}

	}

	private void selectWithText(TestContext testContext,
			GuiElement guiElement, String value) {
		String xpathInputArea = guiElement.getStaticIdentValue();
		String[] parts = xpathInputArea.split("]");
		String xpathSelectBox = parts[0] + "]";
		logger.info("XPATH of SelectBox: " + xpathSelectBox); // we extract the
																// selectBox Id
																// of original
																// Xpath
		logger.info("XPATH of InputArea: " + guiElement.getStaticIdentValue()); // the
																				// original
																				// XML
																				// Xpath
																				// of
																				// guiElement
																				// is
																				// "set"
																				// to
																				// input
																				// button
		logger.info("Value: " + guiElement.getValue());

		if (!Strings.isNullOrEmpty(getCurrentText(testContext, guiElement))) {
			clearInput(testContext, guiElement);
		}
		sendText(testContext, guiElement, value);

		// Build and click the specific element in the dropdown
		GuiElement dropDownElement = new GuiElement();
		dropDownElement.setIdentifier(IDENTIFIER_XPATH);
		dropDownElement.setKind(FrameworkKindEnum.SELECTWITHTEXT);
		dropDownElement.setName("virtualFilterSelectOption");
		if (guiElement.isAjax())
			dropDownElement.setAjax(true);
		String xpath = "//span[text()=\"" + value + "\"]";
		dropDownElement.setIdentValue(xpath);
		logger.info("clicking virtual dropdown element with xpath: " + xpath);
		// scrollTo(testContext, dropDownElement);
		if(!isElementPresent(testContext, dropDownElement)) {
			logger.info("the dropdown element " + value + " is not in the dropdown list");
			throw new RuntimeException(
				"[ERROR] Dropdown option with the value: \"" + value + "\" is not in the list. Verify values in the dropdown list");
		}
		// scrollTo(testContext, dropDownElement);
		clickDirty(testContext, dropDownElement);
	}

	private void selectWithoutText(TestContext testContext,
			GuiElement guiElement, String value) {

		//Build and click the specific element in the dropdown
		GuiElement comboboxOption = new GuiElement();
		comboboxOption.setIdentifier(IDENTIFIER_XPATH);
		comboboxOption.setKind(FrameworkKindEnum.BUTTON);
		comboboxOption.setName("comboboxOption");
		String xpath = "//a[@title='" + value + "']";
		comboboxOption.setIdentValue(xpath);
		
		logger.info("clicking dropdown element with value: " + value);
		
		waitForElementClickable(testContext, guiElement);
		click(testContext, guiElement);
		
		try {
		waitForElementVisible(testContext, comboboxOption, 1);
		} catch (TimeoutException e) {
			logger.info("combobox option with value " + value + " not visible" +
					"Clicking again the dropdown menu button");
			click(testContext, guiElement);
		}
		if(isElementPresent(testContext, comboboxOption)) {
			click(testContext, comboboxOption);
		} else {
			logger.info("the dropdown element " + value + " is not in the dropdown list");
			selectWithoutTextContains(testContext, guiElement, value);
		}
	}
	
	private void selectWithoutTextContains(TestContext testContext,
		GuiElement guiElement, String value) {
		GuiElement comboboxOption = new GuiElement();
		comboboxOption.setIdentifier(IDENTIFIER_XPATH);
		comboboxOption.setKind(FrameworkKindEnum.BUTTON);
		comboboxOption.setName("comboboxOption");
		String xpath = "//a[contains(@title,'" + value + "')]";
		comboboxOption.setIdentValue(xpath);
		
		//needed to expand dropdown again
		waitForElementClickable(testContext, guiElement);
		click(testContext, guiElement);
		
		logger.info("clicking dropdown element that contains value: " + value);
	
		try {
			waitForElementVisible(testContext, comboboxOption, 1);
			} catch (TimeoutException e) {
				logger.info("combobox option that contains value " + value + " not visible" +
						"Clicking again the dropdown menu button");
				click(testContext, guiElement);
			}
			if(isElementPresent(testContext, comboboxOption)) {
				click(testContext, comboboxOption);
			} else {
				logger.info("the dropdown element " + value + " is not in the dropdown list");
				throw new RuntimeException(
					"[ERROR] Dropdown option with the value: \"" + value + "\" is not in the list. Verify values in the dropdown list");
			}
	}

	private void selectCheckBox(TestContext testContext,
			GuiElement guiElement, String value) {
		boolean desiredValue = getValueToSetCheckBox(value);
		boolean currentValue = Boolean.parseBoolean(getCurrentText(testContext,
				guiElement));
		if (desiredValue ^ currentValue) {
			click(testContext, guiElement);
		}
	}

	private boolean getValueToSetCheckBox(String value) {
		if (value.equals("j") || value.equals("J") || value.equals("y")
				|| value.equals("Y") || value.equals("true")) {
			return true;
		}
		return false;
	}

	private boolean isCheckBox(GuiElement guiElement) {
		return guiElement.getKind() == FrameworkKindEnum.CHECKBOX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.test.sogeti.gui.web.ISGTGuiActions#clearInput(com.test.sogeti.testcase
	 * .TestContext, com.test.sogeti.gui.GuiElement)
	 */
	@Override
	public void clearInput(TestContext testContext, GuiElement guiElement) {
		findElement(testContext, guiElement).clear();
		if (guiElement.isAjax())
			sendTab(testContext, guiElement);
	}

	private void selectDropDown(TestContext testContext,
			GuiElement guiElement, String value) {

		String id;
		waitForElementVisible(testContext, guiElement, 30);
		click(testContext, guiElement);
		// in case the GuiElement does not use an id for identification we
		// resolve it
		if (!guiElement.getIdentifier().equalsIgnoreCase(
				IDENTIFIER_ID)) {
			id = findElement(testContext, guiElement).getAttribute("id");
		} else {
			id = guiElement.getIdentValue();
		}

		// Build and click the specific element in the dropdown
		String xpath = "//div[@id=\"" + id + "\"]/../../..//div[text()=\""
				+ value + "\"]";
		GuiElement dropDownElement = new GuiElement();
		dropDownElement.setIdentifier(IDENTIFIER_XPATH);
		dropDownElement.setIdentValue(xpath);
		dropDownElement.setKind(FrameworkKindEnum.DROPDOWN);
		if (guiElement.isAjax())
			dropDownElement.setAjax(true);
		waitForElementVisible(testContext, dropDownElement, 30);
		click(testContext, dropDownElement);
	}

	protected boolean isDropDown(GuiElement guiElement) {
		return guiElement.getKind() == FrameworkKindEnum.DROPDOWN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.test.sogeti.gui.web.ISGTGuiActions#select(com.test.sogeti.testcase
	 * .TestContext, com.test.sogeti.gui.GuiElement, java.lang.String)
	 */
	@Override
	public void select(TestContext testContext, GuiElement guiElement,
			String value) {
		logger.info("value: " + value);

		Select select = new Select(findElement(testContext, guiElement));
		try {
			select.selectByVisibleText(value);
		} catch (NoSuchElementException e) {
			List<WebElement> options = select.getOptions();
			for (WebElement option : options) {
				if (option.getText().startsWith(value)) {
					select.selectByVisibleText(option.getText());
					return;
				}
			}
			for (WebElement option : options) {
				if (option.getText().contains(value)) {
					select.selectByVisibleText(option.getText());
					return;
				}
			}
			for (WebElement option : options) {
				if (option.getText().toLowerCase()
						.contains(value.toLowerCase())) {
					select.selectByVisibleText(option.getText());
					return;
				}
			}
			for (WebElement option : options) {
				if (option.getText().equals(value.trim())) {
					select.selectByValue(option.getText());
				}
			}

			// no case found
			throw e;
		}
	}

	private boolean isComboBox(GuiElement guiElement) {
		return guiElement.getKind() == FrameworkKindEnum.COMBOBOX
				|| guiElement.getKind() == FrameworkKindEnum.SELECT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.test.sogeti.gui.web.ISGTGuiActions#sendText(com.test.sogeti.testcase
	 * .TestContext, com.test.sogeti.gui.GuiElement, java.lang.String)
	 */
	@Override
	public void sendText(TestContext testContext, GuiElement guiElement,
			String value) {
		logger.info("text: " + value);
		findElement(testContext, guiElement).sendKeys(value);

	}

	private boolean isTextField(GuiElement guiElement) {
		return guiElement.getKind() == FrameworkKindEnum.TEXTFIELD;
	}

	private boolean isSelectWithText(GuiElement guiElement) {
		return guiElement.getKind() == FrameworkKindEnum.SELECTWITHTEXT;
	}

	private boolean isSelectWithoutText(GuiElement guiElement) {
		return guiElement.getKind() == FrameworkKindEnum.SELECTWITHOUTTEXT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.test.sogeti.gui.web.ISGTGuiActions#click(com.test.sogeti.testcase
	 * .TestContext, com.test.sogeti.gui.GuiElement)
	 */
	@Override
	public void click(TestContext testContext, final GuiElement guiElement) {
		try {
			waitForElementClickable(testContext, guiElement);
				new WebDriverWait(
						((FrameworkWebTestContext) testContext).getDriver(), 120)
						.ignoring(StaleElementReferenceException.class).until(
						(WebDriver driver) -> {
								driver.findElement(getBy(guiElement))
										.click();
								return true;
						});

			} catch (Exception e) {
			logger.error("Element not Present: " + guiElement);
			System.err.println("Element not Present: " + guiElement);
			e.printStackTrace();
			throw (e);
		}
	}


	@Override
	public void useSpecifiedValue(TestContext testContext,
			GuiElement guiElement, String value) {
		logger.debug(guiElement);
		guiElement.setValue(value);
		applyValue(testContext, guiElement);
	}

	public String resolveTestDataForGuiElement(TestContext testContext,
			GuiElement guiElement) {
		logger.debug(guiElement);
		String returnValue = resolveTestDataForGuiElement(testContext,
				guiElement.getName());
		guiElement.setValue(returnValue);
		return returnValue;
	}

	public String resolveTestDataForGuiElement(TestContext testContext,
			String valueName) {
		Element testData = testContext.getTestData();
		if (testData == null) {
			throw new RuntimeException("No Testdata found");
		}
		String returnValue = null;
		List<Node> children = getAllChildNodesRecursive(testData);
		for (Node child : children) {
			if (child.getNodeName().equalsIgnoreCase(valueName)
					&& child.getAttributes().getLength() == 0) {
				returnValue = child.getTextContent();
			}
		}
		String resolvedString = FrameworkPropertiesManager
				.resolveVariable(returnValue);
		if (resolvedString != null) {
			returnValue = resolvedString;
		}
		return returnValue;
	}

	private static List<Node> getAllChildNodesRecursive(Node testData) {
		List<Node> returnChildren = new ArrayList<Node>();
		NodeList directChildren = testData.getChildNodes();
		for (int i = 0; i < directChildren.getLength(); i++) {
			Node child = directChildren.item(i);
			if (child.hasChildNodes()) {
				returnChildren.addAll(getAllChildNodesRecursive(child));
				returnChildren.add(child);
			}
		}
		return returnChildren;
	}

	public List<WebElement> getAllMatchingElements(TestContext testContext,
			GuiElement guiElement) {
		return ((FrameworkWebTestContext) testContext).getDriver().findElements(
				getBy(guiElement));
	}

	public void switchToFrame(TestContext testContext, String frameName) {
		logger.info("Switch to frame: " + frameName);
		((FrameworkWebTestContext) testContext).getDriver().switchTo()
				.frame(frameName);
	}

	public void switchToFrame(TestContext testContext, int frameNum) {
		logger.info("Switch to frame number: " + frameNum);
		((FrameworkWebTestContext) testContext).getDriver().switchTo()
				.frame(frameNum);
	}

	public void switchToDefaultFrame(TestContext testContext) {
		logger.info("Switch to Defaultframe");
		((FrameworkWebTestContext) testContext).getDriver().switchTo()
				.defaultContent();
	}

	public boolean isElementClickable(TestContext testContext,
			GuiElement guiElement) {

		WebElement element = ((FrameworkWebTestContext) testContext).getDriver()
				.findElement(By.id("co-grid-shipping-customer-confirmation"));
		if (element.isDisplayed() && element.isEnabled()) {
			return true;
		}
		return false;
	}

	public void waitForElementInvisible(TestContext testContext,
			GuiElement guiElement, int timeout) {
		WebDriverWait wait = new WebDriverWait(
				((FrameworkWebTestContext) testContext).getDriver(), timeout);
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(getBy(guiElement)));
	}

	public void waitForElementVisible(TestContext testContext,
			GuiElement guiElement, int timeout) {
		WebDriverWait wait = new WebDriverWait(
				((FrameworkWebTestContext) testContext).getDriver(), timeout);
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(getBy(guiElement)));
	}
	
	public void waitForElementWithAttribute(TestContext testContext, GuiElement guiElement, 
			String attributeName, String attributeValue, int timeout) {
		WebDriverWait wait = new WebDriverWait(
				((FrameworkWebTestContext) testContext).getDriver(), timeout);
		wait.until(ExpectedConditions
				.attributeToBe(getBy(guiElement), attributeName, attributeValue));			
	}
	
	public void waitForElementContainsAttribute(TestContext testContext, GuiElement guiElement, 
			String attributeName, String attributeValue, int timeout) {
		WebDriverWait wait = new WebDriverWait(
				((FrameworkWebTestContext) testContext).getDriver(), timeout);
		wait.until(ExpectedConditions
				.attributeContains(getBy(guiElement), attributeName, attributeValue));		
	}
	
	public void waitForElementText(TestContext testContext, GuiElement guiElement, 
			String text, int timeout) {
		WebDriverWait wait = new WebDriverWait(
				((FrameworkWebTestContext) testContext).getDriver(), timeout);
		wait.until(ExpectedConditions
				.textToBe(getBy(guiElement), text));
	}
	

	public void waitForElementClickable(TestContext testContext,
			GuiElement guiElement) {
		waitForElementClickable(testContext, guiElement, 30);
	}

	public void waitForElementClickable(TestContext testContext,
			GuiElement guiElement, int timeout) {
		WebDriverWait wait = new WebDriverWait(
				((FrameworkWebTestContext) testContext).getDriver(), timeout);
		wait.until(ExpectedConditions
				.elementToBeClickable(getBy(guiElement)));
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.test.sogeti.gui.web.ISGTGuiActions#isElementPresent(com.test.sogeti
	 * .testcase.TestContext, com.test.sogeti.gui.GuiElement)
	 */
	@Override
	public boolean isElementPresent(TestContext testContext,
			GuiElement guiElement) {
		return ((FrameworkWebTestContext) testContext).getDriver()
				.findElements(getBy(guiElement)).size() > 0
				&& findElement(testContext, guiElement).isDisplayed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.test.sogeti.gui.web.ISGTGuiActions#getCurrentText(com.test.sogeti
	 * .testcase.TestContext, com.test.sogeti.gui.GuiElement)
	 */
	@Override
	public String getCurrentText(TestContext testContext,
			GuiElement guiElement) {
		if (!isElementPresent(testContext, guiElement)) {
			return null;
		}
		String returnValue = findElement(testContext, guiElement).getText();
		if (returnValue.length() == 0) {
			if (!isCheckBox(guiElement)) {
				returnValue = findElement(testContext, guiElement)
						.getAttribute("value");
			} else {
				returnValue = findElement(testContext, guiElement)
						.getAttribute("checked");
				if (returnValue != null) {
					returnValue = "true";
				} else {
					returnValue = "false";
				}
			}
		}
		return returnValue;
	}

	public boolean isChecked(TestContext testContext,
			GuiElement guiElement) {
		if (isCheckBox(guiElement)) {
			if (findElement(testContext, guiElement).getAttribute("checked") != null) {
				return true;
			}
			// sometimes radiobuttons are implemented as classes
			// "radiobutton-checked" and "radiobutton-not-checked"
			if (findElement(testContext, guiElement).getAttribute("class") != null) {
				String classes = findElement(testContext, guiElement)
						.getAttribute("class");
				for (String c : classes.split(" ")) {
					if (c.equals("radiobutton-checked")) {
						return true;
					}
				}
			}

			else {
				return false;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.test.sogeti.gui.web.ISGTGuiActions#sendTab(com.test.sogeti.testcase
	 * .TestContext, com.test.sogeti.gui.GuiElement)
	 */
	@Override
	public void sendTab(TestContext testContext, GuiElement guiElement) {
		if (guiElement.isAjax()) {
			if (!isPerformanceEnabled()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			findElement(testContext, guiElement).sendKeys(Keys.TAB);
		}
	}

	private boolean isPerformanceEnabled() {
		return System.getProperty(PERFORMANCE_MEASSUREMENT,
				"false").equals("true");
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.test.sogeti.gui.web.ISGTGuiActions#submit(com.test.sogeti.testcase
	 * .TestContext, com.test.sogeti.gui.GuiElement)
	 */
	@Override
	public void submit(TestContext testContext, GuiElement guiElement) {
		findElement(testContext, guiElement).sendKeys(Keys.ENTER);
	}

	public void switchToWindowTitleContaining(TestContext testContext,
			String windowTitle) {
		Set<String> windows = ((FrameworkWebTestContext) testContext).getDriver()
				.getWindowHandles();
		for (String window : windows) {
			((FrameworkWebTestContext) testContext).getDriver().switchTo()
					.window(window);
			if (((FrameworkWebTestContext) testContext).getDriver().getTitle()
					.contains(windowTitle)) {
				return;
			}
		}
	}

	@Override
	public boolean isElementEnabled(TestContext testContext,
			GuiElement guiElement) {
		if (isDropDown(guiElement)) {
			return isElementEnabledDropDown(testContext, guiElement);
		}
		WebElement element = findElement(testContext, guiElement);
		boolean hasClassDisabled = element.getAttribute("class") != null ? element
				.getAttribute("class").contains("disabled") : false;
		guiElement.setIdentValue(guiElement.getStaticIdentValue());
		return element.isEnabled() && !hasClassDisabled;
	}

	private boolean isElementEnabledDropDown(TestContext testContext,
			GuiElement guiElement) {
		String oldIdentifier = guiElement.getIdentifier();
		guiElement.setIdentifier(IDENTIFIER_XPATH);
		guiElement.setIdentValue("//div[@id='" + guiElement.getIdentValue()
				+ "']/..");
		WebElement element = findElement(testContext, guiElement);
		boolean hasClassDisabled = element.getAttribute("class") != null ? element
				.getAttribute("class").contains("disabled") : false;
		guiElement.setIdentValue(guiElement.getStaticIdentValue());
		guiElement.setIdentifier(oldIdentifier);
		return element.isEnabled() && !hasClassDisabled;
	}

	public String resolveTestDataForGuiElement(TestContext testContext,
			GuiElement guiElement, String id) {
		logger.debug(guiElement);
		if (id == null) {
			return resolveTestDataForGuiElement(testContext, guiElement);
		}

		String returnValue = resolveTestDataForGuiElement(testContext,
				guiElement.getName(), id);
		guiElement.setValue(returnValue);
		return returnValue;
	}

	@Override
	public String resolveTestDataForGuiElement(TestContext testContext,
			String valueName, String id) {
		Element testData = testContext.getTestData();
		String returnValue = null;
		List<Node> children = getAllChildNodesRecursive(testData);
		for (Node child : children) {
			if (child.getNodeName().equalsIgnoreCase(valueName)
					&& child.getAttributes().getLength() > 0
					&& child.getAttributes()
							.getNamedItem(TESTDATA_ID)
							.getTextContent().equals(id)) {
				returnValue = child.getTextContent();
			}
		}
		String resolvedString = FrameworkPropertiesManager.resolveVariable(returnValue);
		if (resolvedString != null) {
			returnValue = resolvedString;
		}
		if (returnValue == null) {
			returnValue = resolveTestDataForGuiElement(testContext, valueName);
		}

		return returnValue;
	}

	public void clickDirty(TestContext testContext, GuiElement guiElement) {
		waitForElementClickable(testContext, guiElement);

		switch (guiElement.getIdentifier()) {
		case IDENTIFIER_ID:
			((JavascriptExecutor) ((FrameworkWebTestContext) testContext).getDriver())
					.executeScript(
							"document.getElementById('"
									+ guiElement.getStaticIdentValue()
									+ "').click(); ",
							findElement(testContext, guiElement));
			break;
		default:
			((JavascriptExecutor) ((FrameworkWebTestContext) testContext).getDriver())
					.executeScript("arguments[0].click();",
							findElement(testContext, guiElement));
			break;
		}

	}

	public void scrollTo(TestContext testContext, GuiElement guiElement) {
		switch (guiElement.getIdentifier()) {
		case IDENTIFIER_ID:
			((JavascriptExecutor) ((FrameworkWebTestContext) testContext).getDriver())
					.executeScript(
							"document.getElementById('"
									+ guiElement.getStaticIdentValue()
									+ "').scrollIntoView(true);",
							findElement(testContext, guiElement));
			break;
		default:
			((JavascriptExecutor) ((FrameworkWebTestContext) testContext).getDriver())
					.executeScript("arguments[0].scrollIntoView(true);",
							findElement(testContext, guiElement));
			break;
		}
	}

	/**
	 * added by Gilles: use a drag able element(Scroll bar) to scroll until the
	 * element to find is in view.
	 * 
	 * 
	 * @param testContext     : the actual TestContext
	 * @param dragableElement : the scroll bar to drag
	 * @param element         : the element to be found/ or element which should be
	 *                        visible.
	 */
	public void dragUntilElementIsVisible(TestContext testContext, GuiElement dragableElement,
			GuiElement element) {

		WebElement elementToFind = findElement(testContext, element);
		WebElement dragablelementToFind = findElement(testContext, dragableElement);

		Point elementLocation = elementToFind.getLocation();
		Point dragableElementLocation = dragablelementToFind.getLocation();
		int velocityPixel = 5;

		// make sure that the scroll bar in DLG dialog visilble
		if (!dragablelementToFind.isDisplayed())
			restoreScrollBar(testContext);
		if (elementLocation.getY() >= dragableElementLocation.getY()) {
			while (!isVisibleInViewport(testContext, elementToFind)) {
				try {
					drag(testContext, dragableElement, velocityPixel);
					velocityPixel += velocityPixel;
				} catch (Exception e) {
					logger.catching(e);

				}
			}
		} else if (elementLocation.getY() <= dragableElementLocation.getY()) {
			while (!isVisibleInViewport(testContext, elementToFind)) {
				try {
					drag(testContext, dragableElement, -velocityPixel);
					velocityPixel += velocityPixel;
				} catch (Exception e) {
					logger.catching(e);
				}
			}
		}
	}

	public void executeScript(TestContext testContext, String script) {
		JavascriptExecutor js = (JavascriptExecutor) ((FrameworkWebTestContext) testContext)
				.getDriver();
		js.executeScript(script);
	}

	public boolean elementExists(TestContext testContext,
			GuiElement guiElement) {

		return ((FrameworkWebTestContext) testContext).getDriver()
				.findElements(getBy(guiElement)).size() > 0;
	}

	/**
	 * added by Tatiana Nikolaeva clicks the scroll bar and drags it down or up
	 * by the number of pixels
	 * 
	 * @param draggableScrollbar
	 *            : the scroll bar to drag
	 * @param numberOfPixelsToDragTheScrollbar
	 *            : the number of pixels to drag. If the number is positive -
	 *            drags downwards, if negative - upwards
	 */
	public void drag(TestContext testContext,
			GuiElement draggableScrollbar,
			int numberOfPixelsToDragTheScrollbar) {
		WebDriver driver = ((FrameworkWebTestContext) testContext).getDriver();
		Actions dragger = new Actions(driver);

		// scroll bar to drag
		WebElement draggablePartOfScrollbar = driver
				.findElement(getBy(draggableScrollbar));

		// drag downwards/upwards by the number of pixels
		dragger.moveToElement(draggablePartOfScrollbar).clickAndHold()
				.moveByOffset(0, numberOfPixelsToDragTheScrollbar).release()
				.perform();

	}

	private void sleep(int timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * added by Tatiana Nikolaeva: waits for ajax request to complete
	 * 
	 * @param maxTimeToWait
	 *            : maximum time to wait
	 */
	public void waitForAjaxToFinish(TestContext testContext,
			int maxTimeToWait) {
		WebDriver driver = ((FrameworkWebTestContext) testContext).getDriver();

		boolean timeoutWaiting = false;
		boolean javaScriptReady = false;
		boolean jQueryLoaded = false;
		int timeWaited = 0;

		while (!javaScriptReady && !jQueryLoaded && !timeoutWaiting) {
			sleep(50);
			timeWaited += 50;
			timeoutWaiting = timeWaited <= maxTimeToWait;

			javaScriptReady = ((JavascriptExecutor) driver)
					.executeScript("return document.readyState").toString()
					.equals("complete");

			jQueryLoaded = (Boolean) ((JavascriptExecutor) driver)
	                .executeScript("return window.jQuery != undefined && jQuery.active === 0");

		}

	}

	/**
	 * added by Tatiana Nikolaeva: hovers over Webelement without clicking it
	 */
	public void hoverOverElement(TestContext testContext,
			GuiElement guiElement) {
		WebElement element = findElement(testContext, guiElement);
		Actions builder = new Actions(
				((FrameworkWebTestContext) testContext).getDriver());
		builder.moveToElement(element).build().perform();
	}

	public void selectByIndex(TestContext testContext,
			GuiElement dropdown, int index) {
		Select dropdownList = new Select(((FrameworkWebTestContext) testContext)
				.getDriver().findElement(getBy(dropdown)));
		dropdownList.selectByIndex(index);
	}
	
	/**
	 * added by Tatiana Nikolaeva: sets size for the browser window
	 * @param testContext
	 * @param width of browser window
	 * @param height of browser window
	 */
	public void setWindowSize(TestContext testContext, int width, int height){
		((FrameworkWebTestContext) testContext).getDriver().manage().window().setSize(new Dimension(width, height));
	}
	
	/**
	 * added by Tatiana Nikolaeva: accepts the currently displayed alert pop up
	 * @param testContext
	 */
	public void acceptAlert(TestContext testContext) {
		try {
			Alert alert = ((FrameworkWebTestContext) testContext).getDriver()
					.switchTo().alert();
			alert.accept();
		} catch (NoAlertPresentException ex) {
			// Alert not present
			logger.info("No Alert window displayed");
		}
	}
	
	/**
	 * added by Lukas: wait for element to be clickable for a specified timeout
	 * @param guiElement element that we are waiting for
	 */
	
	public void waitForElementToHide(TestContext testContext, GuiElement guiElement,
			int timeout) {
		WebDriverWait wait = new WebDriverWait(
				((FrameworkWebTestContext) testContext).getDriver(), timeout);
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(getBy(guiElement)));
	}
	

	/**
	 * added by Gilles: try through resizing a window, to make the Scroll bar in DLG
	 * Dialog visible.
	 * 
	 * @param TestContext
	 */
	public void restoreScrollBar(TestContext testContext) {
		Dimension actualSize = ((FrameworkWebTestContext) testContext).getDriver().manage().window().getSize();
		((FrameworkWebTestContext) testContext).getDriver().manage().window().maximize();
		((FrameworkWebTestContext) testContext).getDriver().manage().window().setSize(actualSize);

	}

	/**
	 * added by Gilles: Determine with the Java script and the coordinate of an
	 * element, it the element is visible in Viewport,meaning the element is
	 * actually been seen in view.
	 *
	 */
	public static Boolean isVisibleInViewport(TestContext testContext, WebElement element) {
		WebDriver driver = ((FrameworkWebTestContext) testContext).getDriver();

		return (Boolean) ((JavascriptExecutor) driver).executeScript(
				"var elem = arguments[0],                 " + "  box = elem.getBoundingClientRect(),    "
						+ "  cx = box.left + box.width / 2,         " + "  cy = box.top + box.height / 2,         "
						+ "  e = document.elementFromPoint(cx, cy); " + "for (; e; e = e.parentElement) {         "
						+ "  if (e === elem)                        " + "    return true;                         "
						+ "}                                        " + "return false;                            ",
				element);
	}
	
	/**
	 * added by Lukas: different than scrollTo in such a way that it does not
	 * scroll until the element is on the top of the page but it scrolls to
	 * get the element to the bottom of the page
	 * @param guiElement
	 */
	public void scrollToBottom(TestContext testContext, GuiElement guiElement) {
		switch (guiElement.getIdentifier()) {
		case IDENTIFIER_ID:
			((JavascriptExecutor) ((FrameworkWebTestContext) testContext).getDriver())
					.executeScript(
							"document.getElementById('"
									+ guiElement.getStaticIdentValue()
									+ "').scrollIntoView(false);",
							findElement(testContext, guiElement));
			break;
		default:
			((JavascriptExecutor) ((FrameworkWebTestContext) testContext).getDriver())
					.executeScript("arguments[0].scrollIntoView(false);",
							findElement(testContext, guiElement));
			break;
		}
	}
	
	/**
	 * Added By Zuhair
	 * This Method Waits until the provide Frame is available and then switch to it
	 * @param testContext
	 * @param frameName
	 * @param timeout
	 */
	@Override
	public void waitForFrameToBeAvailableAndSwitchToIt(TestContext testContext, String frameName, int timeout) {
		WebDriverWait wait = new WebDriverWait(
				((FrameworkWebTestContext) testContext).getDriver(), timeout);
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
	}
	
	public void waitForFrameByAttributeAndSwitchToIt(TestContext testContext, GuiElement frameLocator, int timeout) {
		WebDriverWait wait = new WebDriverWait(
				((FrameworkWebTestContext) testContext).getDriver(), timeout);
		try {
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(getBy(frameLocator)));
		} catch (TimeoutException e) {
			//remove catch once analysed
			logger.info("frame not found, verify the frame locator!");
		}
	}
	
	
	public void waitForPageToLoad(TestContext testContext, int timeout) {
		WebDriver driver = ((FrameworkWebTestContext) testContext).getDriver();
		
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
                    }
                };
        
       WebDriverWait wait = new WebDriverWait(driver, timeout);
       wait.until(pageLoadCondition);
	}
	
	
	public void scrollToMiddle(TestContext testContext, GuiElement guiElement) {
		WebDriver driver = ((FrameworkWebTestContext) testContext).getDriver();
		
		String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                + "var elementTop = arguments[0].getBoundingClientRect().top;"
                + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

		WebElement element = findElement((FrameworkWebTestContext) testContext, guiElement);
		
		( (JavascriptExecutor) driver).executeScript(scrollElementIntoMiddle, element);
	}
	
	public void scrollBy(TestContext testContext, int x, int y) {
		WebDriver driver = ((FrameworkWebTestContext) testContext).getDriver();
		( (JavascriptExecutor) driver).executeScript("window.scrollBy(" + x + "," + y + ")");
		
	}
	
	public void scrollToTop(TestContext testContext) {
		WebDriver driver = ((FrameworkWebTestContext) testContext).getDriver();
		
		( (JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}
	
	public void setInputValue(TestContext testContext, GuiElement guiElement, String value) {
		WebDriver driver = ((FrameworkWebTestContext) testContext).getDriver();
		WebElement element = findElement((FrameworkWebTestContext) testContext, guiElement);
		
		( (JavascriptExecutor) driver).executeScript("arguments[0].value='" + value + "';", element);
	}
	
	public void sendKeyUp(TestContext testContext, GuiElement guiElement, int count) {
		for(int i = 0; i < count; i++) {
			findElement(testContext, guiElement).sendKeys(Keys.UP);
		}
	}

	public String getSource(TestContext testContext) {
		return ((FrameworkWebTestContext) testContext).getDriver().getPageSource();
	}

}