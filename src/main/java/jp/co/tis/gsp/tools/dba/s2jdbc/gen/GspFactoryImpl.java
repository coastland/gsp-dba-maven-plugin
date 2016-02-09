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

import java.io.File;

import javax.persistence.GenerationType;
import javax.sql.DataSource;

import org.seasar.extension.jdbc.gen.command.Command;
import org.seasar.extension.jdbc.gen.desc.AttributeDescFactory;
import org.seasar.extension.jdbc.gen.desc.CompositeUniqueConstraintDescFactory;
import org.seasar.extension.jdbc.gen.desc.EntityDescFactory;
import org.seasar.extension.jdbc.gen.desc.EntitySetDescFactory;
import org.seasar.extension.jdbc.gen.dialect.GenDialect;
import org.seasar.extension.jdbc.gen.internal.desc.CompositeUniqueConstraintDescFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.desc.EntityDescFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.desc.EntitySetDescFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.factory.FactoryImpl;
import org.seasar.extension.jdbc.gen.meta.DbTableMetaReader;
import org.seasar.framework.convention.PersistenceConvention;

public class GspFactoryImpl extends FactoryImpl {
	@Override
    public DbTableMetaReader createDbTableMetaReader(Command command,
            DataSource dataSource, GenDialect dialect, String schemaName,
            String tableNamePattern, String ignoreTableNamePattern,
            boolean readComment) {

        return new DbTableMetaReaderWithView(dataSource, dialect, schemaName,
                tableNamePattern, ignoreTableNamePattern, readComment);
    }

    @Override
    public EntitySetDescFactory createEntitySetDescFactory(Command command,
                                                           DbTableMetaReader dbTableMetaReader,
                                                           PersistenceConvention persistenceConvention, GenDialect dialect,
                                                           String versionColumnNamePattern, File pluralFormFile,
                                                           GenerationType generationType, Integer initialValue,
                                                           Integer allocationSize) {

        return new EntitySetDescFactoryImpl(dbTableMetaReader,
                persistenceConvention, dialect, versionColumnNamePattern,
                pluralFormFile, generationType, initialValue, allocationSize) {
            @Override
            protected EntityDescFactory createEntityDescFactory() {
                AttributeDescFactory attributeDescFactory = new GspAttributeDescFactoryImpl(
                        persistenceConvention, dialect, versionColumnNamePattern,
                        generationType, initialValue, allocationSize);
                CompositeUniqueConstraintDescFactory compositeUniqueConstraintDescFactory = new CompositeUniqueConstraintDescFactoryImpl();
                return new EntityDescFactoryImpl(persistenceConvention,
                        attributeDescFactory, compositeUniqueConstraintDescFactory);
            }

        };
    }
}
