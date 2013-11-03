package org.open.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.junit.Assert;
import org.openutils.db.JdbcSqlDataProvider;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Unit test for JdbcSqlDataProvider.
 */
public class JdbcSqlDataProviderTest
{
	// I could make connection & teardown as before / after test methods, do it!!

	@BeforeTest
	public void setUp() throws Exception
	{
		Class.forName("org.h2.Driver");
	}

	@Test
	public void executeSelectingQueryWithParams() throws Exception {

		Connection conn = null;
		try
		{
			conn = DriverManager.getConnection("jdbc:h2:~/test");

			JdbcSqlDataProvider provider = new JdbcSqlDataProvider(conn);
			provider.executeNonSelectingQuery("CREATE TABLE testTable");

			//Assert.assertNotNull(rs.next());
		}
		finally
		{
			if(conn != null)
			{
				try
				{
					conn.close();
				}
				catch(Exception e)
				{
					// Ignore
				}
			}
		}
	}

	@Test
	public void executeSelectingQuery() throws Exception
	{
		Connection conn = null;
		try
		{
			conn = DriverManager.getConnection("jdbc:h2:~/test");

			JdbcSqlDataProvider provider = new JdbcSqlDataProvider(conn);
			ResultSet rs = provider.executeSelectingQuery("SELECT 1");
			Assert.assertNotNull(rs.next());
		}
		finally
		{
			if(conn != null)
			{
				try
				{
					conn.close();
				}
				catch(Exception e)
				{
					// Ignore
				}
			}
		}
	}
}
