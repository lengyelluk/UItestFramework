package com.test.lengyel.dependency;

import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.helper.FrameworkResourceMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FrameworkDependencyMap {

	private static final Logger logger = LogManager.getLogger(FrameworkDependencyMap.class);
	private Map<String, List<String>> dependencyMap;

	public Map<String, List<String>> getDependencyMap() {
		return dependencyMap;
	}

	public void setDependencyMap(Map<String, List<String>> dependencyMap) {
		this.dependencyMap = dependencyMap;
	}

	public List<String> getDependenciesForTestCase(String testCase) {
		List<String> returnValue = null;
		if (dependencyMap != null) {
			returnValue = dependencyMap.get(testCase);
		}
		if(returnValue==null){
			returnValue = new ArrayList<String>();
		}
		return returnValue;
	}

	private static XStream getXstream() {
		XStream xstream = new XStream(new StaxDriver());
		xstream.autodetectAnnotations(true);
		xstream.alias("dependencies", FrameworkDependencyMap.class);
		return xstream;
	}

	public static FrameworkDependencyMap getInstance() {
		FrameworkDependencyMap dependencyMap = null;
		XStream xstream = getXstream();
		try {
			dependencyMap = (FrameworkDependencyMap) xstream.fromXML(FrameworkResourceMapper.getFileFromDir(
					FrameworkConstants.DEPENDENCIES_PATH, FrameworkConstants.DEPENDENCIES_NAME + FrameworkConstants.DEPENDENCIES_ENDING));
		} catch (NullPointerException e) {
			logger.catching(e);
		}
		return dependencyMap;
	}

	public static void serializeInstance(FrameworkDependencyMap dependencyMap) {
		XStream xstream = getXstream();
		try {
			FileWriterWithEncoding writer = new FileWriterWithEncoding(FrameworkResourceMapper.getFileFromDir(
					FrameworkConstants.DEPENDENCIES_PATH, FrameworkConstants.DEPENDENCIES_NAME + FrameworkConstants.DEPENDENCIES_ENDING),
					"UTF-8");
			xstream.toXML(dependencyMap, writer);
		} catch (Exception e) {
			logger.catching(e);
		}
	}

}
