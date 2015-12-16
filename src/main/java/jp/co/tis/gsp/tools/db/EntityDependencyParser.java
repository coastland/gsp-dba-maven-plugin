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

package jp.co.tis.gsp.tools.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.tis.gsp.tools.GspToolsException;
import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.dialect.DialectFactory;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.ResultSetUtil;


public class EntityDependencyParser {
    private Map<String, Table> tableMap = new HashMap<String, Table>();

    public void parse(Connection conn, String url, String schema) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            Dialect dialect = DialectFactory.getDialect(url);
            String normalizedSchemaName = dialect.normalizeSchemaName(schema);
            List<String> tableNames = getAllTableNames(metaData, normalizedSchemaName);
            for (String tableName : tableNames) {
                parseReference(metaData, normalizedSchemaName, tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getAllTableNames(DatabaseMetaData metaData, String normalizedSchemaName)
            throws SQLException {
        List<String> allNames = new ArrayList<String>();
        String[] types = {"TABLE"};
        ResultSet rs = null;
        try {
            rs = metaData.getTables(null, normalizedSchemaName, "%", types);
            while (rs.next()) {
                allNames.add(rs.getString("TABLE_NAME"));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return allNames;
    }

    private void parseReference(DatabaseMetaData metaData, String normalizedSchemaName, String tableName) throws SQLException {
        ResultSet rs = null;
        try {
            rs = metaData.getImportedKeys(null, normalizedSchemaName, tableName);
            while (rs.next()) {
                String child = rs.getString("FKTABLE_NAME");
                String parent = rs.getString("PKTABLE_NAME");
                Table childTable = tableMap.get(child);
                if (childTable == null) {
                    childTable = new Table(child);
                    tableMap.put(child, childTable);
                }
                Table parentTable = tableMap.get(parent);
                if (parentTable == null) {
                    parentTable = new Table(parent);
                    tableMap.put(parent, parentTable);
                }
                childTable.parents.add(parentTable);
                parentTable.children.add(childTable);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    private void countUp(Table table, Map<String, Integer> tableReferenceCounts, int level) {
        for (Table childTable : table.children) {
            if (!StringUtils.equals(table.name, childTable.name))
                countUp(childTable, tableReferenceCounts, level + 1);
        }

        Integer currentLevel = tableReferenceCounts.get(table.name);
        if (level > currentLevel) {
            tableReferenceCounts.put(table.name, level);
        }
    }

    public List<String> getTableList() {
        List<Table> rootTables = new ArrayList<Table>();
        List<String> tableList = new ArrayList<String>();
        final Map<String, Integer> tableReferenceCounts = new HashMap<String, Integer>();
        for (Table table : tableMap.values()) {
            if (table.parents.isEmpty()) {
                rootTables.add(table);
            }
            tableReferenceCounts.put(table.name, 0);
            tableList.add(table.name);
        }
        if (!tableList.isEmpty() && rootTables.isEmpty()) {
            throw new GspToolsException("ルートとなるテーブルが見つかりません。循環参照になっていると思います！");
        }
        for (Table table : rootTables) {
            countUp(table, tableReferenceCounts, 0);
        }
        Collections.sort(tableList, new Comparator<String>() {
            @Override
            public int compare(String t1, String t2) {
                return tableReferenceCounts.get(t1) - tableReferenceCounts.get(t2);
            }
        });
        return tableList;
    }

    public static class Table {
        public String name;
        public List<Table> parents = new ArrayList<Table>();
        public List<Table> children = new ArrayList<Table>();

        public Table(String name) {
            this.name = name;
        }
    }
}
