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

import org.seasar.extension.jdbc.gen.internal.dialect.PostgreGenDialect;

/**
 * @author Naoki Yamamoto
 */
public class ExtendedPostgreGenDialect extends PostgreGenDialect {

    public ExtendedPostgreGenDialect() {
        super();
        columnTypeMap.put("date", ExtendedPostgreColumnType.DATE);
        columnTypeMap.put("timestamp", ExtendedPostgreColumnType.TIMESTAMP);
        columnTypeMap.put("timestamptz", ExtendedPostgreColumnType.TIMESTAMPTZ);
        columnTypeMap.put("bool", ExtendedPostgreColumnType.BOOL);
    }

    public static class ExtendedPostgreColumnType extends PostgreColumnType {
        private static ExtendedPostgreColumnType DATE = new ExtendedPostgreColumnType("date",
                java.sql.Date.class, false, TemporalType.DATE);
        
        private static ExtendedPostgreColumnType TIMESTAMP = new ExtendedPostgreColumnType("timestamp",
                Timestamp.class, false, TemporalType.TIMESTAMP);
        
        private static ExtendedPostgreColumnType TIMESTAMPTZ = new ExtendedPostgreColumnType(
                "timestamptz", Timestamp.class, false, TemporalType.TIMESTAMP);
        
        private static ExtendedPostgreColumnType BOOL = new ExtendedPostgreColumnType(
                "bool", Boolean.class);
        
        public ExtendedPostgreColumnType(String dataType, Class<?> attributeClass) {
            super(dataType, attributeClass);
        }

        public ExtendedPostgreColumnType(String dataType, Class<?> attributeClass,
                boolean lob, TemporalType temporalType) {
            super(dataType, attributeClass, lob);
            super.temporalType = temporalType;
        }
    }
}
