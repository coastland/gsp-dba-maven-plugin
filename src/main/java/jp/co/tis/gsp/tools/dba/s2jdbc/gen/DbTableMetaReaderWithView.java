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

package jp.co.tis.gsp.tools.dba.s2jdbc.gen;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.gen.dialect.GenDialect;
import org.seasar.extension.jdbc.gen.internal.meta.DbTableMetaReaderImpl;
import org.seasar.extension.jdbc.gen.meta.DbForeignKeyMeta;
import org.seasar.extension.jdbc.gen.meta.DbTableMeta;
import org.seasar.extension.jdbc.gen.meta.DbUniqueKeyMeta;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.util.ArrayMap;
import org.seasar.framework.util.ResultSetUtil;

import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.util.DialectUtil;

public class DbTableMetaReaderWithView extends DbTableMetaReaderImpl {
	public DbTableMetaReaderWithView(DataSource dataSource, GenDialect dialect,
			String schemaName, String tableNamePattern,
			String ignoreTableNamePattern, boolean readComment) {
		super(dataSource, dialect, schemaName, tableNamePattern,
				ignoreTableNamePattern, readComment);
	}

    protected List<DbTableMeta> getDbTableMetaList(DatabaseMetaData metaData,
        String schemaName) {
	    List<DbTableMeta> result = new ArrayList<DbTableMeta>();
	    try {
	        ResultSet rs = metaData.getTables(null, schemaName, null,
	                new String[] { "TABLE", "VIEW" });
	        try {
	            while (rs.next()) {
	                DbTableMeta dbTableMeta = new DbTableMeta();
	                dbTableMeta.setCatalogName(rs.getString("TABLE_CAT"));
	                dbTableMeta.setSchemaName(rs.getString("TABLE_SCHEM"));
	                dbTableMeta.setName(rs.getString("TABLE_NAME"));
	                if (readComment) {
	                    dbTableMeta.setComment(rs.getString("REMARKS"));
	                }
	                if (isTargetTable(dbTableMeta)) {
	                    result.add(dbTableMeta);
	                }
	            }
	            return result;
	        } finally {
	            ResultSetUtil.close(rs);
	        }
	    } catch (SQLException e) {
	        throw new SQLRuntimeException(e);
	    }
    }

