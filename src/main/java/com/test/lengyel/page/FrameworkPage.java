package com.test.lengyel.page;

import com.test.lengyel.actions.FrameworkAssertActions;
import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.gui.GuiElement;
import com.test.lengyel.gui.IFrameworkGuiActions;
import com.test.lengyel.gui.ShadowGuiActions;
import com.test.lengyel.testcase.FrameworkPropertiesManager;
import com.test.lengyel.testcase.TestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.GregorianCalendar;


public abstract class FrameworkPage {
    protected static final Logger logger = LogManager.getLogger(FrameworkPage.class);

    protected TestContext testContext;
    protected IFrameworkGuiActions guiActions;

    public FrameworkPage(TestContext testContext) {
        this.testContext = testContext;
    }

    public TestContext getTestContext() {
        return testContext;
    }

    public void setTestContext(TestContext testContext) {
        this.testContext = testContext;
    }

    public IFrameworkGuiActions getGuiActions() {
        return guiActions;
    }

    public void setGuiActions(IFrameworkGuiActions guiActions) {
        String shadowModeProperty = System.getProperty(FrameworkConstants.SHADOW_MODE, "false");
        if (shadowModeProperty.equalsIgnoreCase("true")) {
            this.guiActions = new ShadowGuiActions();
        } else {
            this.guiActions = guiActions;
        }
    }

    public void clickAndReplaceSearchString(GuiElement guiElement, String... replacements) {
        replaceSearchString(guiElement, replacements);
        sleep(2000);
        click(guiElement);
    }

    public void useSpecificValueAndReplaceSearchString(GuiElement guiElement, String searchString, String value) {
        replaceSearchString(guiElement, searchString);
        waitForElementVisible(guiElement);
        useSpecifiedValue(guiElement, value);
    }

