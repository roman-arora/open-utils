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

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.openutils.map.CamelCaseHashMap;
import org.openutils.map.CamelCasePolicy;

import com.core.common.persistence.HibernateSessionFactory;
import com.core.utils.ConnectionFactory;

public class JbdcSqlProvider implements SqlProviderIfc
{
	private static final Logger log = Logger.getLogger(Exception.class);
	private static final int ZERO = 0;

	public Collection<Map<?,?>> executeSelectingQuery(String sql, Map<String, Object> argumentMap) throws SQLException
	{
		Connection connection = ConnectionFactory.getInstance().getConnectionFromSession();
		if(connection == null)
			throw new NullPointerException("Connection must not be null.");

		CallableStatement statement = prepareStatement(connection, sql, argumentMap);
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

	private CallableStatement prepareStatement(Connection connection, String sql, Map<String, Object> argumentMap) throws SQLException
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

	public void executeSql(final String sql) throws Exception
	{
		Session session = null;
		try 
		{
			session = HibernateSessionFactory.openSession();

			Transaction tx = null;
			tx = session.beginTransaction();
			session.doWork(
					new Work() {
						public void execute(Connection connection) throws SQLException 
						{ 
							Statement statement = connection.createStatement();
							statement.execute(sql);
						}					
					}
					);
			tx.commit();
		}
		catch(Exception e)
		{
			throw e;
			//throw new EdiSqlException(EdiSqlException.QUERY_EXECUTION_FAILED, null, e);
		}
		finally
		{
			if(session != null)
			{
				//session.close();
			}
		}
	}
}