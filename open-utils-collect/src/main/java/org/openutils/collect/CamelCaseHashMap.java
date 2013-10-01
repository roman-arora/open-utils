/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 */
package org.openutils.collect;

import java.util.HashMap;

public class CamelCaseHashMap<V> extends HashMap<String, V>
{
	private static final long serialVersionUID = -4518486669478024943L;
	private CamelCasePolicy camelCasePolicy;
	
	public CamelCaseHashMap(CamelCasePolicy camelCaseStrategy)
	{
		this.camelCasePolicy = camelCaseStrategy;
	}
	
	private String camelCaseKey(String key)
	{
		if(camelCasePolicy.equals(CamelCasePolicy.START_LOWER_CASE) )
		{
			key = key.substring(0, 1).toLowerCase() + key.substring(1);
		}
		else if(camelCasePolicy.equals(CamelCasePolicy.START_UPPER_CASE) )
		{
			key = key.substring(0, 1).toUpperCase() + key.substring(1);
		}
		
		return key;
	}
	
	@Override
	public V get(Object key)
	{
		return super.get( camelCaseKey((String)key) );
	}

	@Override
	public V put(String key, V value)
	{
		return super.put( camelCaseKey(key) , value);
	}
}
