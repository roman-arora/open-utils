package org.openutils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReflectionUtils
{
	private static final Log log = LogFactory.getLog(  ReflectionUtils.class.getName() );
	
	public static Field getDeclaredField(Class<?> klass, String fieldName)
	{
		try
		{
			return klass.getDeclaredField(fieldName);
		}
		catch(NoSuchFieldException nsfe)
		{
			if(klass.getSuperclass() != null && klass.getSuperclass() != klass)
			{
				return getDeclaredField(klass.getSuperclass(), fieldName);
			}
		}
		
		return null;
	}
	
	public static Object getInstanceField(Class<?> klass, Object instance, String getMethodName, Class<?>[] paramTypes)
	{
		Method getMethod = null;
		try
		{
			 getMethod = klass.getDeclaredMethod(getMethodName, paramTypes);			
		}
		catch(NoSuchMethodException nsme)
		{
			if(klass.getSuperclass() != null && klass.getSuperclass() != klass)
			{
				return getInstanceField(klass.getSuperclass(), instance, getMethodName, paramTypes);
			}
			else
			{
				log.warn( "No method found with name: " + nsme.getMessage() );
			}
		}
		
		if(getMethod != null)
		{
			try
			{
				return getMethod.invoke(instance, paramTypes);	
			}
			catch(Exception e)
			{
				log.error( e.getMessage() );
			}
		}
		
		return null;
	}
	
	//TODO: This method should throw a NoSuchMethodException
	public static void setInstanceField(Class<?> klass, Object instance, String setMethodName, Object value)
	{
		setInstanceField(klass, instance, setMethodName, value.getClass(), value);
	}

	//TODO: This method should throw a NoSuchMethodException
	public static void setInstanceField(Class<?> klass, Object instance, String setMethodName, Class<?> propertyClass, Object value)
	{
		try
		{		
			Method setMethod = null;
			try
			{
				 setMethod = klass.getDeclaredMethod(setMethodName, propertyClass);			
			}
			catch(NoSuchMethodException nsme)
			{
				if(klass.getSuperclass() != null && klass.getSuperclass() != klass)
				{
					setInstanceField(klass.getSuperclass(), instance, setMethodName, value);
				}
				else
				{
					log.warn( "No method found with name: " + nsme.getMessage() );
				}
			}
			
			if(setMethod != null)
				setMethod.invoke(instance, value);
		}
		catch(Exception e)
		{
			log.error("Failed to assign value to instance member variable");
		}
	}
}
