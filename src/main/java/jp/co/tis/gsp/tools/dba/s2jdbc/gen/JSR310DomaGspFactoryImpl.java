/*
 * Copyright (C) 2017 coastland
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

import org.seasar.extension.jdbc.gen.command.Command;
import org.seasar.extension.jdbc.gen.internal.factory.FactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.AssociationModelFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.CompositeUniqueConstraintModelFactoryImpl;
import org.seasar.extension.jdbc.gen.model.EntityModelFactory;
import org.seasar.framework.convention.PersistenceConvention;

public class JSR310DomaGspFactoryImpl extends FactoryImpl {

    @Override
    public EntityModelFactory createEntityModelFactory(Command command, String packageName, Class<?> superclass,
                                                       boolean useTemporalType, boolean useAccessor,
                                                       boolean useComment, boolean showCatalogName,
                                                       boolean showSchemaName, boolean showTableName,
                                                       boolean showColumnName, boolean showColumnDefinition,
                                                       boolean showJoinColumn,
                                                       PersistenceConvention persistenceConvention) {
        return new DomaGspFactoryImpl.DomaEntityModelFactory(packageName, superclass,
                new JSR310AttributeModelFactoryImpl(showColumnName,
                        showColumnDefinition, useTemporalType,
                        persistenceConvention),
                new AssociationModelFactoryImpl(showJoinColumn),
                new CompositeUniqueConstraintModelFactoryImpl(), useAccessor,
                useComment, showCatalogName, showSchemaName, showTableName);
    }
}
