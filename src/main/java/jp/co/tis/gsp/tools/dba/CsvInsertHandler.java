/*
 * Copyright (C) 2015 coastland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.tis.gsp.tools.dba;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.tis.gsp.tools.dba.dialect.Dialect;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.tiger.CollectionsUtil;


public class CsvInsertHandler {
	private static final Map<String, Integer> TYPE_NAMES = new HashMap<String, Integer>();
	private final Connection conn;
	private final List<String> columns;
	private final List<Integer> types;
	private PreparedStatement stmt;
	private final String schema;
	private final String tableName;
	private final Dialect dialect;

	static {
		TYPE_NAMES.put("VARCHAR", Types.VARCHAR);
		TYPE_NAMES.put("DATE", Types.DATE);
		TYPE_NAMES.put("TIMESTAMP", Types.TIMESTAMP);
		TYPE_NAMES.put("ARRAY", Types.ARRAY);
	}
	
	public CsvInsertHandler(Connection conn, Dialect dialect, String schema, String tableName, String[] headers) throws SQLException {
		this.conn = conn;
		this.schema = schema;
		this.tableName = tableName;
		this.columns = CollectionsUtil.newArrayList(headers.length);
		this.types   = CollectionsUtil.newArrayList(headers.length);
		this.dialect = dialect;
		initialize(headers);
	}

	public void prepare() throws SQLException {
		StringBuilder sb = new StringBuilder("INSERT INTO ");
		sb.append(tableName).append("(")
			.append(StringUtils.join(columns, ','))
			.append(") VALUES (");
		for(int i=0; i<columns.size(); i++) {
			sb.append("?");
			if (i < columns.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		stmt = conn.prepareStatement(sb.toString());
	}

	public void setObject(int parameterIndex, String value) throws SQLException {
		dialect.setObjectInStmt(stmt, parameterIndex, value, types.get(parameterIndex-1));
	}

	public int execute() throws SQLException {
		return stmt.executeUpdate();
	}

	public void close() throws SQLException {
		stmt.close();
		conn.commit();
	}

	private String initialize(String[] headers) throws SQLException {
		for (String header : headers) {
			if (header.indexOf(':') >= 0) {
				String[] columnTokens = header.split(":",2);
				columns.add(columnTokens[0]);
				types.add(getTypeByName(columnTokens[1]));
			} else {
				columns.add(header);
				types.add(dialect.guessType(conn, schema, tableName, header));
			}
		}
		return null;
	}

	private int getTypeByName(String typeName) {
		Integer type = TYPE_NAMES.get(typeName);
		if (type == null) {
			return Types.VARCHAR;
		}
		return type;
	}
}
