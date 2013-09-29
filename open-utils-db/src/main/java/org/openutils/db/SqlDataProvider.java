package org.openutils.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public interface SqlDataProvider
{
	public Collection<Map<?,?>> executeSelectingQuery(String sql, Map<String, Object> argumentMap) throws SQLException;
	public void executeNonSelectingQuery(final String sql) throws SQLException;
}
