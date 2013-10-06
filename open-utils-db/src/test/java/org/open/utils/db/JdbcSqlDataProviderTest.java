package org.open.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.openutils.db.JdbcSqlDataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for JdbcSqlDataProvider.
 */
public class JdbcSqlDataProviderTest 
{
	@Test
    public void testExecuteSelectingQuery() throws Exception 
    {
		Class.forName("org.h2.Driver");
		Connection conn = null;
		try
		{
			conn =  DriverManager.getConnection("jdbc:h2:~/test");
			
			JdbcSqlDataProvider provider = new JdbcSqlDataProvider(conn);
			provider.executeSelectingQuery("SELECT 1", null);
		}
		finally
		{
			if(conn != null) {
				try {
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
