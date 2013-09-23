package org.openutils.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public interface SqlProviderIfc
{
	public Collection<Map<?,?>> executeSelectingQuery(String sql, Map<String, Object> argumentMap) throws SQLException;
	public void executeSql(final String sql) throws Exception;
}
