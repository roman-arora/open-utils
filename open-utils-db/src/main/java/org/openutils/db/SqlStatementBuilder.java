package org.openutils.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility methods for creating prepared statements
 */
public class SqlStatementBuilder
{
	public static final String PARAM_NAME_PREFIX = "paramNamePrefix";
	public static final String DEFERRED_PARAM_VALUE_SYMBOL = "deferredParamValueSymbol";

	private static final Log log = LogFactory.getLog(Exception.class);
	private static final int ZERO = 0;

	private Connection connection;
	private Map<String, String> properties;

	@Inject
	public SqlStatementBuilder(Connection connection, Map<String,String> properties)
	{
		this.connection = connection;
		this.properties = properties;

		if(properties == null)
		{
			this.properties = new HashMap<String, String>();
			this.properties.put(PARAM_NAME_PREFIX, "#");
			this.properties.put(DEFERRED_PARAM_VALUE_SYMBOL, "?");
		}
	}

	public CallableStatement prepareStatement(String sql, Map<String, Object> parameterMap) throws SQLException
	{
		List<String> parameters = getSqlParameters(sql);
		sql = formatSqlParameters(sql, parameterMap.keySet());

		CallableStatement statement = connection.prepareCall(sql);

		if(parameters.size() > 0)
		{
			for(int i = ZERO; i < parameters.size(); i++)
			{
				Object value = parameterMap.get(parameters.get(i));
				if(value == null) {
					log.warn("Callable statement parameter value is null.");
				}

				populateStatementParameter(statement, i + 1, value);
			}
		}

		return statement;
	}

	private String formatSqlParameters(String sql, Set<String> params)
	{
		String parameterPrefix = properties.get(PARAM_NAME_PREFIX);
		String deferredValueId = properties.get(DEFERRED_PARAM_VALUE_SYMBOL);
		for(String param : params)
		{
			sql = sql.replaceAll(parameterPrefix + param, deferredValueId);
		}

		return sql;
	}

	/* Get SQL query parameter names */
	private static List<String> getSqlParameters(String sql)
	{
		String arguments = sql.substring(sql.indexOf("(") + 1, sql.indexOf(")"));
		String[] sqlParams = arguments.split(",");

		List<String> params = new ArrayList<String>();
		for(String arg : sqlParams)
		{
			params.add(arg.substring(1));
		}

		return params;
	}

	private static void populateStatementParameter(CallableStatement statement, int i, Object value) throws SQLException
	{
		if(value instanceof Long)
		{
			statement.setLong(i, (Long) value);
		}
		else if(value instanceof Integer)
		{
			statement.setInt(i, (Integer) value);
		}
		else if(value instanceof String)
		{
			statement.setString(i, (String) value);
		}
		else if(value instanceof Date)
		{
			statement.setTimestamp(i, new Timestamp(((Date) value).getTime()));
		}
		else if(value == null)
		{
			statement.setString(i, null);
		}
		else
		{
			log.warn("Unrecognize parameter type!");
			statement.setString(i, value.toString());
		}
	}
}
