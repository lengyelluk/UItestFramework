package com.test.lengyel.dependency;

import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.helper.FrameworkResourceMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class FrameworkTestCaseStatus {

	private static final Logger logger = LogManager.getLogger(FrameworkTestCaseStatus.class);

	private Map<String, Map<String, FrameworkTestCaseStatusEnum>> statusPerTestChain;

	public Map<String, Map<String, FrameworkTestCaseStatusEnum>> getStatusPerTestChain() {
		return statusPerTestChain;
	}

	public void setStatusPerTestChain(Map<String, Map<String, FrameworkTestCaseStatusEnum>> statusPerTestChain) {
		this.statusPerTestChain = statusPerTestChain;
	}

	private static XStream getXstream() {
		XStream xstream = new XStream(new StaxDriver());
		xstream.autodetectAnnotations(true);
		xstream.alias("statusPerTestChain", FrameworkTestCaseStatus.class);
		return xstream;
	}

	public static FrameworkTestCaseStatus getInstance() {
		return getInstance("");
	}

	public static FrameworkTestCaseStatus getInstance(String addition) {
		FrameworkTestCaseStatus status = null;
		XStream xstream = getXstream();
		try {
			status = (FrameworkTestCaseStatus) xstream.fromXML(FrameworkResourceMapper.getFileFromDir(FrameworkConstants.STATUS_PATH,
					FrameworkConstants.STATUS_NAME + addition + FrameworkConstants.STATUS_ENDING));
		} catch (NullPointerException e) {
			logger.catching(e);
		}
		return status;
	}

	public static void serializeInstance(FrameworkTestCaseStatus status) {
		serializeInstance(status, "");
	}

	public static void serializeInstance(FrameworkTestCaseStatus status, String addition) {
		XStream xstream = getXstream();
		try {
			FileWriterWithEncoding writer = new FileWriterWithEncoding(FrameworkResourceMapper.getFileFromDir(
					FrameworkConstants.STATUS_PATH, FrameworkConstants.STATUS_NAME + addition + FrameworkConstants.STATUS_ENDING),
					"UTF-8");
			xstream.toXML(status, writer);
		} catch (Exception e) {
		}
	}

	public static void resetStatus(String addition) {
		FrameworkTestCaseStatus status = new FrameworkTestCaseStatus();
		Map<String, Map<String, FrameworkTestCaseStatusEnum>> statusPerTestChain = new HashMap<String, Map<String, FrameworkTestCaseStatusEnum>>();
		status.setStatusPerTestChain(statusPerTestChain);
		serializeInstance(status, addition);
	}

	public static void setStatusForTestCaseAndTestChain(String testChain, String testCase,
			FrameworkTestCaseStatusEnum statusEnum, String addition) {
		FrameworkTestCaseStatus status = getInstance();
		if (status.getStatusPerTestChain().containsKey(testChain)) {
			status.getStatusPerTestChain().get(testChain).put(testCase, statusEnum);
		} else {
			Map<String, FrameworkTestCaseStatusEnum> statusPerTestCase = new HashMap<String, FrameworkTestCaseStatusEnum>();
			statusPerTestCase.put(testCase, statusEnum);
			status.getStatusPerTestChain().put(testChain, statusPerTestCase);
		}
		serializeInstance(status, addition);
	}


}
