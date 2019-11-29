package com.test.lengyel.testcase;

import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.helper.FrameworkResourceMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.Properties;

public class FrameworkPropertiesManager {

	private static final Logger logger = LogManager
			.getLogger(FrameworkPropertiesManager.class);

	public static String getFunctionalProperty(TestContext testContext,
			String propertyName, boolean useEnvironmentValue) {
		Properties functionalProperties = null;
		if (testContext == null) {
			functionalProperties = loadFunctionalProperties();
		} else {
			functionalProperties = testContext.getFuntionalSettings();
		}
		String returnValue = null;
		if (useEnvironmentValue) {
			String sysproperty = System.getProperty(propertyName);
			if (sysproperty != null && !sysproperty.isEmpty()) {
				returnValue = sysproperty;
					if(returnValue.contains(FrameworkConstants.FUNCTIONAL_PROPERTIES_VALUES_REPLACEMENT_IDENTIFIER)){
						returnValue = FrameworkConstants.FUNCTIONAL_PROPERTIES_VALUES_REPLACEMENT_IDENTIFIER + propertyName +"."+ returnValue;
				}
			}
		}
		if (returnValue == null)
			returnValue = functionalProperties.getProperty(propertyName);

		String resolvedString = resolveVariable(returnValue);
		if (resolvedString != null) {
			returnValue = resolvedString;
		}
		return returnValue;
	}

	public static void setFunctionalProperty(TestContext testContext,
			String propertyName, String value) {
		System.setProperty(propertyName, value);
		testContext.getFuntionalSettings().setProperty(propertyName, value);
		File propertiesFile = FrameworkResourceMapper.getFileFromDir(
				FrameworkConstants.PROPERTIES_PATH,
				FrameworkConstants.FUNCTIONAL_PROPERTIES_NAME);
		try {
			testContext.getFuntionalSettings().store(
					new FileOutputStream(propertiesFile), "");
		} catch (Exception e) {
		}

	}

	public static Properties loadFunctionalSettingValues() {
		return loadPropertiesForName(FrameworkConstants.FUNCTIONAL_PROPERTIES_VALUES_NAME);
	}

	private static Properties loadPropertiesForName(String propertiesName) {
		logger.debug("Loading Settings " + propertiesName);
		Properties properties = null;
		try {
			File propertiesFile = FrameworkResourceMapper.getFileFromDir(
					FrameworkConstants.PROPERTIES_PATH, propertiesName);
			properties = new Properties();
			properties.load(new BufferedReader(new InputStreamReader(new FileInputStream(propertiesFile), StandardCharsets.UTF_8)));
		} catch (Exception e) {
			logger.fatal("No technical Settings found");
		}
		return properties;
	}

	public static Properties loadFunctionalProperties() {
		return resolveFunctionalValues(loadPropertiesForName(FrameworkConstants.FUNCTIONAL_PROPERTIES_NAME));
	}

	public static Properties loadTechnicalProperties() {
		return loadPropertiesForName(FrameworkConstants.TECHNICAL_PROPERTIES_NAME);
	}

	private static Properties resolveFunctionalValues(
			Properties firstLevelProperties) {
		Properties functionalPropertiesValues = loadFunctionalSettingValues();
		for (Entry<Object, Object> v : firstLevelProperties.entrySet()) {
			String value = (String) v.getValue();
			String key = (String) v.getKey();
			if (((String) value)
					.startsWith(FrameworkConstants.FUNCTIONAL_PROPERTIES_VALUES_REPLACEMENT_IDENTIFIER)) {
				String secondLevelKey = key
						+ "."
						+ value.substring(FrameworkConstants.FUNCTIONAL_PROPERTIES_VALUES_REPLACEMENT_IDENTIFIER
								.length());
				String secondLevelValue = functionalPropertiesValues
						.getProperty(secondLevelKey);
				if (secondLevelValue != null) {
					firstLevelProperties.setProperty(key, secondLevelValue);
				}
			}
		}
		return firstLevelProperties;
	}

	public static String resolveVariable(String variableFromTestData) {
		String returnValue = null;
		if (variableFromTestData != null) {
			if (variableFromTestData
					.contains(FrameworkConstants.FUNCTIONAL_PROPERTIES_VALUES_REPLACEMENT_IDENTIFIER)) {
				String[] parts = variableFromTestData
						.split("\\"
								+ FrameworkConstants.FUNCTIONAL_PROPERTIES_VALUES_REPLACEMENT_IDENTIFIER);
				// just 2 levels supported
				if (parts.length == 2) {
					String variableName = parts[1];
					Properties firstLevelProperties = loadFunctionalProperties();
					variableName = variableName
							.replace(
									FrameworkConstants.FUNCTIONAL_PROPERTIES_VALUES_REPLACEMENT_IDENTIFIER,
									"");
					returnValue = firstLevelProperties
							.getProperty(variableName);
				}
				if (parts.length == 3) {
					String variableName = parts[1];
					String variableShape = parts[2];
					Properties secondLevelProperties = loadFunctionalSettingValues();
					variableName = variableName
							.replace(
									FrameworkConstants.FUNCTIONAL_PROPERTIES_VALUES_REPLACEMENT_IDENTIFIER,
									"");
					variableShape = variableShape
							.replace(
									FrameworkConstants.FUNCTIONAL_PROPERTIES_VALUES_REPLACEMENT_IDENTIFIER,
									"");
					String secondLevelKey = variableName + variableShape;
					returnValue = secondLevelProperties
							.getProperty(secondLevelKey);
				}
			}
		}
		return returnValue;
	}

	public static String getTechnicalProperty(TestContext testContext,
			String propertyName, boolean useEnvironmentValue) {
		Properties technicalProperties = null;
		if (testContext == null) {
			technicalProperties = loadTechnicalProperties();
		} else {
			technicalProperties = testContext.getTechnicalSettings();
		}
		String returnValue = null;
		if (useEnvironmentValue) {
			if (System.getProperty(propertyName) != null) {
				returnValue = System.getProperty(propertyName);
			}
		}
		if (returnValue == null)
			returnValue = technicalProperties.getProperty(propertyName);
		return returnValue;
	}

}
