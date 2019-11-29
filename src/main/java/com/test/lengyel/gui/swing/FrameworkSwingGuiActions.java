package com.test.lengyel.gui.swing;

import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.gui.FrameworkKindEnum;
import com.test.lengyel.gui.GuiElement;
import com.test.lengyel.gui.IFrameworkGuiActions;
import com.test.lengyel.gui.web.FrameworkWebGuiActions;
import com.test.lengyel.testcase.TestContext;
import com.test.lengyel.testcase.swing.FrameworkSwingTestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fest.swing.core.Robot;
import org.fest.swing.core.*;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.timing.Condition;
import org.fest.swing.timing.Pause;
import org.testng.collections.Lists;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.concurrent.*;

public class FrameworkSwingGuiActions implements IFrameworkGuiActions {

	private static final Logger logger = LogManager.getLogger(FrameworkSwingGuiActions.class);

	@Override
	public void useCorrectValue(TestContext testContext, GuiElement guiElement) {
		logger.debug(guiElement);
		resolveTestDataForGuiElement(testContext, guiElement);
		applyValue(testContext, guiElement);
	}

	@Override
	public void useCorrectValue(TestContext testContext, GuiElement guiElement, String id) {
		logger.debug(guiElement);
		resolveTestDataForGuiElement(testContext, guiElement, id);
		applyValue(testContext, guiElement);
	}

	@Override
	public void applyValue(TestContext testContext, GuiElement guiElement) {
		if (guiElement.getValue() == null) {
			return;
		}
		if (isTextField(guiElement)) {
			sendText(testContext, guiElement, guiElement.getValue());
		} else if (isList(guiElement)) {
			select(testContext, guiElement, guiElement.getValue());
		} else if (isCombobox(guiElement)) {
			// selectCombo(testContext, guiElement, guiElement.getValue());
		} else if (isTable(guiElement)) {
			selectTable(testContext, guiElement, guiElement.getValue());
		} else {
			throw new RuntimeException("Unsupported Kind:" + guiElement.getKind());
		}
	}

	// private void selectCombo(TestContext testContext, GuiElement
	// guiElement, String value) {
	// Map<String, Integer> values = new HashMap<String, Integer>();
	// MultiColumnComboBox combo = ((MultiColumnComboBox) component(guiElement,
	// frame(testContext).robot));
	// int index = -1;
	// for (int i = 0; i < combo.getItemCount(); i++) {
	// String temp = combo.getItemAt(i).toString();
	// values.put(temp, i);
	// if (temp.contains(value)) {
	// index = i;
	// }
	// }
	// if (index == -1) {
	// throw new RuntimeException("Value " + value + " not Present in values " +
	// values.keySet());
	// }
	// combo.setSelectedIndex(index);
	// }

	private boolean isCombobox(GuiElement guiElement) {
		return guiElement.getKind() == FrameworkKindEnum.COMBOBOX;
	}

	private void selectTable(TestContext testContext, GuiElement guiElement, String value) {
		((JTable) component(guiElement, robot(testContext, guiElement))).changeSelection(0, 0, true, false);
	}

	private boolean isTable(GuiElement guiElement) {
		return guiElement.getKind() == FrameworkKindEnum.TABLE;
	}

	private boolean isList(GuiElement guiElement) {
		return guiElement.getKind() == FrameworkKindEnum.LIST;
	}

