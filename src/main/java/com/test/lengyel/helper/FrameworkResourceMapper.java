package com.test.lengyel.helper;

import com.test.lengyel.constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FrameworkResourceMapper {

	private static final Logger logger = LogManager.getLogger(FrameworkResourceMapper.class);
	private static Map<String, Map<String, File>> fileMap = null;

	public static Map<String, File> getFilesAsMapForDir(String directory) {
		return getFilesAsMapForDir(directory, false);
	}

	public static Map<String, File> getFilesAsMapForDir(String directory, boolean forceReload) {
		if (fileMap != null) {
			if (fileMap.containsKey(directory) && !forceReload) {
				return fileMap.get(directory);
			}
		}
		//logger.debug("Loading Data from " + directory);
		Map<String, File> returnValue = new HashMap<String, File>();
		File file = null;
		String instDir = System.getProperty(FrameworkConstants.SGT_INSTALLATION_DIR);
		if (instDir != null) {
			file = new File(instDir + File.separator + directory);
			if (file.exists())
				returnValue.putAll(getFileMapRecursiveForFile(file));
		}
		if (System.getProperty(FrameworkConstants.USE_MAJA_PROPERTY, "false").equalsIgnoreCase("true")) {
			String testsDir = System.getProperty("user.home") + File.separator + "MAJA";
			if (testsDir != null) {
				File testsFile = new File(testsDir + File.separator + directory);
				if (testsFile.exists())
					returnValue.putAll(getFileMapRecursiveForFile(testsFile));
			}
		}
		if (file == null || !file.exists()) {
			returnValue.putAll(getResourceListing(directory));
		}
		if (!directory.equalsIgnoreCase(FrameworkConstants.PROPERTIES_PATH)) {
			if (fileMap == null) {
				fileMap = new HashMap<String, Map<String, File>>();
			}
			fileMap.put(directory, returnValue);
		}
		return returnValue;
	}

	@SuppressWarnings("resource")
	static Map<String, File> getResourceListing(String path) {
		URL dirURL = FrameworkResourceMapper.class.getClassLoader().getResource(path);
		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			URL url = FrameworkResourceMapper.class.getClassLoader().getResource(path);
			return getFileMapRecursiveForFile(new File(url.getFile()));
		}

		if (dirURL == null) {
			String me = FrameworkResourceMapper.class.getName().replace(".", "/") + ".class";
			dirURL = FrameworkResourceMapper.class.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar")) {
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
			JarFile jar = null;
			try {
				jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			} catch (Exception e) {
			}
			Enumeration<JarEntry> entries = jar.entries();
			Map<String, File> result = new HashMap<String, File>();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				String name = jarEntry.getName();
				if (name.startsWith(path)) {
					String entry = name.substring(path.length());
					if (jarEntry.isDirectory()) {
						try {
							result.putAll(getResourceListing(path + "/" + entry));
						} catch (Exception e) {
						}
					} else {
						if (entry.contains("/")) {
							entry = entry.substring(entry.lastIndexOf("/") + 1);
						}
						result.put(entry, extract(name));
					}
				}
			}
			return result;
		}
		return null;
	}

	private static File extract(String filePath) {
		try {
			File f = File.createTempFile(filePath, null);
			FileOutputStream resourceOS = new FileOutputStream(f);
			byte[] byteArray = new byte[1024];
			int i;
			InputStream classIS = FrameworkResourceMapper.class.getClassLoader().getResourceAsStream(filePath);
			while ((i = classIS.read(byteArray)) > 0) {
				resourceOS.write(byteArray, 0, i);
			}
			// Close streams to prevent errors
			classIS.close();
			resourceOS.close();
			return f;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Map<String, File> getFileMapRecursiveForFile(File file) {
		Map<String, File> returnValue = new HashMap<String, File>();
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				returnValue.putAll(getFileMapRecursiveForFile(child));
			}
		} else {
			returnValue.put(file.getName(), file);
		}
		return returnValue;
	}

	public static Object[][] getFileNamesWithWildCard(String wildCardString, String pathToFilter) {
		Map<String, File> testdata = getFilesAsMapForDir(FrameworkConstants.TESTDATA_PATH, true);
		wildCardString = wildCardString.replace("*", "");
		ArrayList<String> list = new ArrayList<String>();
		for (String key : testdata.keySet()) {
			if (key.startsWith(wildCardString)) {
				if (testdata.get(key).getAbsolutePath().contains(pathToFilter)) {
					key = key.replace(".xml", "");
					list.add(key);
				}
			}
		}
		Object[][] returnValue = new Object[list.size()][1];
		for (int i = 0; i < list.size(); i++) {
			returnValue[i][0] = list.get(i);
		}
		return returnValue;
	}

	public static String getSOAPString(String fileName) {
		File soapFile = getSOAPFile(fileName);
		byte[] encoded = new byte[0];
		try {
			encoded = Files.readAllBytes(soapFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(encoded);
	}

	private static File getSOAPFile(String fileName) {
		logger.info("Loading SOAPFile for " + fileName);
		File testDataFile = FrameworkResourceMapper.getFileFromDir(FrameworkConstants.SOAP_PATH, fileName.concat(".xml"));
		if (testDataFile == null) {
			logger.error("No Testdata found for " + fileName);
		}
		return testDataFile;
	}

	public static File getFileFromDir(String path, String fileName) {
		//logger.info("Loading " + fileName + " from " + path);
		File returnFile = null;
		if (fileMap == null) {
			fileMap = new HashMap<String, Map<String, File>>();
		}
		if (fileMap.containsKey(path)) {
			if (fileMap.get(path).get(fileName) != null) {
				returnFile = fileMap.get(path).get(fileName);
			} else {
				Map<String, File> files = getFilesAsMapForDir(path, true);
				returnFile = files.get(fileName);
			}
		} else {
			Map<String, File> files = getFilesAsMapForDir(path, true);
			returnFile = files.get(fileName);
		}
		//logger.info("found: " + returnFile.getAbsolutePath());
		returnFile.getAbsolutePath();
		return returnFile;
	}
}