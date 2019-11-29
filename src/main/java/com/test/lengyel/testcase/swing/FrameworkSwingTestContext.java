package com.test.lengyel.testcase.swing;

import com.test.lengyel.testcase.TestContext;
import org.fest.swing.fixture.FrameFixture;

public class FrameworkSwingTestContext extends TestContext {

	private FrameFixture frameFixture;

	public FrameworkSwingTestContext(TestContext generalContext) {
		setTestData(generalContext.getTestData());
		setTechnicalSettings(generalContext.getTechnicalSettings());
		setFuntionalSettings(generalContext.getFuntionalSettings());
		setAssertActions(generalContext.getAssertActions());

	}

	public FrameFixture getFrameFixture() {
		return frameFixture;
	}

	public void setFrameFixture(FrameFixture frameFixture) {
		this.frameFixture = frameFixture;
	}

	public void cleanUpFrame() {
		this.frameFixture.cleanUp();
	}

}
