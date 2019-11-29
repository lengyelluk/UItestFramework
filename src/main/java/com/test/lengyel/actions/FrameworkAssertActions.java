package com.test.lengyel.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class FrameworkAssertActions {

	private List<Error> verificationErrors;
	public static SoftAssert verifications;
	private static List<String> checkPoints;
	private static TreeMap<String, String> milestones;

	private static final Logger logger = LogManager.getLogger(FrameworkAssertActions.class);

	public FrameworkAssertActions() {
		if (verifications == null) {
			verifications = new SoftAssert();
		}
		checkPoints = new ArrayList<String>();
		milestones = new TreeMap<String, String>();
		setVerificationErrors(new ArrayList<Error>());
	}

	public List<Error> getVerificationErrors() {
		return verificationErrors;
	}

	public void setVerificationErrors(List<Error> verificationErrors) {
		this.verificationErrors = verificationErrors;
	}

	public void assertEquals(Object currentValue, Object expectedValue, String message) {
		logger.info("Assert for Equality: Current:" + currentValue + " against expected: " + expectedValue);
		Assert.assertEquals(currentValue, expectedValue, message);
		checkPoints.add("[OK] " + message);
	}

	public void assertEquals(Object currentValue, Object expectedValue) {
		assertEquals(currentValue, expectedValue, "");
	}

	public void assertEquals(String currentValue, String expectedValue, String message) {
		logger.info("Assert for Equality: Current:" + currentValue + " against expected: " + expectedValue);
		Assert.assertEquals(currentValue, expectedValue, message);
		checkPoints.add("[OK] " + message);
	}

	public void assertEquals(String currentValue, String expectedValue) {
		assertEquals(currentValue, expectedValue, "");
	}

	public void assertEquals(int currentValue, int expectedValue, String message) {
		logger.info("Assert for Equality: Current:" + currentValue + " against expected: " + expectedValue);
		Assert.assertEquals(currentValue, expectedValue, message);
	}

	public void assertEquals(int currentValue, int expectedValue) {
		assertEquals(currentValue, expectedValue, "");
	}

	public void verifyEquals(String currentValue, String expectedValue) {
		verifyEquals(currentValue, expectedValue, "");
	}

	public void verifyEquals(String currentValue, String expectedValue, String message) {
		logger.info("Verify for Equality: Current: " + currentValue + " against expected: " + expectedValue);
		try {
			verifications.assertEquals(currentValue, expectedValue, message + "\n");
			checkPoints.add("[OK] " + message);
		} catch (Error e) {
			System.err.println("Failed");
			verificationErrors.add(e);
			checkPoints.add("[ERROR] " + message);
		}

	}

	public static void checkForVerificationErrors() {
		logger.info("Checking for VerificationErrors");
		if (verifications != null) {
			try {
				verifications.assertAll();
			} catch (Error e) {
				throw e;
			} finally {
				verifications = new SoftAssert();
			}
		}
	}

	public static String checkForVerificationErrorMessages() {
		try {
			checkForVerificationErrors();
		} catch (Error e) {
			return e.getMessage().replace("[ERROR] The following asserts failed:\n", "");
		}
		return "";
	}

	public void assertTrue(boolean checkCondition) {
		assertTrue(checkCondition, "");
	}

	public void assertFalse(boolean checkCondition) {
		assertFalse(checkCondition, "");
	}

	public void assertTrue(boolean checkCondition, String message) {
		logger.info("Assert for True: Current:" + checkCondition);
		logger.info(message);
		Assert.assertTrue(checkCondition, message);
		checkPoints.add("[OK] " + message);
	}

	public void assertFalse(boolean checkCondition, String message) {
		logger.info("Assert for False: Current:" + checkCondition);
		Assert.assertFalse(checkCondition, message);
		checkPoints.add("[OK] " + message);
	}
	
	public void assertNotNull(Object currentValue, String message) {
		logger.info("Assert not to be null: Current:" + currentValue);
		Assert.assertNotNull(currentValue, message);
		checkPoints.add("[OK] " + message);
	}
	
	public void assertNotNull(Object currentValue) {
		assertNotNull(currentValue, "");
	}

	public void verifyTrue(boolean checkCondition) {
		verifyTrue(checkCondition, "");
	}

	public void verifyTrue(boolean checkCondition, String message) {
		logger.info("Verify for True: Current:" + checkCondition);
		try {
			if (checkCondition) {
				checkPoints.add("[OK] " + message);
			} else {
				checkPoints.add("[NOK] " + message);
				logger.info("[NOK] " + message);
			}
			verifications.assertTrue(checkCondition, message + "\n");
		} catch (Error e) {
			System.err.println("Failed");
			verificationErrors.add(e);
			checkPoints.add("[ERROR] " + message);
		}
	}

	public void verifyEquals(BigDecimal currentValue, BigDecimal expectedValue, String message) {
		logger.info("Verify for Equality: Current:" + currentValue + " against expected: " + expectedValue);
		try {
			verifications.assertEquals(currentValue, expectedValue, message + "\n");
			checkPoints.add("[OK] " + message);
		} catch (Error e) {
			System.err.println("Failed");
			verificationErrors.add(e);
			checkPoints.add("[ERROR] " + message);
		}
	}

	public List<String> getCheckPoints() {
		return checkPoints;
	}

	public static String getCheckPointsString() {
		StringBuilder sb = new StringBuilder();
		for (String cp : checkPoints) {
			sb.append(cp + "\n");
		}
		checkPoints = new ArrayList<String>();
		return sb.toString();
	}

	
	public static TreeMap<String, String> getMilestones() {
		TreeMap<String, String> milestonesResults = new TreeMap<>();
		Set<String> keys = milestones.keySet();
        for(String key : keys){
            milestonesResults.put(key, milestones.get(key));
            System.out.println("Value of copied "+ key +" is: "+ milestones.get(key));
        }
        milestones.clear();
        System.out.println("Saving milestones results to result entity and clearing them for next test");
        
        return milestonesResults;
	}

	public void setMilestones(TreeMap<String, String> milestones) {
		FrameworkAssertActions.milestones = milestones;
	}
	
	public void setMilestoneResult(boolean condition, String key) {
		logger.info("Milestone " + key + " added to the list of milestones");
		String result = condition ? "passed" : "failed";
		milestones.put(key, result);
	}
}
