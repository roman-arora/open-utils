package org.openutils.io;

/**
 * 
 */
public interface ISystemProperties
{
	public Integer getAndIncrementIntProperty(String key);
	public boolean getBoolProperty(String key);
	public Integer getIntProperty(String key);
	public String getProperty(String Key);
	
	public void setProperty(String key, Object value);
	
	//TODO: I can remove this method if I create a @PreDestroy method
	public void save() throws Exception;
}
