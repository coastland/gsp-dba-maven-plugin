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

import java.sql.Timestamp;

import javax.persistence.TemporalType;

import org.seasar.extension.jdbc.gen.internal.dialect.MysqlGenDialect;

/**
 * @author Naoki Yamamoto
 */
public class ExtendedMysqlGenDialect extends MysqlGenDialect {

    public ExtendedMysqlGenDialect() {
        super();
        columnTypeMap.put("date", ExtendedMysqlColumnType.DATE);
        columnTypeMap.put("datetime", ExtendedMysqlColumnType.DATETIME);
        columnTypeMap.put("timestamp", ExtendedMysqlColumnType.TIMESTAMP);
        
    }

    public static class ExtendedMysqlColumnType extends MysqlColumnType {
        private static ExtendedMysqlColumnType DATE = new ExtendedMysqlColumnType("date",
                java.sql.Date.class, false, TemporalType.DATE);
       
        private static ExtendedMysqlColumnType DATETIME = new ExtendedMysqlColumnType("datetime", 
        		Timestamp.class, false, TemporalType.TIMESTAMP);
        
        private static ExtendedMysqlColumnType TIMESTAMP = new ExtendedMysqlColumnType("timestamp",
                Timestamp.class, false, TemporalType.TIMESTAMP);
       
        
        public ExtendedMysqlColumnType(String dataType, Class<?> attributeClass) {
            super(dataType, attributeClass);
        }

        public ExtendedMysqlColumnType(String dataType, Class<?> attributeClass,
                boolean lob, TemporalType temporalType) {
            super(dataType, attributeClass, lob);
            super.temporalType = temporalType;
        }
    }
}
