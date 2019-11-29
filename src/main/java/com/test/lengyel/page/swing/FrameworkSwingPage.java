package com.test.lengyel.page.swing;

import com.test.lengyel.gui.GuiElement;
import com.test.lengyel.gui.swing.FrameworkSwingGuiActions;
import com.test.lengyel.page.FrameworkPage;

import com.test.lengyel.testcase.TestContext;
import com.test.lengyel.testcase.swing.FrameworkSwingTestContext;
import org.fest.swing.core.BasicComponentPrinter;
import org.fest.swing.core.ComponentPrinter;

public class FrameworkSwingPage extends FrameworkPage {

	public FrameworkSwingPage(TestContext testContext) {
		super(testContext);
		setGuiActions(new FrameworkSwingGuiActions());
	}

	@Override
	public FrameworkSwingTestContext getTestContext() {
		return (FrameworkSwingTestContext) testContext;
	}

	public FrameworkSwingGuiActions getGuiActions() {
		return (FrameworkSwingGuiActions) super.getGuiActions();

	}

	public void printComponents() {
		ComponentPrinter printer = BasicComponentPrinter.printerWithCurrentAwtHierarchy();
		printer.printComponents(System.err);
	}

	public boolean isElementEnabled(GuiElement guiElement) {
		return getGuiActions().isElementEnabled(getTestContext(), guiElement);
	}

	public void selectTab(GuiElement guiElement, String tabToChoose) {
		getGuiActions().selectTab(getTestContext(), guiElement, tabToChoose);
	}
}
