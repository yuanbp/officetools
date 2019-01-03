package com.chieftain.utils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SingletonUtil {

	private static XMLReader xmlReader;
	private SingletonUtil() {

	}
	public static synchronized XMLReader getXMLReader() throws SAXException,ParserConfigurationException {
		if (xmlReader == null) {
			SAXParserFactory sax = SAXParserFactory.newInstance();
			sax.setNamespaceAware(false);
			xmlReader = sax.newSAXParser().getXMLReader();
		}
		return xmlReader;
	}
}