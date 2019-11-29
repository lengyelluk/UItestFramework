package com.test.lengyel.testcase.swing;

import com.test.lengyel.testcase.FrameworkTestCase;
import com.test.lengyel.testcase.TestContextFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;

import java.awt.*;

public class FrameworkSwingTestCase extends FrameworkTestCase {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(FrameworkSwingTestCase.class);

	public FrameworkSwingTestContext getTestContext() {
		return (FrameworkSwingTestContext) testContext;
	}

	public void setUpTestContext(Frame frameForFixture, ITestContext ctx) {
		testContext = TestContextFactory.initializeSwingContext(this, ctx, frameForFixture);
	}
	
	@AfterClass
	public void cleanupContext(){
		getTestContext().cleanUpFrame();
	}

}