    @Override
    protected List<DbUniqueKeyMeta> getDbUniqueKeyMetaList(
            DatabaseMetaData metaData, DbTableMeta tableMeta) {

        if (!dialect.supportsGetIndexInfo(tableMeta.getCatalogName(), tableMeta
                .getSchemaName(), tableMeta.getName())) {
            logger.log("WS2JDBCGen0002", new Object[] {
                    tableMeta.getCatalogName(), tableMeta.getSchemaName(),
                    tableMeta.getName() });
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        Map<String, DbUniqueKeyMeta> map = new ArrayMap();
        try {
        	// テーブルじゃなければ読み飛ばす！
        	String typeName = getObjectTypeName(metaData, tableMeta);
        	String tableName = tableMeta.getName();

        	// TABLE は H2 以外のDB用、BASE TABLE は H2 の 2.x 系用です。
        	// https://www.h2database.com/html/systemtables.html#information_schema_tables の TABLE_TYPE の値です。
        	if (!StringUtils.equals(typeName, "TABLE") && !StringUtils.equals(typeName, "BASE TABLE")) {
        		return Collections.emptyList();
        	}

            ResultSet rs = metaData
                    .getIndexInfo(tableMeta.getCatalogName(), tableMeta
                            .getSchemaName(), tableName, true, false);
            try {
                while (rs.next()) {
                    String name = rs.getString("INDEX_NAME");
                    if (!map.containsKey(name)) {
                        DbUniqueKeyMeta ukMeta = new DbUniqueKeyMeta();
                        ukMeta.setName(name);
                        map.put(name, ukMeta);
                    }
                    DbUniqueKeyMeta ukMeta = map.get(name);
                    ukMeta.addColumnName(rs.getString("COLUMN_NAME"));
                }
            } finally {
                ResultSetUtil.close(rs);
            }

            DbUniqueKeyMeta[] array = map.values().toArray(
                    new DbUniqueKeyMeta[map.size()]);
            return Arrays.asList(array);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    @Override
    protected Set<String> getPrimaryKeySet(DatabaseMetaData metaData,
            DbTableMeta tableMeta) {
    	Set<String> result = new HashSet<String>();
    	Dialect gspDialect = DialectUtil.getDialect();
        try {
	        String typeName = getObjectTypeName(metaData, tableMeta);
	        String tableName = tableMeta.getName();
	        ViewAnalyzer viewAnalyzer = null;
	        if (StringUtils.equals(typeName, "VIEW")) {
	        	String sql = gspDialect.getViewDefinition(metaData.getConnection(), tableName, tableMeta);
	        	viewAnalyzer = new ViewAnalyzer();
	        	viewAnalyzer.parse(sql);
	        	if (viewAnalyzer.isSimple()) {
	        		tableName = viewAnalyzer.getTableName().toUpperCase();
	        	} else {
	        		return Collections.emptySet();
	        	}
	        }
	        ResultSet rs = metaData.getPrimaryKeys(tableMeta.getCatalogName(),
	        		tableMeta.getSchemaName(), tableName);

            try {
                while (rs.next()) {
                    result.add(rs.getString("COLUMN_NAME"));
                }
            } finally {
                ResultSetUtil.close(rs);
            }

            if (viewAnalyzer != null && !result.isEmpty()) {
            	Set<String> viewPKs = new TreeSet<String>();
            	for (String pkColumn : result) {
            		if (viewAnalyzer.getColumnNames().contains(pkColumn.toUpperCase())) {
            			String alias = viewAnalyzer.getAlias((pkColumn.toUpperCase()));
            			viewPKs.add(StringUtils.isEmpty(alias) ? pkColumn : alias);
            		}
            	}
            	System.out.println("View Pks" + viewPKs);
            	if (viewPKs.size() == result.size())
            		return viewPKs;
            	else
            		return Collections.emptySet();
            }
            return result;
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    @Override
    protected List<DbForeignKeyMeta> getDbForeignKeyMetaList(
            DatabaseMetaData metaData, DbTableMeta tableMeta) {
        @SuppressWarnings("unchecked")
        Map<String, DbForeignKeyMeta> map = new ArrayMap();
        Dialect gspDialect = DialectUtil.getDialect();
        try {
            String typeName = getObjectTypeName(metaData, tableMeta);
	        String tableName = tableMeta.getName();
	        ViewAnalyzer viewAnalyzer = null;
	        if (StringUtils.equals(typeName, "VIEW")) {
	        	String sql = gspDialect.getViewDefinition(metaData.getConnection(), tableMeta.getName(), tableMeta);
	        	viewAnalyzer = new ViewAnalyzer();
	        	viewAnalyzer.parse(sql);
	        	if (viewAnalyzer.isSimple()) {
	        		tableName = viewAnalyzer.getTableName();
	        	} else {
	        		return Collections.emptyList();
	        	}
	        }
            ResultSet rs = metaData.getImportedKeys(tableMeta.getCatalogName(),
                    tableMeta.getSchemaName(), tableName);
            try {
                while (rs.next()) {
                    String name = rs.getString("FK_NAME");
                    if (!map.containsKey(name)) {
                        DbForeignKeyMeta fkMeta = new DbForeignKeyMeta();
                        fkMeta.setName(name);
                        fkMeta.setPrimaryKeyCatalogName(rs
                                .getString("PKTABLE_CAT"));
                        fkMeta.setPrimaryKeySchemaName(rs
                                .getString("PKTABLE_SCHEM"));
                        fkMeta.setPrimaryKeyTableName(rs
                                .getString("PKTABLE_NAME"));
                        map.put(name, fkMeta);
                    }
                    DbForeignKeyMeta fkMeta = map.get(name);
                    fkMeta.addPrimaryKeyColumnName(rs
                            .getString("PKCOLUMN_NAME"));
                    fkMeta.addForeignKeyColumnName(rs
                            .getString("FKCOLUMN_NAME"));
                }
            } finally {
                ResultSetUtil.close(rs);
            }
            if (viewAnalyzer != null && !map.isEmpty()) {
            	
            	Map<String, DbForeignKeyMeta> tmpMap = new ArrayMap(map);
            	
            	for (DbForeignKeyMeta fkMeta : tmpMap.values()) {
            		boolean fkContains = true;
            		for (String fkColumn : fkMeta.getForeignKeyColumnNameList()) {
            			fkContains &= viewAnalyzer.getColumnNames().contains(fkColumn.toUpperCase());
            		}
            		if (!fkContains)
            			map.remove(fkMeta.getName());
            	}
            }
            
            DbForeignKeyMeta[] array = map.values().toArray(
                    new DbForeignKeyMeta[map.size()]);
            return Arrays.asList(array);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    protected String getObjectTypeName(DatabaseMetaData metaData, DbTableMeta tableMeta) throws SQLException {
    	ResultSet rs = metaData.getTables(tableMeta.getCatalogName(), tableMeta.getSchemaName(), tableMeta.getName(), null);
    	try {
    		rs.next();
    		String typeName = rs.getString("TABLE_TYPE");
    		return typeName;
    	} finally {
    		ResultSetUtil.close(rs);
    	}
    }

}
