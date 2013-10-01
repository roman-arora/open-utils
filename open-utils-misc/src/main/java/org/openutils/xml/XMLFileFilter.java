package org.openutils.xml;

import java.io.File;

public class XMLFileFilter implements java.io.FileFilter
{
	public boolean accept(File f)
	{
		if(f.isDirectory())
			return false;
	
		String name = f.getName().toLowerCase();
		return name.endsWith("xml");
	}
}
