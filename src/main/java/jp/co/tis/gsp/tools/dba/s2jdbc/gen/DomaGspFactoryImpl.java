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

import java.io.Serializable;

import javax.annotation.Generated;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.SequenceGenerator;
import org.seasar.doma.Table;
import org.seasar.doma.TableGenerator;
import org.seasar.doma.Transient;
import org.seasar.doma.Version;
import org.seasar.extension.jdbc.gen.command.Command;
import org.seasar.extension.jdbc.gen.desc.EntityDesc;
import org.seasar.extension.jdbc.gen.internal.factory.FactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.AssociationModelFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.AttributeModelFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.CompositeUniqueConstraintModelFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl;
import org.seasar.extension.jdbc.gen.model.AssociationModelFactory;
import org.seasar.extension.jdbc.gen.model.AttributeModel;
import org.seasar.extension.jdbc.gen.model.AttributeModelFactory;
import org.seasar.extension.jdbc.gen.model.CompositeUniqueConstraintModelFactory;
import org.seasar.extension.jdbc.gen.model.EntityModel;
import org.seasar.extension.jdbc.gen.model.EntityModelFactory;
import org.seasar.framework.convention.PersistenceConvention;

public class DomaGspFactoryImpl extends FactoryImpl {

    @Override
    public EntityModelFactory createEntityModelFactory(final Command command, final String packageName,
            final Class<?> superclass,
            final boolean useTemporalType, final boolean useAccessor, final boolean useComment,
            final boolean showCatalogName,
            final boolean showSchemaName, final boolean showTableName, final boolean showColumnName,
            final boolean showColumnDefinition,
            final boolean showJoinColumn, final PersistenceConvention persistenceConvention) {

        return new DomaEntityModelFactory(packageName, superclass,
                new AttributeModelFactoryImpl(showColumnName,
                        showColumnDefinition, useTemporalType,
                        persistenceConvention),
                new AssociationModelFactoryImpl(showJoinColumn),
                new CompositeUniqueConstraintModelFactoryImpl(), useAccessor,
                useComment, showCatalogName, showSchemaName, showTableName);
    }

    private static class DomaEntityModelFactory extends EntityModelFactoryImpl {

        public DomaEntityModelFactory(final String packageName, final Class<?> superclass,
                final AttributeModelFactory attributeModelFactory,
                final AssociationModelFactory associationModelFactory,
                final CompositeUniqueConstraintModelFactory compositeUniqueConstraintModelFactory,
                final boolean useAccessor, final boolean useComment, final boolean showCatalogName,
                final boolean showSchemaName,
                final boolean showTableName) {
            super(packageName, superclass, attributeModelFactory, associationModelFactory,
                    compositeUniqueConstraintModelFactory, useAccessor, useComment, showCatalogName, showSchemaName,
                    showTableName);
        }

        @Override
        protected void doImportName(final EntityModel model, final EntityDesc entityDesc) {
            classModelSupport.addImportName(model, Entity.class);
            classModelSupport.addImportName(model, Serializable.class);
            classModelSupport.addImportName(model, Generated.class);
            if (model.getCatalogName() != null || model.getSchemaName() != null
                    || model.getTableName() != null) {
                classModelSupport.addImportName(model, Table.class);
            }
            if (superclass != null) {
                classModelSupport.addImportName(model, superclass);
            }
            for (AttributeModel attr : model.getAttributeModelList()) {
                if (attr.isId()) {
                    classModelSupport.addImportName(model, Id.class);
                    if (attr.getGenerationType() != null) {
                        classModelSupport
                                .addImportName(model, GeneratedValue.class);
                        classModelSupport
                                .addImportName(model, GenerationType.class);
                        if (attr.getGenerationType() == javax.persistence.GenerationType.SEQUENCE) {
                            classModelSupport.addImportName(model,
                                    SequenceGenerator.class);
                        } else if (attr.getGenerationType() == javax.persistence.GenerationType.TABLE) {
                            classModelSupport.addImportName(model,
                                    TableGenerator.class);
                        }
                    }
                }
                if (attr.isTransient()) {
                    classModelSupport.addImportName(model, Transient.class);
                } else {
                    classModelSupport.addImportName(model, Column.class);
                }
                if (attr.isVersion()) {
                    classModelSupport.addImportName(model, Version.class);
                }
                classModelSupport.addImportName(model, attr.getAttributeClass());
            }
        }
    }
}
