package org.open.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.junit.Assert;
import org.openutils.db.JdbcSqlDataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for JdbcSqlDataProvider.
 */
public class JdbcSqlDataProviderTest
{
	// TODO: add test for testExecuteSelectingQuery that takes query & map of
	// parameters

	@Test
	public void testExecuteSelectingQuery() throws Exception
	{
		Class.forName("org.h2.Driver");
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
