package org.openutils.collections;

public enum CamelCasePolicy
{
	START_LOWER_CASE,
	START_UPPER_CASE;
	
	public static String camelCase(CamelCasePolicy policy, String param)
	{
		if(policy.equals(CamelCasePolicy.START_LOWER_CASE) )
		{
			param = param.substring(0, 1).toLowerCase() + param.substring(1);
		}
		else if(policy.equals(CamelCasePolicy.START_UPPER_CASE) )
		{
			param = param.substring(0, 1).toUpperCase() + param.substring(1);
		}
		
		return param;
	}
}
