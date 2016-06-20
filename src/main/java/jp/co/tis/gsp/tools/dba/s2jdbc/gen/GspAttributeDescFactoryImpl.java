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

import javax.persistence.GenerationType;

import org.seasar.extension.jdbc.gen.desc.AttributeDesc;
import org.seasar.extension.jdbc.gen.dialect.GenDialect;
import org.seasar.extension.jdbc.gen.internal.desc.AttributeDescFactoryImpl;
import org.seasar.extension.jdbc.gen.meta.DbColumnMeta;
import org.seasar.extension.jdbc.gen.meta.DbForeignKeyMeta;
import org.seasar.extension.jdbc.gen.meta.DbTableMeta;
import org.seasar.framework.convention.PersistenceConvention;

/**
 * @author kawasima
 */
public class GspAttributeDescFactoryImpl extends AttributeDescFactoryImpl {
    public GspAttributeDescFactoryImpl(
            PersistenceConvention persistenceConvention, GenDialect dialect,
            String versionColumnNamePattern, GenerationType generationType,
            Integer initialValue, Integer allocationSize) {
        super(persistenceConvention, dialect, versionColumnNamePattern, generationType, initialValue, allocationSize);
    }

    @Override
    protected void doGenerationType(DbTableMeta tableMeta,
                                    DbColumnMeta columnMeta, AttributeDesc attributeDesc) {
        super.doGenerationType(tableMeta, columnMeta, attributeDesc);

        // PrimaryKeyでも外部キーになっているものは、ID生成しないので対象から外す。
        if (attributeDesc.getGenerationType() != null) {
            for (DbForeignKeyMeta foreignKeyMeta : tableMeta.getForeignKeyMetaList()) {
                if (foreignKeyMeta.getForeignKeyColumnNameList().contains(columnMeta.getName())) {
                    attributeDesc.setGenerationType(null);
                    attributeDesc.setInitialValue(0);
                    attributeDesc.setAllocationSize(0);
                }
            }
        }
    }
}
