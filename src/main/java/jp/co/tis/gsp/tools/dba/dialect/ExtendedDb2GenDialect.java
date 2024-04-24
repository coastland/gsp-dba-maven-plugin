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
import java.sql.Timestamp;

import javax.persistence.TemporalType;

import org.seasar.extension.jdbc.gen.internal.dialect.Db2GenDialect;

/**
 * @author Naoki Yamamoto
 */
public class ExtendedDb2GenDialect extends Db2GenDialect {
	
    public ExtendedDb2GenDialect() {
        super();
        columnTypeMap.put("date", ExtendedDb2ColumnType.DATE);
        columnTypeMap.put("timestamp", ExtendedDb2ColumnType.TIMESTAMP);
        
        columnTypeMap.put("decimal", new Db2ColumnType(
                "decimal($p,$s)", BigDecimal.class) {
            @Override
            public Class<?> getAttributeClass(int length, int precision,
                                              int scale) {
                
            	if (scale == 0 && precision == 1) {
                    return boolean.class;
                }
                
                return BigDecimal.class;
            }
        });
    }

    public static class ExtendedDb2ColumnType extends Db2ColumnType {
        private static ExtendedDb2ColumnType DATE = new ExtendedDb2ColumnType("date",
                java.sql.Date.class, false, TemporalType.DATE);
        
        private static ExtendedDb2ColumnType TIMESTAMP = new ExtendedDb2ColumnType("timestamp",
                Timestamp.class, false, TemporalType.TIMESTAMP);

        public ExtendedDb2ColumnType(String dataType, Class<?> attributeClass,
                boolean lob, TemporalType temporalType) {
            super(dataType, attributeClass, lob);
            super.temporalType = temporalType;
        }
    }
}
