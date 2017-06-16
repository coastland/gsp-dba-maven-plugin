package jp.co.tis.gsp.tools.dba.s2jdbc.gen;

import org.seasar.extension.jdbc.gen.command.Command;
import org.seasar.extension.jdbc.gen.internal.model.AssociationModelFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.CompositeUniqueConstraintModelFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl;
import org.seasar.extension.jdbc.gen.model.AssociationModelFactory;
import org.seasar.extension.jdbc.gen.model.AttributeModelFactory;
import org.seasar.extension.jdbc.gen.model.CompositeUniqueConstraintModelFactory;
import org.seasar.extension.jdbc.gen.model.EntityModelFactory;
import org.seasar.framework.convention.PersistenceConvention;

/**
 * @author MAENO Daisuke
 */
public class JSR310GspFactoryImpl extends GspFactoryImpl {

    @Override
    public EntityModelFactory createEntityModelFactory(final Command command, final String packageName,
                                                       final Class<?> superclass, final boolean useTemporalType,
                                                       final boolean useAccessor, final boolean useComment,
                                                       final boolean showCatalogName, final boolean showSchemaName,
                                                       final boolean showTableName, final boolean showColumnName,
                                                       final boolean showColumnDefinition, final boolean showJoinColumn,
                                                       final PersistenceConvention persistenceConvention) {
        return new JSR310GspEntityModelFactory(packageName, superclass,
                new JSR310AttributeModelFactoryImpl(showColumnName,
                        showColumnDefinition, useTemporalType,
                        persistenceConvention),
                new AssociationModelFactoryImpl(showJoinColumn),
                new CompositeUniqueConstraintModelFactoryImpl(), useAccessor,
                useComment, showCatalogName, showSchemaName, showTableName);
    }

    private static class JSR310GspEntityModelFactory extends EntityModelFactoryImpl {

        public JSR310GspEntityModelFactory(final String packageName, final Class<?> superclass,
                                           final AttributeModelFactory attributeModelFactory,
                                           final AssociationModelFactory associationModelFactory,
                                           final CompositeUniqueConstraintModelFactory compositeUniqueConstraintModelFactory,
                                           final boolean useAccessor, final boolean useComment,
                                           final boolean showCatalogName, final boolean showSchemaName,
                                           final boolean showTableName) {
            super(packageName, superclass, attributeModelFactory, associationModelFactory, compositeUniqueConstraintModelFactory, useAccessor, useComment, showCatalogName, showSchemaName, showTableName);
        }
    }
}