	@Override
	public void clearInput(TestContext testContext, GuiElement guiElement) {
		sendText(testContext, guiElement, "");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void select(TestContext testContext, GuiElement guiElement, String value) {
		new JListFixture(frame(testContext).robot, (JList) component(guiElement, frame(testContext).robot)).selectItem(value);
	}

	public void selectTab(TestContext testContext, GuiElement guiElement, String tabToChoose) {
		new JTabbedPaneFixture(frame(testContext).robot, (JTabbedPane) component(guiElement, frame(testContext).robot)).selectTab(tabToChoose);
	}

	@Override
	public void sendText(TestContext testContext, GuiElement guiElement, String value) {
		logger.debug(guiElement);
		robot(testContext, guiElement).enterText(value);
	}

	private Robot robot(final TestContext testContext, final GuiElement guiElement) {
		try {
			if (!guiElement.getKind().name().equals(FrameworkConstants.KIND_TABLE)) {
				ExecutorService executor = Executors.newCachedThreadPool();
				Callable<Object> task = new Callable<Object>() {
					public Object call() {
						frame(testContext).robot.focusAndWaitForFocusGain(component(guiElement, frame(testContext).robot));
						return new Object();
					}
				};
				Future<Object> future = executor.submit(task);
				try {
					future.get(5, TimeUnit.SECONDS);
				} catch (Exception ex) {
				} finally {
					future.cancel(true); // may or may not desire this
				}

			}
		} catch (Exception e) {
		}
		return frame(testContext).robot;
	}

	@SuppressWarnings("unchecked")
	private Component component(GuiElement guiElement, Robot robot) {
		Component component = null;
		try {
			if (guiElement.getIdentifier().equalsIgnoreCase(FrameworkConstants.IDENTIFIER_NAME)) {
				if (guiElement.getKind() != null && guiElement.getKind() != FrameworkKindEnum.TABLE && guiElement.getKind() != FrameworkKindEnum.COMBOBOX) {
					component = robot.finder().findByName(guiElement.getIdentValue(), getComponentClassForKind(guiElement));
				} else {
					component = robot.finder().findByName(guiElement.getIdentValue());
				}
			} else if (guiElement.getIdentifier().equalsIgnoreCase(FrameworkConstants.IDENTIFIER_TYPE)) {
				if (guiElement.getKind() != null) {
					component = robot.finder().findByType(getComponentClassForKind(guiElement));
				}
			}
		} catch (ComponentLookupException cle) {
			if (cle.found().size() > 0) {
				component = Lists.newArrayList(cle.found()).get(0);
			} else {
				throw cle;
			}
		}
		return component;
	}

	@SuppressWarnings("rawtypes")
	private Class getComponentClassForKind(GuiElement guiElement) {
		switch (guiElement.getKind()) {
		case TEXTFIELD:
			return JTextComponent.class;
		case BUTTON:
			return JButton.class;
		case LIST:
			return JList.class;
		case PANEL:
			return JPanel.class;
		default:
			break;
		}
		return null;
	}

	private FrameFixture frame(TestContext testContext) {
		return ((FrameworkSwingTestContext) testContext).getFrameFixture();
	}

	@Override
	public void click(TestContext testContext, GuiElement guiElement) {
		logger.debug(guiElement);
		robot(testContext, guiElement).click(component(guiElement, robot(testContext, guiElement)));
	}

	@Override
	public void useSpecifiedValue(TestContext testContext, GuiElement guiElement, String value) {
		logger.debug(guiElement);
		guiElement.setValue(value);
		applyValue(testContext, guiElement);
	}

	private boolean isTextField(GuiElement guiElement) {
		return guiElement.getKind() == FrameworkKindEnum.TEXTFIELD;
	}

	@Override
	public boolean isElementPresent(TestContext testContext, GuiElement guiElement) {
		boolean returnValue = false;
		try {
			returnValue = component(guiElement, robot(testContext, guiElement)).isVisible();
		} catch (ComponentLookupException e) {
		}
		return returnValue;
	}

	@Override
	public String getCurrentText(TestContext testContext, GuiElement guiElement) {
		return ((JTextComponent) component(guiElement, robot(testContext, guiElement))).getText();
	}

	@Override
	public void sendTab(TestContext testContext, GuiElement guiElement) {

	}

	@Override
	public void submit(TestContext testContext, GuiElement guiElement) {
		robot(testContext, guiElement).pressAndReleaseKeys(KeyEvent.VK_ENTER);
	}

	@Override
	public void waitForElementVisible(final TestContext testContext, final GuiElement guiElement, int timeout) {
		Pause.pause(new Condition("Waiting for visibility") {
			@Override
			public boolean test() {
				Collection<Component> list = ((FrameworkSwingTestContext) testContext).getFrameFixture().robot.finder().findAll(((FrameworkSwingTestContext) testContext).getFrameFixture().target,
						getMatcher(guiElement));
				return list.size() > 0;
			}
		}, timeout * 1000);
	}

	@SuppressWarnings("unchecked")
	protected ComponentMatcher getMatcher(final GuiElement guiElement) {
		if (guiElement.getIdentifier().equalsIgnoreCase(FrameworkConstants.IDENTIFIER_NAME)) {
			if (guiElement.getKind() != null) {
				return new NameMatcher(guiElement.getIdentValue());
			}
		} else if (guiElement.getIdentifier().equalsIgnoreCase(FrameworkConstants.IDENTIFIER_TYPE)) {
			if (guiElement.getKind() != null) {
				return new TypeMatcher(getComponentClassForKind(guiElement));
			}
		} else if (guiElement.getIdentifier().equalsIgnoreCase(FrameworkConstants.IDENTIFIER_VALUE)) {
			if (guiElement.getKind() != null) {
				if (guiElement.getKind() == FrameworkKindEnum.LABEL) {
					GenericTypeMatcher<JLabel> textMatcher = new GenericTypeMatcher<JLabel>(JLabel.class) {
						@Override
						protected boolean isMatching(JLabel label) {
							return guiElement.getValue().equals(label.getText());
						}
					};
					return textMatcher;
				}
			}
		}
		return null;
	}

	@Override
	public void waitForElementInvisible(final TestContext testContext, final GuiElement guiElement, int timeout) {
		Pause.pause(new Condition("Waiting for invisibility") {
			@Override
			public boolean test() {
				Collection<Component> list = ((FrameworkSwingTestContext) testContext).getFrameFixture().robot.finder().findAll(((FrameworkSwingTestContext) testContext).getFrameFixture().target,
						getMatcher(guiElement));
				return list.size() == 0;
			}
		}, timeout * 1000);
	}

	public boolean isElementEnabled(FrameworkSwingTestContext testContext, GuiElement guiElement) {
		return component(guiElement, robot(testContext, guiElement)).isEnabled();
	}

	@Override
	public boolean isElementEnabled(TestContext testContext, GuiElement guiElement) {
		return false;
	}

	@Override
	public String resolveTestDataForGuiElement(TestContext testContext, GuiElement guiElement) {
		return new FrameworkWebGuiActions().resolveTestDataForGuiElement(testContext, guiElement);
	}

	@Override
	public String resolveTestDataForGuiElement(TestContext testContext, String valueName) {
		return new FrameworkWebGuiActions().resolveTestDataForGuiElement(testContext, valueName);
	}

	@Override
	public String resolveTestDataForGuiElement(TestContext testContext, GuiElement guiElement, String id) {
		return new FrameworkWebGuiActions().resolveTestDataForGuiElement(testContext, guiElement, id);
	}

	@Override
	public void waitForFrameToBeAvailableAndSwitchToIt(TestContext testContext, String frameName, int timeout) {

	}

	@Override
	public String resolveTestDataForGuiElement(TestContext testContext, String valueName, String id) {
		return new FrameworkWebGuiActions().resolveTestDataForGuiElement(testContext, valueName, id);
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