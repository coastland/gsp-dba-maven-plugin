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

package jp.co.tis.gsp.tools.dba.dialect;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.TemporalType;

import org.seasar.extension.jdbc.gen.internal.dialect.OracleGenDialect;
import org.seasar.framework.util.StringUtil;

/**
 * @author kawasima
 */
public class ExtendedOracleGenDialect extends OracleGenDialect {
    public ExtendedOracleGenDialect() {
        super();
        columnTypeMap.put("date", ExtendedOracleColumnType.DATE);
        columnTypeMap.put("timestamp", ExtendedOracleColumnType.TIMESTAMP);
        columnTypeMap.put("number", new OracleColumnType(
                "number($p,$s)", BigDecimal.class) {
            @Override
            public Class<?> getAttributeClass(int length, int precision,
                                              int scale) {
                if (scale != 0) {
                    return BigDecimal.class;
                }
                if (precision == 1) {
                    return boolean.class;
                }
                if (precision < 5) {
                    return Short.class;
                }
                if (precision < 10) {
                    return Integer.class;
                }
                if (precision < 19) {
                    return Long.class;
                }
                return BigInteger.class;
            }
        });
    }
    
    @Override
    public ColumnType getColumnType(String typeName, int sqlType) {
        if (useOracleDate && StringUtil.equalsIgnoreCase(typeName, "date")) {
            return ExtendedOracleColumnType.DATE;
        }
        ColumnType columnType = columnTypeMap.get(typeName);
        if (columnType != null) {
            return columnType;
        }

        if (StringUtil.startsWithIgnoreCase(typeName, "timestamp")) {
            typeName = "timestamp";
        }
        return super.getColumnType(typeName, sqlType);
    }

    public static class ExtendedOracleColumnType extends OracleColumnType {
        private static ExtendedOracleColumnType DATE = new ExtendedOracleColumnType("date",
                java.sql.Date.class, false, TemporalType.DATE);
        
        private static ExtendedOracleColumnType TIMESTAMP = new ExtendedOracleColumnType("timestamp($s)",
                Timestamp.class, false, TemporalType.TIMESTAMP);
        
        public ExtendedOracleColumnType(String dataType, Class<?> attributeClass,
                boolean lob, TemporalType temporalType) {
            super(dataType, attributeClass, lob, temporalType);
        }
    }
}
