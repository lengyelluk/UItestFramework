package com.test.lengyel.gui.web;

import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.testcase.web.FrameworkWebTestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class FrameworkDriverFactory {

	private static Map<FrameworkDriverMetaData, List<WebDriver>> driverPool;

	private static final Logger logger = LogManager.getLogger(FrameworkDriverFactory.class);

	public static synchronized WebDriver getDriverViaMetaData(FrameworkDriverMetaData metaData) {
		logger.info("Get Driver via Metadata");
		if (driverPool == null) {
			intitializeDriverPool(metaData);
		}
		if (driverPool.containsKey(metaData) && driverPool.get(metaData).size() > 0) {
			//get driver from pool
			WebDriver returnValue = driverPool.get(metaData).get(0);
			logger.info("Get Driver" + returnValue + " from pool");
			driverPool.get(metaData).remove(0);
			returnValue.manage().deleteAllCookies();
			returnValue.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			return returnValue;
		} else {
			//create new driver (do not put in pool, because we use it directly)
			if (!driverPool.containsKey(metaData)) {
				driverPool.put(metaData, new ArrayList<WebDriver>());
			}
			return getNewDriverViaMetaData(metaData);
		}
	}

	private static synchronized void intitializeDriverPool(FrameworkDriverMetaData metaData) {
		driverPool = new HashMap<FrameworkDriverMetaData, List<WebDriver>>();
	}

	private static synchronized WebDriver getNewDriverViaMetaData(FrameworkDriverMetaData metaData) {
		if(metaData.getFirefoxOptions() != null) {
		
			if (metaData.isGridFlag()) {
				return new RemoteWebDriver(metaData.getGridUrl(), metaData.getFirefoxOptions());
			} else {
				if (metaData.getFirefoxOptions().getBrowserName().replaceAll(" ", "")
						.equalsIgnoreCase(FrameworkConstants.FIREFOX_PROPERTY)) {
					logger.info("Starting new Firefox webdriver");
					//remove all anonymous firefox profiles from temp directory
					//selenium does not correctly remove these files, if the browser is kept open OR driver.quit() is not called
					//see http://stackoverflow.com/questions/6787095/how-to-stop-selenium-from-creating-temporary-firefox-profiles-using-web-driver
					cleanFirefoxProfilesFromTempDirectory();
					return new FirefoxDriver(metaData.getFirefoxOptions());
				}
			}
			logger.info("Starting new Firefox webdriver default");
		
		} else if (metaData.getChromeOptions() != null)	 {
			if (metaData.getChromeOptions().getBrowserName().replaceAll(" ", "")
					.equalsIgnoreCase(FrameworkConstants.CHROME_PROPERTY)) {
				logger.info("Starting new Chrome webdriver");

				//appli tools hack????
				return new ChromeDriver(metaData.getChromeOptions());
			}
		}
		logger.info("Starting default web browser - Firefox");
		return new FirefoxDriver(metaData.getFirefoxOptions());
	}

	public static synchronized void putDriverInPool(FrameworkDriverMetaData metaData, WebDriver driver) {
		logger.info("Putting driver " + driver + " into pool");
		if (!driverPool.containsKey(metaData)) {
			driverPool.put(metaData, new ArrayList<WebDriver>());
		}
		driverPool.get(metaData).add(driver);
	}

	public static void quit() {
		logger.info("Begin quit drivers in pool");
		
		int driverCounter = 0;
		if (driverPool != null && driverPool.values() != null) {
			for (List<WebDriver> entry : driverPool.values()) {
				for (WebDriver driver : entry) {
					driverCounter += 1;
					driver.close();
					driver.quit();
				}
			}
			driverPool = null;
		}
		logger.info("Quitting all " + driverCounter + " drivers in pool");
	}

	public static Map<FrameworkDriverMetaData, List<WebDriver>> getDriverPool() {
		return driverPool;
	}

	public static synchronized void putDriverInPool(FrameworkWebTestContext context) {
		putDriverInPool(context.getDriverMetaData(), context.getDriver());
	}
	
	private static void cleanFirefoxProfilesFromTempDirectory(){
		logger.debug("Deleting all Firefox profiles from Temp directory");
		//get system temp 
		File sysTemp = new File(System.getProperty("java.io.tmpdir"));
		//filter profiles directories by name and last modified time
		File[] directories = sysTemp.listFiles(new FilenameFilter() {
			
		  int counter = 1;
		
		  @Override
		  public boolean accept(File current, String name) {
			  if(name.matches("anonymous[0-9]+webdriver\\-profile") && counter <= 100){
				  File firefoxFile = new File(current, name);
				  //read directory attributes
				  BasicFileAttributes attr;
				  try {
					 attr = Files.readAttributes(firefoxFile.toPath(), BasicFileAttributes.class);
					 Date lastModifiedDate = new Date(attr.lastModifiedTime().to(TimeUnit.MILLISECONDS));
					 Calendar dateBefore = GregorianCalendar.getInstance();
					 dateBefore.add(Calendar.DAY_OF_MONTH, -3);
					 if(lastModifiedDate.before(dateBefore.getTime())){
						 counter++;
						 return firefoxFile.isDirectory();
					 }
				  } catch (IOException e) {
					  return false;
				  }
				  
				  //default false
				  return false;
			  } else {
				  return false;
			  }
		  }
		});
		//delete directories
		for(File directory : directories){
			deleteDir(directory);
		}
	}
	
	/**
	 * method to delete a firefox directory recursively (does nothing if directory is locked)
	 * 
	 * @param file - directory to delete
	 */
	private static void deleteDir(File directory) {
	    File[] files = directory.listFiles();
	    if (files != null) {
	    	//check for specific file lock 
	    	//that indicates whether firefox is currently running or not
	    	for(File f : files){    		
	    		if(f.getName().equals("parent.lock")){
	    			if(isFileLocked(f)){
	    				return;
	    			}
	    		}
	    	}	    	
	    	//delete sub-files/directories recursively
	        for (File f : files) {
	            deleteDir(f);
	        }
	    }
	    directory.delete();
	}
	
	/**
	 * windows specific solution to check for file lock 
	 * 
	 * @param file
	 * @return true if locked, else false
	 */
	private static boolean isFileLocked(File file){
		RandomAccessFile checkFile = null;
		try {
			//try to get read/write access on file
			checkFile = new RandomAccessFile(file, "rw");
    	} catch (FileNotFoundException e) {
    		//in case file is locked we get FileNotFoundException
			return true;
		} finally {
			//clean up resources
    	    if(checkFile != null){
				try {
					checkFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}
		return false;
	}

}