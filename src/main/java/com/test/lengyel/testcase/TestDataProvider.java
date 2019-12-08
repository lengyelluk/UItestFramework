package com.test.lengyel.testcase;

import com.google.common.base.Strings;
import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.helper.FrameworkResourceMapper;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestDataProvider {

	@DataProvider(name = "dataProvider")
	public static Object[][] provideData(Method m) {
		String testCaseName = m.getDeclaringClass().getSimpleName();
		String specificMarket = System.getProperty("market");

		String customPath = FrameworkPropertiesManager.getFunctionalProperty(null,
				FrameworkConstants.CUSTOM_TESTDATA_PATH_PROPERTY, true);
		if (Strings.isNullOrEmpty(customPath)) {
			customPath = FrameworkConstants.TESTDATA_PATH;
		}
		Object[][] returnValue;
		
		if (specificMarket!=null && specificMarket.equals("ALL")) {
			returnValue = filterFoundFiles(FrameworkResourceMapper
					.getFileNamesWithWildCard(testCaseName + "_*", customPath));
		} 
		else if (specificMarket!=null && !specificMarket.equals("ALL")) {
			System.out.println("Specific Market="+specificMarket+".Prepare test only for this market");
			returnValue = filterFoundFiles(FrameworkResourceMapper
					.getFileNamesWithWildCard(testCaseName + "_M-"+specificMarket+"*", customPath));
		}else {
			returnValue = filterFoundFiles(FrameworkResourceMapper
					.getFileNamesWithWildCard(testCaseName + "_*", customPath));
		}
		if (returnValue.length == 0) {
			returnValue = new Object[1][1];
			returnValue[0][0] = testCaseName;
		}
		return returnValue;
	}

	private static Object[][] filterFoundFiles(Object[][] fileNamesWithWildCard) {
		List<String> matchingFiles = new ArrayList<String>();
		for (int i = 0; i < fileNamesWithWildCard.length; i++) {
			String filename = (String) fileNamesWithWildCard[i][0];
			if (hasAddition(filename)) {
				if (isAdditionMatching(filename)) {
					matchingFiles.add(filename);
				}
			} else {
				String market = filename.substring(filename.indexOf("_M-")+3, filename.indexOf("_M-")+5);
				System.out.println("Current market: " + market);
				if (market != null) {
					if (filename.contains(market)) {
						System.out.println("Adding test data xml: " + filename);
						matchingFiles.add(filename);
					}
				} else
					matchingFiles.add(filename);
			}
		}
		Object[][] returnValue = new Object[matchingFiles.size()][1];
		for (int i = 0; i < matchingFiles.size(); i++) {
			returnValue[i][0] = matchingFiles.get(i);
		}
		return returnValue;
	}

	private static boolean isAdditionMatching(String filename) {
		String additionFromProperties = TestContextFactory
				.getFunctionalProperty(
						FrameworkConstants.NUMBERCACHE_ADDITION_PROPERTY)
				.toLowerCase();
		String additionFromFileName = filename.substring(
				filename.indexOf(FrameworkConstants.TESTDATA_ADDITION)
						+ FrameworkConstants.TESTDATA_ADDITION.length())
				.toLowerCase();
		return additionFromFileName.equals(additionFromProperties);
	}

	private static boolean hasAddition(String filename) {
		return filename.contains(FrameworkConstants.TESTDATA_ADDITION);
	}

	@DataProvider(name = "SupportDataProvider")
	public static Object[][] provideData(ITestContext context) {
		String testDataName = context.getCurrentXmlTest().getParameter(
				FrameworkConstants.TESTDATA_PARAMETER);
		Object[][] returnValue = new Object[1][1];
		returnValue[0][0] = testDataName;
		return returnValue;
	}

}