    /**
     * Replaces #searchString in the identification value of the guiElement with
     * the supplied replacement values. If more than one replacement is used,
     * the identification value must use #searchString with increasing number
     * appended i.e. #searchString1 #searchString2 ...
     *
     * @param guiElement
     * @param replacements
     */
    public void replaceSearchString(GuiElement guiElement, String... replacements) {
        // replace identification value
        try {
            if (replacements.length == 1) {
                guiElement.setIdentValue(guiElement.getStaticIdentValue().replace("#searchString", replacements[0]));
            } else {
                String identValue = guiElement.getStaticIdentValue();
                for (int i = 0; i < replacements.length; i++) {
                    identValue = identValue.replace("#searchString" + (i + 1), replacements[i]);
                }
                guiElement.setIdentValue(identValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void useCorrectValue(GuiElement guiElement) {
        guiActions.useCorrectValue(testContext, guiElement);
    }

    public void useCorrectValue(GuiElement guiElement, String id) {
        guiActions.useCorrectValue(testContext, guiElement, id);
    }

    public void click(GuiElement guiElement) {
        guiActions.click(testContext, guiElement);
    }

    public void useSpecifiedValue(GuiElement guiElement, String value) {
        guiActions.useSpecifiedValue(testContext, guiElement, value);
    }

    protected String getProperty(String propertyName) {
        return getProperty(propertyName, true);
    }

    protected String getProperty(String propertyName, boolean useEnvironmentValue) {
        return FrameworkPropertiesManager.getFunctionalProperty(testContext, propertyName, useEnvironmentValue);
    }

    public String resolveTestDataForGuiElement(GuiElement guiElement) {
        return guiActions.resolveTestDataForGuiElement(testContext, guiElement);
    }

    public String resolveTestData(String valueName) {
        return guiActions.resolveTestDataForGuiElement(testContext, valueName);
    }

    public String resolveTestData(String valueName, String id) {
        return guiActions.resolveTestDataForGuiElement(testContext, valueName, id);
    }

    public String resolveTestDataForGuiElement(GuiElement guiElement, String id) {
        return guiActions.resolveTestDataForGuiElement(testContext, guiElement, id);
    }

    public void waitForElementInvisible(GuiElement guiElement) {
        waitForElementInvisible(guiElement, 30);
    }



    public boolean isElementPresent(GuiElement guiElement) {
        return guiActions.isElementPresent(testContext, guiElement);
    }

    public boolean isElementEnabled(GuiElement guiElement) {
        return guiActions.isElementEnabled(testContext, guiElement);
    }

    public <T extends FrameworkPage> T getPage(Class<T> clazz) {
        return FrameworkPageFactory.getPage(clazz, getTestContext());
    }

    public String getCurrentText(GuiElement guiElement) {
        return guiActions.getCurrentText(testContext, guiElement);
    }

    public FrameworkAssertActions assertActions() {
        return getTestContext().getAssertActions();
    }

    public void openLink(String linkText) {
        GuiElement guiElement = new GuiElement();
        guiElement.setName("Link");
        guiElement.setIdentifier(FrameworkConstants.IDENTIFIER_LINKTEXT);
        guiElement.setIdentValue(linkText);
        if(isElementPresent(guiElement))
            guiActions.click(testContext, guiElement);
        else {
            throw new RuntimeException(
                    "[ERROR] The element with link text \"" + linkText + "\" is not present.");
        }
    }

    public void useSpecifiedValue(GuiElement guiElement, int value) {
        useSpecifiedValue(guiElement, "" + value);
    }

    public void sendTab(GuiElement guiElement) {
        guiActions.sendTab(testContext, guiElement);
    }

    public void submit(GuiElement guiElement) {
        guiActions.submit(testContext, guiElement);
    }

    public void clearInput(GuiElement guiElement) {
        guiActions.clearInput(testContext, guiElement);
    }

    public void sleep(int timeInMillis) {
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void storeProperty(String propertyName, String value) {
        if(value == null){
            return;
        }
        FrameworkPropertiesManager.setFunctionalProperty(testContext, propertyName, value);
    }

    public String getValueFromXML(String xmlString, String tagName) {
        String openingTag = "<" + tagName + ">";
        String closingTag = "</" + tagName + ">";
        String value = xmlString.substring(xmlString.indexOf(openingTag) + openingTag.length(), xmlString.indexOf(closingTag));
        return value;
    }

    public void waitForElementInvisible(GuiElement guiElement, int timeout) {
        guiActions.waitForElementInvisible(testContext, guiElement, timeout);
    }

    public void waitForElementVisible(GuiElement guiElement) {
        waitForElementVisible(guiElement, 30);
    }

    public void waitForElementVisible(GuiElement guiElement, int timeout) {
        guiActions.waitForElementVisible(testContext, guiElement, timeout);
    }

    /**
     * Returns a calendar object for a given constant String
     *
     * @param dateString
     *            - one of {@link FrameworkConstants#TODAY}
     *            {@link FrameworkConstants#YESTERDAY} {@link FrameworkConstants#TOMORROW}
     * @return if nothing matches {@code dateString} the method returns null
     */
    public Calendar getDate(String dateString) {
        Calendar cal = GregorianCalendar.getInstance();

        switch (dateString) {
            case FrameworkConstants.YESTERDAY:
                cal.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case FrameworkConstants.TOMORROW:
                cal.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case FrameworkConstants.TODAY:
                // do nothing
                break;
            default:
                // could not interpret
                cal = null;
        }

        return cal;
    }

    /**
     * to translate a label in an element locator
     * labels in selectors are not marked specifically, just a standard #searchString
     * @param guiElement which has a label value in xpath
     * @param market as default language depends on the market
     */
    public void translateLocatorWithLabel(GuiElement guiElement, String market) {
        String labelTranslation = getLabelTranslation(guiElement, market);
        replaceSearchString(guiElement, labelTranslation);
    }

    /**
     * to return a translation of a label in an element locator
     * labels in selectors are not marked specifically, just a standard #searchString
     * this method is needed in case more than one #searchString should be replaced
     * @param guiElement which has a label value in xpath
     * @param market as default language depends on the market
     */
    public String getLabelTranslation(GuiElement guiElement, String market) {
        String label = guiElement.getName();
        String labelTranslation = com.test.lengyel.helper.FrameworkTranslationHelper.getLabelTranslation(label, market);
        return labelTranslation;
    }

    public void waitForFrameToBeAvailableAndSwitchToIt(String frameName, int timeout) {
        guiActions.waitForFrameToBeAvailableAndSwitchToIt(testContext, frameName, timeout);
    }

    public void waitForPageToLoad(int timeout) {
        guiActions.waitForPageToLoad(testContext, timeout);
    }

    public void scrollToMiddle(GuiElement guiElement) {
        guiActions.scrollToMiddle(testContext, guiElement);
    }

    public void scrollToTop() {
        guiActions.scrollToTop(testContext);
    }

    public void scrollBy(int x, int y) {
        guiActions.scrollBy(testContext, x, y);
    }

    public void setInputValue(GuiElement guiElement, String value) {
        guiActions.setInputValue(testContext, guiElement, value);
    }

    public void waitForElementWithAttribute(GuiElement guiElement, String attributeName,
                                            String attributeValue, int timeout) {
        guiActions.waitForElementWithAttribute(testContext, guiElement, attributeName, attributeValue, timeout);
    }

    public void waitForElementContainsAttribute(GuiElement guiElement, String attributeName,
                                                String attributeValue, int timeout) {
        guiActions.waitForElementContainsAttribute(testContext, guiElement, attributeName, attributeValue, timeout);
    }

    public void waitForElementText(GuiElement guiElement, String text, int timeout) {
        guiActions.waitForElementText(testContext, guiElement, text, timeout);
    }

    public void waitForFrameByAttributeAndSwitchToIt(GuiElement guiElement, int timeout) {
        guiActions.waitForFrameByAttributeAndSwitchToIt(testContext, guiElement, timeout);
    }

    public void sendText(GuiElement guiElement, String value) {
        guiActions.sendText(testContext, guiElement, value);
    }

    public void sendKeyUp(GuiElement guiElement, int count) {
        guiActions.sendKeyUp(testContext, guiElement, count);
    }
}

