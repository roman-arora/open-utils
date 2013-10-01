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

public class XmlUtils 
{
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
	
	public static String getCloseTag(String name)
	{
		return "</" + name + ">" /*+ Constants.NEW_LINE*/;
	}

	public static String getOpenTag(String name)
	{
		return  "<" + name + ">" /*+ Constants.NEW_LINE*/;
	}

	public static String removeLeadingZeroes(String str)
	{
		String regex = "^0*";
	    return str.replaceAll(regex, "");
	}
	
	public static String getOpenTag(String name, Map<String, String> attributes)
	{
		String openTag = "<" + name;

		if( !attributes.isEmpty() )
		{
			Iterator<String> keySetIterator = attributes.keySet().iterator();
			while(keySetIterator.hasNext())
			{
				String key = keySetIterator.next();
				openTag += " " + key + "=\"" + attributes.get(key) + "\"";
			}	
		}

		openTag += ">"  /* + Constants.NEW_LINE*/;

		return openTag;
	}

	public static void encloseInTags(StringBuilder builder, String name, Map<String, String> attributes) 
	{
		builder.insert( 0, getOpenTag(name, attributes) );
		builder.append( getCloseTag(name) );
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
}
