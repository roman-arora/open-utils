package org.openutils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;


public class FileUtils
{
	public static final String WINDOWS_DRIVE_REGEX = "[A-Z]:\\\\(.)*";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static int index = 0;
	
	public static boolean deleteDirectory(File path)
	{
		if(path.exists())
		{
			File[] files = path.listFiles();
			for(int i = 0; i < files.length; i++)
			{
				if(files[i].isDirectory())
				{
					deleteDirectory(files[i]);
				}
				else
				{
					files[i].delete();
				}
			}
		}
		return(path.delete());
	}

	private static String readFile(BufferedReader input) throws IOException
	{
		StringBuilder data = new StringBuilder();
		String line = null;
		while((line = input.readLine()) != null)
		{
			data.append(line + NEW_LINE);
		}

		return data.toString();
	}

	public static String readFile(File inputFile) throws IOException
	{
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		return readFile(input);
	}

	public static String readFile(String inputFile) throws IOException
	{
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		return readFile(input);
	}

	private static void saveTemporaryDocument(Document document, String folderName) throws IOException, Exception
	{
		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
		DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
		LSSerializer writer = impl.createLSSerializer();

		String str = writer.writeToString(document);
		str = str.replace("UTF-16", "UTF-8");

		File outputFolder = new File(folderName);
		outputFolder.mkdirs();

		String fileName = outputFolder.getAbsolutePath() + "/" + "file_" + String.valueOf(index) + ".xml";

		OutputStreamWriter outWriter = null;
		try
		{
			outWriter = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
			outWriter.write(str);	
		}
		finally
		{
			if(outWriter != null) 
			{
				outWriter.close();	
			}
				
		}

		index++;
	}

	public static Document transformDocument(String xsltFileName, Document transactionDoc) throws Exception
	{
		//TODO: Fix this
		//XsltTransformer transformer = new XsltTransformer();
		//return transformer.transform(transactionDoc, xsltFileName);
		return null;
	}
	
	/*public static ArrayList<Document> transformDocuments(ArrayList<Document> transactionDocuments) throws Exception
	{
		ArrayList<Document> documents = new ArrayList<Document>();
		XsltTransformer transformer = new XsltTransformer();
		for(int i = 0; i < transactionDocuments.size(); i++)
		{
			// TODO: Fix the file name property String fileName = "FixME.xsl";
			Document doc = transformer.transform(transactionDocuments.get(i), fileName);
			if(doc != null)
				documents.add(doc);
		}
		return documents;
	}*/
	 
	public static void writeDocuments(ArrayList<Document> transactionDocuments, String folderName) throws Exception
	{
		for(int i = 0; i < transactionDocuments.size(); i++)
		{
			Document document = transactionDocuments.get(i);
			saveTemporaryDocument(document, folderName);
		}
	}
	
	public static String getRelativeFileName(String absoluteFileName) 
	{
		int index = absoluteFileName.lastIndexOf('/');
		return absoluteFileName.substring(index + 1);
	}
	
	/**
	 * Returns a partially or fully qualified file name.  
	 */
	public static String getQualifiedFileName(String path, String fileName)
	{
		if(path != null) 
		{
			if( !path.endsWith("/") )
			{
				path += "/";
			}
			
			fileName = path + fileName; 
		}
		
		return fileName.replaceAll("\\\\", "/").replaceAll("//", "/");			
	}
	

	/**
	 * TODO Description.
	 * 
	 * @param fileName
	 * @param destination
	 */
	public static void write(File fileName, String destination) throws IOException {
		File outputFolder = new File(destination);
		outputFolder.mkdirs();
		Writer outWriter = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					fileName));
			outWriter = new OutputStreamWriter(new FileOutputStream(
					outputFolder), "UTF-8");
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				outWriter.write(line);
			}
		} 
		finally {
			if (outWriter != null) {
				outWriter.close();
			}
		}
	}
	
	/**
	 * Copy file from a source to a destination
	 * @param sourceFile Source file
	 * @param destFile destination file
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
}
