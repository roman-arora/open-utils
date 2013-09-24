package org.openutils.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;

public class HibernateSqlDataProvider /* implements SqlDataProvider */
{
/*
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
		}
		finally
		{
			if(session != null)
			{
				//session.close();
			}
		}
	}
*/
}
