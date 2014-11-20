package org.openutils.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openutils.collections.CamelCaseHashMap;
import org.openutils.collections.CamelCasePolicy;

public class JdbcSqlDataProvider implements SqlDataProvider
{
	private static final Log log = LogFactory.getLog(Exception.class);

	private Connection connection;
	private SqlStatementBuilder statementBuilder;
	private CamelCasePolicy casePolicy;

	@Inject
	public JdbcSqlDataProvider(Connection connection)
	{
		this.connection = connection;
		this.statementBuilder = new SqlStatementBuilder(connection, null);
		this.casePolicy = CamelCasePolicy.START_UPPER_CASE;
	}

	public ResultSet executeSelectingQuery(String query) throws SQLException
	{
		Statement statement = connection.createStatement();
		return statement.executeQuery(query);
	}

	@Override
	public Collection<Map<?, ?>> executeSelectingQuery(String sql, Map<String, Object> parameterMap) throws SQLException
	{
		if(parameterMap == null) {
			throw new IllegalArgumentException("Parameters must be provided.");
		}

		CallableStatement statement = statementBuilder.prepareStatement(sql, parameterMap);
		statement.execute();

		return resultSetToMap(statement.getResultSet());
	}

	/**
	 * Converts a ResultSet to a Collection of Map objects.
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private Collection<Map<?, ?>> resultSetToMap(ResultSet resultSet) throws SQLException
	{
		Collection<Map<?, ?>> records = new LinkedList<Map<?, ?>>();

		while(resultSet.next())
		{
			Map<String, Object> valueMap = new CamelCaseHashMap<Object>(casePolicy);

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

	@Override
	public void executeNonSelectingQuery(String sql) throws SQLException
	{
		// TODO Auto-generated method stub
	}
}
