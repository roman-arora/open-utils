package org.openutils.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlBuilder 
{
	private static final String NEW_LINE = System.getProperty("line.separator");
	
	public static Document stringToDocument(String xmlStr, boolean isValidating) 
			throws SAXException, IOException, ParserConfigurationException 
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(isValidating);
		DocumentBuilder builder = factory.newDocumentBuilder();
			
		InputSource inputSource = new InputSource(new StringReader(xmlStr));
		Document document = builder.parse(inputSource);
		return document;		
	}
	
	public static String removeLeadingZeroes(String str)
	{
		String regex = "^0*";
	    return str.replaceAll(regex, "");
	}
	
	public static Node getElement(String elementName, Node rootNode)
	{
		Node node = rootNode.getFirstChild();
		while(node != null)
		{
			if(node.getNodeName().equalsIgnoreCase(elementName))
			{
				return node;
			}
			
			node = node.getNextSibling();
		}
		
		return null;
	}
	
	private boolean prettyPrint;
	private StringBuilder strBuilder;
	
	public XmlBuilder(boolean prettyPrint)
	{
		this.prettyPrint = prettyPrint;
		this.strBuilder = new StringBuilder();
	}
	
	public void append(String xml)
	{
		strBuilder.append(xml);
		
		if(prettyPrint) {
			 strBuilder.append(NEW_LINE);
		}
	}
	
	public void closeTag(String name)
	{
		append("</" + name + ">");
	}

	public void openTag(String name)
	{
		append("<" + name + ">");
	}
	
	public void openTag(String name, Map<String, String> attributes)
	{
		String openTag = "<" + name;

		if( attributes != null && !attributes.isEmpty() )
		{
			Iterator<String> keySetIterator = attributes.keySet().iterator();
			while(keySetIterator.hasNext())
			{
				String key = keySetIterator.next();
				openTag += " " + key + "=\"" + attributes.get(key) + "\"";
			}	
		}

		openTag += ">";

		append(openTag);
	}

	public void enclose(String name, Map<String, String> attributes, String value)
	{
		openTag(name, attributes);
		append(value);
		closeTag(name);
	}
	
	public void enclose(String name, String value)
	{
		openTag(name);
		append(value);
		closeTag(name);
	}

	@Override
	public String toString()
	{
		return strBuilder.toString();
	}
}
