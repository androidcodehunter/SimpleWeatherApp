package com.example.weatherapp.tools;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlParser {

	private final static String ITEM = "item";
	private final static String DESC = "description";
	private final static String NO_REC = "No record found";
	private final static String Q = "?";

	private XmlParser() {
	}

	public static String parse(String xmlRecords) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName(ITEM);

			Element element = (Element) nodes.item(0);

			NodeList name = element.getElementsByTagName(DESC); // get weather
																// description
			Element line = (Element) name.item(0);
			return getCharacterDataFromElement(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NO_REC;
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = ((Node) e).getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return Q;
	}
}
