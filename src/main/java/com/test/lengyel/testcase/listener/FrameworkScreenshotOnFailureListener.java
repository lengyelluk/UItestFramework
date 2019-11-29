package com.test.lengyel.testcase.listener;

import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.testcase.web.FrameworkWebTestCase;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.internal.InvokedMethod;
import org.testng.internal.TestResult;

import java.io.File;
import java.lang.reflect.Field;

public class FrameworkScreenshotOnFailureListener extends FrameworkWebTestCase implements IInvokedMethodListener {

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		if (method.isTestMethod()) {
			if (!testResult.isSuccess()) {
				String logPath = getTechnicalProperty(FrameworkConstants.CURRENT_LOGPATH_PROPERTY);
				String testDataName = null;
				if (testResult.getParameters() != null && testResult.getParameters().length > 0
						&& testResult.getParameters()[0] != null) {
					testDataName = (String) testResult.getParameters()[0];
				} else {
					testDataName = method.getTestMethod().getRealClass().getSimpleName();
				}
				try {
					testDataName = testDataName.substring(0, testDataName.indexOf("#"));
				} catch (Exception e) {
				}
				Field privateTestCaseField;
				try {
					privateTestCaseField = InvokedMethod.class.getDeclaredField("m_instance");
					privateTestCaseField.setAccessible(true);
					FrameworkWebTestCase testCase = (FrameworkWebTestCase) privateTestCaseField.get(method);
					WebDriver driver = testCase.getTestContext().getDriver();
					if (driver.getClass().equals(RemoteWebDriver.class)) {
						driver = new Augmenter().augment(driver);
					}
					String numberCacheAddition = getProperty(FrameworkConstants.NUMBERCACHE_ADDITION_PROPERTY);
					if (numberCacheAddition == null) {
						numberCacheAddition = "";
					}
					testDataName += numberCacheAddition;
					String screenshotAddition = getProperty(FrameworkConstants.CUSTOM_ADDITIONAL_SCREENSHOT_PROPERTY);
					if(screenshotAddition==null){
						screenshotAddition="";
					}else{
						if(screenshotAddition.length() > 0){
							testDataName+="_";
						}
					}
					testDataName+=screenshotAddition;
					File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
					FileUtils.copyFile(screenshot, new File(logPath + "\\" + testDataName + ".png"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Field cause = TestResult.class.getDeclaredField("m_throwable");
					cause.setAccessible(true);
					Throwable throwa = (Throwable) cause.get(testResult);
					if(throwa != null){
						System.err.println(throwa.getMessage());
						throwa.printStackTrace(System.out);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
