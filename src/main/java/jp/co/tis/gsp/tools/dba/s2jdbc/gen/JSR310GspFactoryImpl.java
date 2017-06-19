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
import org.seasar.extension.jdbc.gen.internal.model.AssociationModelFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.CompositeUniqueConstraintModelFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl;
import org.seasar.extension.jdbc.gen.model.EntityModelFactory;
import org.seasar.framework.convention.PersistenceConvention;

public class JSR310GspFactoryImpl extends GspFactoryImpl {

    @Override
    public EntityModelFactory createEntityModelFactory(final Command command, final String packageName,
                                                       final Class<?> superclass, final boolean useTemporalType,
                                                       final boolean useAccessor, final boolean useComment,
                                                       final boolean showCatalogName, final boolean showSchemaName,
                                                       final boolean showTableName, final boolean showColumnName,
                                                       final boolean showColumnDefinition, final boolean showJoinColumn,
                                                       final PersistenceConvention persistenceConvention) {
        return new EntityModelFactoryImpl(packageName, superclass,
                new JSR310AttributeModelFactoryImpl(showColumnName,
                        showColumnDefinition, useTemporalType,
                        persistenceConvention),
                new AssociationModelFactoryImpl(showJoinColumn),
                new CompositeUniqueConstraintModelFactoryImpl(), useAccessor,
                useComment, showCatalogName, showSchemaName, showTableName);
    }
}
