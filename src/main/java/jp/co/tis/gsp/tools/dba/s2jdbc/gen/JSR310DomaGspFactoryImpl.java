package jp.co.tis.gsp.tools.dba.s2jdbc.gen;

import org.seasar.extension.jdbc.gen.command.Command;
import org.seasar.extension.jdbc.gen.internal.factory.FactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.AssociationModelFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.model.CompositeUniqueConstraintModelFactoryImpl;
import org.seasar.extension.jdbc.gen.model.EntityModelFactory;
import org.seasar.framework.convention.PersistenceConvention;

/**
 * @author MAENO Daisuke
 */
public class JSR310DomaGspFactoryImpl extends FactoryImpl {

    @Override
    public EntityModelFactory createEntityModelFactory(Command command, String packageName, Class<?> superclass, boolean useTemporalType, boolean useAccessor, boolean useComment, boolean showCatalogName, boolean showSchemaName, boolean showTableName, boolean showColumnName, boolean showColumnDefinition, boolean showJoinColumn, PersistenceConvention persistenceConvention) {
        return new DomaGspFactoryImpl.DomaEntityModelFactory(packageName, superclass,
                new JSR310AttributeModelFactoryImpl(showColumnName,
                        showColumnDefinition, useTemporalType,
                        persistenceConvention),
                new AssociationModelFactoryImpl(showJoinColumn),
                new CompositeUniqueConstraintModelFactoryImpl(), useAccessor,
                useComment, showCatalogName, showSchemaName, showTableName);
    }
}
