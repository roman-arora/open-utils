package org.openutils.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openutils.collect.CamelCaseHashMap;
import org.openutils.collect.CamelCasePolicy;

public class JbdcSqlDataProvider implements SqlDataProvider
{
	private static final Log log = LogFactory.getLog(Exception.class);
	private static final int ZERO = 0;

	// @Inject
	private Connection connection;
	private CamelCasePolicy casePolicy;
	
	public JbdcSqlDataProvider()
	{
		
	}
	
	public Collection<Map<?,?>> executeSelectingQuery(String sql, Map<String, Object> argumentMap) throws SQLException
	{
		/*Connection connection = ConnectionFactory.getInstance().getConnectionFromSession();*/
		if(connection == null)
			throw new NullPointerException("Connection must not be null.");

		CallableStatement statement = prepareStatement(sql, argumentMap);
		statement.execute();

		ResultSet resultSet = statement.getResultSet();

		Collection<Map<?,?>> records = new LinkedList<Map<?,?>>();
		while( resultSet.next() )
		{
			Map<String, Object> valueMap = new CamelCaseHashMap<Object>(CamelCasePolicy.START_UPPER_CASE);

			// Get result set meta data
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int numColumns = rsmd.getColumnCount();

			// Get the column names; column indices start from 1
			for(int i = 1; i < numColumns + 1; i++)
			{
				String columnName = rsmd.getColumnName(i);
				Object value = resultSet.getObject(i);
				valueMap.put(columnName, value);
			}

			records.add(valueMap);
		}

		return records;
	}

	private CallableStatement prepareStatement(String sql, Map<String, Object> argumentMap) throws SQLException
	{
		List<String> parameters = getSqlParameters(sql);
		sql = formatSqlParameters(sql, argumentMap.keySet() );

		CallableStatement statement = connection.prepareCall(sql);

		if(parameters.size() > 0)
		{
			for(int i = ZERO; i < parameters.size(); i++)
			{
				Object value = argumentMap.get( parameters.get(i) ) ;
				if(value == null)
					log.warn("Callable statement parameter value is null");

				populateStatementParameter(statement, i + 1, value);
			}
		}

		return statement;
	}

	private String formatSqlParameters(String sql, Set<String> params)
	{
		for(String param : params)
		{
			sql = sql.replaceAll("#" + param, "?");
		}

		return sql;
	}

	private List<String> getSqlParameters(String sql)
	{
		String arguments = sql.substring( sql.indexOf("(") + 1, sql.indexOf(")") );
		String[] sqlParams = arguments.split(",");

		List<String> params = new ArrayList<String>();
		for(String arg : sqlParams)
		{
			params.add( arg.substring(1) );
		}

		return params;
	}

	private void populateStatementParameter(CallableStatement statement, int i, Object value) throws SQLException
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
			statement.setTimestamp(  i, new Timestamp( ((Date)value).getTime() )  );
		}
		else if(value == null)
		{
			statement.setString(i, null);
		}
		else
		{
			log.warn("Unrecognize parameter type!");
			statement.setString( i, value.toString() );
		}
	}

	@Override
	public void executeNonSelectingQuery(String sql) throws SQLException
	{
		// TODO Auto-generated method stub
		
	}
}
