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

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.tis.gsp.tools.db.beans.Column;

public class TypeMapper {
	static Pattern DATA_TYPE_PTN = Pattern.compile("(.*?)(?:\\((\\d+)(?:,(\\d+))?\\))?");
	static Map<String, Integer> nameToTypeMap = new HashMap<String, Integer>();

    static {
        nameToTypeMap.put("VARCHAR", Types.VARCHAR);
        nameToTypeMap.put("CHAR", Types.CHAR);
        nameToTypeMap.put("TEXT", Types.CLOB);
        nameToTypeMap.put("INT",  Types.INTEGER);
        nameToTypeMap.put("INTEGER",  Types.INTEGER);
        nameToTypeMap.put("BIGINT", Types.BIGINT);
        nameToTypeMap.put("BOOLEAN", Types.BOOLEAN);
    }

	private Map<Integer, String> typeToNameMap;

	public TypeMapper() {

	}
	public TypeMapper(Map<Integer, String> typeToNameMap) {
        this();
		setTypeToName(typeToNameMap);
	}

	public void setTypeToName(Map<Integer, String> typeToNameMap) {
		this.typeToNameMap = typeToNameMap;
	}

	public void convert(Column column) {
		String originalName = column.getDataType();
		Integer sqlType = nameToTypeMap.get(originalName);

		// 該当するマッピングがなければ変換しない
		if (sqlType == null || typeToNameMap == null)
			return;

		String dbType = typeToNameMap.get(sqlType);

		// 該当するマッピングがなければ変換しない
		if (dbType == null)
			return;

		Matcher m = DATA_TYPE_PTN.matcher(dbType);
		if (m.matches()) {
			int c = m.groupCount();

			if (m.group(1) != null) {
				dbType = m.group(1);
			}
			if (m.group(2) != null) {
                try {
                    Integer length = Integer.parseInt(m.group(2));
                    if (length != null && length != 0)
                        column.setLength(length);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(column.getName() + " " + column.getDataType(), ex);
                }
			}
		}
		column.setDataType(dbType);

	}

}
