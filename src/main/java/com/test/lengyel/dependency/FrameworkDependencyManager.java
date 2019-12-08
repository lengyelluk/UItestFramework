package com.test.lengyel.dependency;

import org.testng.SkipException;

import java.util.List;
import java.util.Map;

public class FrameworkDependencyManager {

	public static void setStatusForTestCaseAndTestChain(String testChain, String testCase, FrameworkTestCaseStatusEnum statusEnum, String addition) {
		FrameworkTestCaseStatus.setStatusForTestCaseAndTestChain(testChain, testCase, statusEnum, addition);
	}

	public static boolean checkTestCaseAndTestChainDependencies(String testChain, String testCase, String addition) {
		List<String> dependencies = FrameworkDependencyMap.getInstance().getDependenciesForTestCase(testCase);
		Map<String, FrameworkTestCaseStatusEnum> statusPerTestChain =  FrameworkTestCaseStatus.getInstance().getStatusPerTestChain().get(testChain);
		if(statusPerTestChain != null){
			for (String dependency : dependencies){
				FrameworkTestCaseStatusEnum status = statusPerTestChain.get(dependency);
				if(status!=null){
					if (status.equals(FrameworkTestCaseStatusEnum.BLOCKED)||status.equals(FrameworkTestCaseStatusEnum.FAILED)){
						throw new SkipException("Blocked by " + dependency);
					}
				}
			}
		}
		return true;
	}
	
	public static void resetStatus(String addition){
		FrameworkTestCaseStatus.resetStatus(addition);
	}
	
	

}
