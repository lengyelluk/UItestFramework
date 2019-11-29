package com.test.lengyel.page;

import com.test.lengyel.constants.FrameworkConstants;
import com.test.lengyel.gui.GuiElement;
import com.test.lengyel.helper.FrameworkGuiElementMap;
import com.test.lengyel.helper.FrameworkResourceMapper;
import com.test.lengyel.page.swing.FrameworkSwingPage;
import com.test.lengyel.page.web.FrameworkWebPage;
import com.test.lengyel.testcase.TestContext;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class FrameworkPageFactory {

	private static final Logger logger = LogManager.getLogger(FrameworkPageFactory.class);

	private static Map<String, FrameworkPage> pageCache;

	@SuppressWarnings("unchecked")
	public static <T extends FrameworkPage> T getPage(Class<T> clazz, TestContext testContext) {
		//logger.info("[PAGE]loading Page " + clazz.getSimpleName());
		T pageObject = null;
		if (pageCache == null) {
			pageCache = new HashMap<String, FrameworkPage>();
		}
		if (pageCache.containsKey(clazz.getSimpleName())) {
			pageObject = (T) pageCache.get(Integer.toString(clazz.getSimpleName().hashCode()));
			pageObject.setTestContext(testContext);
		} else {
			pageObject = loadPageWithInstantiation(clazz, testContext);
			pageCache.put(Integer.toString(pageObject.getClass().getSimpleName().hashCode()), pageObject);
		}
		return pageObject;
	}

	private static <T extends FrameworkPage> T loadPageWithInstantiation(Class<T> clazz, TestContext testContext) {
		T pageObject = instantiatePage(clazz, testContext);
		loadSelectorsInPage(pageObject);
		return pageObject;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T instantiatePage(Class<T> clazz, TestContext testContext) {
		T pageObject = null;
		try {
			Constructor constructor = clazz.getConstructor(TestContext.class);
			pageObject = (T) constructor.newInstance(testContext);
		} catch (Exception e) {
			logger.catching(e);
		}
		return pageObject;
	}

	private static void loadSelectorsInPage(FrameworkPage pageObject) {
		Map<String, GuiElement> map = loadSelectorsRecursiveToFrameworkClassesToMap(pageObject);
		if (map != null) {
			for (Field field : pageObject.getClass().getFields()) {
				if (isGuiElement(field)) {
					String name = field.getName();
					GuiElement elementFromMap = map.get(name);
					if (elementFromMap == null) {
						logger.error(name + " not found in SelectorsFile");
					} else {
						try {
							//manually initialize static value from GuiElement
							elementFromMap.setStaticIdentValue(elementFromMap.getIdentValue());
							field.set(pageObject, elementFromMap);
						} catch (Exception e) {
							logger.catching(e);
						}
					}
				}
			}
		}
	}

	private static Map<String, GuiElement> loadSelectorsRecursiveToFrameworkClassesToMap(FrameworkPage pageObject) {
		Map<String, GuiElement> map = new HashMap<String, GuiElement>();
		FrameworkGuiElementMap mapFromfile = loadSelectorsToMap(pageObject);
		if (mapFromfile != null && mapFromfile.getElementMap() != null) {
			//resolve super class elements
			if (!isFrameworkClass(pageObject.getClass().getSuperclass())) {
				FrameworkPage superPageObject = (FrameworkPage) instantiatePage(pageObject.getClass().getSuperclass(),
						pageObject.getTestContext());
				map.putAll(loadSelectorsRecursiveToFrameworkClassesToMap(superPageObject));
			}
			checkForAlreadyExistingGuiElements(map, mapFromfile.getElementMap());
			map.putAll(mapFromfile.getElementMap());
		}
		return map;
	}

	private static boolean isGuiElement(Field field) {
		return field.getType().equals(GuiElement.class);
	}
	
	private static void checkForAlreadyExistingGuiElements(Map<String, GuiElement> map1, Map<String, GuiElement> map2){
		if(map1 == null || map2 == null){
			return;
		}
		for (Map.Entry<String, GuiElement> entry : map1.entrySet()) {
		    if(map2.containsKey(entry.getKey())){
		    	logger.warn("WARNING: GuiElement:" + entry.getKey() + " already exists in a superclass.");
		    }
		}
	}

	@SuppressWarnings("rawtypes")
	private static boolean isFrameworkClass(Class clazz) {
		return clazz.equals(FrameworkPage.class)||clazz.equals(FrameworkWebPage.class)||clazz.equals(FrameworkSwingPage.class);
	}

	private static FrameworkGuiElementMap loadSelectorsToMap(FrameworkPage pageObject) {
		FrameworkGuiElementMap map = null;
		XStream xstream = new XStream(new StaxDriver());
		xstream.autodetectAnnotations(true);
		xstream.alias("page", FrameworkGuiElementMap.class);
		xstream.addImplicitMap(FrameworkGuiElementMap.class, "elementMap", GuiElement.class, "name");
		xstream.alias("GuiElement", GuiElement.class);
		xstream.registerConverter(FrameworkKindEnumConverter.getInstance());
		try {
			map = (FrameworkGuiElementMap) xstream.fromXML(getFileForPage(pageObject));
		} catch (NullPointerException e) {
			logger.error(pageObject.getClass().getSimpleName() + " SelectorsFile not Found.");
			logger.catching(e);
		}
		return map;
	}

	private static File getFileForPage(FrameworkPage pageObject) {
		try{
			return FrameworkResourceMapper.getFileFromDir(FrameworkConstants.SELECTORS_PATH, pageObject.getClass().getSimpleName() + "_" + System.getProperty(FrameworkConstants.CUSTOM_TESTDATA_PATH_PROPERTY)
				+ ".xml");
		}catch(NullPointerException e){
			return FrameworkResourceMapper.getFileFromDir(FrameworkConstants.SELECTORS_PATH, pageObject.getClass().getSimpleName()
					+ ".xml");
		}
	}

}
