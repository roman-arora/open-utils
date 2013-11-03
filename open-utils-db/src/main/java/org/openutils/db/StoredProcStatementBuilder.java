package org.openutils.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class StoredProcStatementBuilder extends SqlStatementBuilder
{
	@Inject
	public StoredProcStatementBuilder(Connection connection, Map<String, String> properties)
	{
		super(connection, properties);
	}

	public Statement createStatement(String storedProcName, List<String> parameters, Map<String, Object> parameterMap) throws SQLException
	{
		return super.prepareStatement(getCallStr(storedProcName, parameters), parameters, parameterMap);
	}

	private String getCallStr(String storedProcName, List<String> parameters)
	{
		String paramStr = "";
		if( parameters != null && !parameters.isEmpty() )
		{
			paramStr += "(";

			for(int i = 0; i < parameters.size(); i++)
			{
				paramStr += "#" + parameters.get(i) + ",";
			}

			paramStr = paramStr.substring(0, paramStr.length() - 1) + ")";
		}

		return "{ call " + storedProcName + paramStr + " }";
	}

	/*
	private Map<String, Object> getSqlArguments(List<String> parameters, Map<String, Object> valueMap)
	{
		if(valueMap == null && !parameters.isEmpty() )
			throw new NullPointerException("Value map must not be null.");

		HashMap<String, Object> arguments = new HashMap<String, Object>();
		if( parameters != null && !parameters.isEmpty() )
		{
			for(int i = 0; i < parameters.size(); i++)
			{
				arguments.put( parameters.get(i), valueMap.get( parameters.get(i) ) );
			}
		}

		return arguments;
	}
	 */
}
