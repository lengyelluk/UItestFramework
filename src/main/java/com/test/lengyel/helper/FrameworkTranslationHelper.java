package com.test.lengyel.helper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * The class provides translations for market specific texts, labels (for ex.,
 * error message text)
 * 
 * @author tnikolae
 *
 */
public class FrameworkTranslationHelper {

	private static final String LABEL_XML_FILE = "Labels.xml";
	private static final String TRANSLATIONS_FOLDER = "Translations";

	/**
	 * returns text of label in the specified language.
	 * The value is retrieved from Labels.xml 
	 */
	public static String getLabelTranslation(String label, String market) {
		String labelTranslation = null;
		String marketLanguage = getDefaultLanguage(market);

		// build XML Document
		Document doc = getConfigurationXmlDocument(LABEL_XML_FILE);

		Node labelNode = findLabel(doc, label);
		Element labelElement = (Element) labelNode;

		Element marketElement = (Element) labelElement.getElementsByTagName(
				marketLanguage.toLowerCase()).item(0);

		labelTranslation = marketElement.getTextContent();

		return labelTranslation;
	}
	

	// searches for XML tag for the specified market
	private static Node findMessage(Document doc, String message) {
		System.out.println("Searching for message " + message + " in translations xml");
		Node marketNode = doc.getElementsByTagName(message).item(0);

		if (marketNode == null)
			System.out.println("Failed to find message " + message
					+ " in translations xml");
		return marketNode;
	}
	
	// searches for XML tag for the specified market
		private static Node findLabel(Document doc, String label) {
			System.out.println("Searching for label " + label + " in translations xml");
			Node marketNode = doc.getElementsByTagName(label).item(0);

			if (marketNode == null)
				System.out.println("Failed to find label " + label
						+ " in translations xml");
			return marketNode;
		}


	// creates Document from XML file
	private static Document getConfigurationXmlDocument(String xmlFileName) {
		// xml file with translations for each Activity
		File xmlFile = getConfigurationFile(xmlFileName);

		// build xml document
		System.out.println("Parsing XML with Translations");
		Document doc = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			System.out.println("Failed to parse the xml with Translations: "
					+ xmlFile.getAbsolutePath());
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("Failed to parse the xml with Translations: "
					+ xmlFile.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not find the xml with Translations: "
					+ xmlFile.getAbsolutePath());
			e.printStackTrace();
		}

		return doc;
	}

	// returns XML file with the specified name
	private static File getConfigurationFile(String fileName) {
		File file = new File(FrameworkTranslationHelper.class.getClassLoader()
				.getResource(TRANSLATIONS_FOLDER + "/" + fileName).getFile());
		System.out.println("Found XML with translations: " + file.getAbsolutePath());
		return file;
	}
	
	/**
	 * to return default language of a specific market
	 * @param market
	 * @return default language of the market
	 */
	private static String getDefaultLanguage(String market) {
		market = market.toLowerCase();
		switch (market) {
			case "uk":
				return "en";
			case "ch":
				return "ch";
			case "it":
				return "en";
			case "fr":
				return "fr";
			case "nl":
				return "en";
			case "tr":
				return "en";
			case "be":
				return "en";
			case "pt":
				return "en";
			default:
				return "en";
		}
				
			
	}

	
}
