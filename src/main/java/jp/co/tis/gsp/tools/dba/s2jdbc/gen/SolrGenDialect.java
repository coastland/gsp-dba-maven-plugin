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

import org.seasar.extension.jdbc.gen.internal.dialect.StandardGenDialect;

import java.sql.Connection;
import java.util.List;

public class SolrGenDialect extends StandardGenDialect {
	public SolrGenDialect() {
		super();
		columnTypeMap.put("ARRAY", SolrColumnType.ARRAY);
        columnTypeMap.put("VARCHAR_ARRAY", columnTypeMap.get("VARCHAR"));
        columnTypeMap.put("DATE_ARRAY", columnTypeMap.get("DATE"));
        columnTypeMap.put("INT_ARRAY", columnTypeMap.get("INT"));
        columnTypeMap.put("LONG_ARRAY", columnTypeMap.get("LONG"));
	}

    @Override
    public String getName() {
        return "solr";
    }

    @Override
    public boolean supportsCommentInCreateTable() {
        return false;
    }

    @Override
    public boolean supportsCommentOn() {
        return false;
    }

    @Override
    public ColumnType getColumnType(String typeName, int sqlType) {
        ColumnType columnType = columnTypeMap.get(typeName);
        return columnType != null ? columnType : fallbackColumnTypeMap
                .get(sqlType);
    }

    public static class SolrColumnType extends StandardColumnType {
    	private static SolrColumnType ARRAY = new SolrColumnType("ARRAY", List.class);

		public SolrColumnType(String dataType, Class<?> attributeClass) {
			super(dataType, attributeClass);

		}

		public SolrColumnType(String dataType, Class<?> attributeClass,
				boolean lob) {
            super(dataType, attributeClass, lob);
        }
        @Override
        public Class<?> getAttributeClass(int length, int precision, int scale) {
            return attributeClass;
        }
    }

    @Override
    public boolean isAutoIncrement(Connection connection, String catalogName, String schemaName, String tableName, String columnName) {
        return false;
    }
}
