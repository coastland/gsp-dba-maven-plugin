package jp.co.tis.gsp.tools.dba.s2jdbc.gen;

import org.seasar.extension.jdbc.gen.desc.AttributeDesc;
import org.seasar.extension.jdbc.gen.internal.model.AttributeModelFactoryImpl;
import org.seasar.extension.jdbc.gen.model.AttributeModel;
import org.seasar.framework.convention.PersistenceConvention;

import javax.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author MAENO Daisuke
 */
public class JSR310AttributeModelFactoryImpl extends AttributeModelFactoryImpl {

    public JSR310AttributeModelFactoryImpl(boolean showColumnName, boolean showColumnDefinition, boolean useTemporalType, PersistenceConvention persistenceConvention) {
        super(showColumnName, showColumnDefinition, useTemporalType, persistenceConvention);
    }

    @Override
    protected void doAttributeClass(AttributeModel attributeModel, AttributeDesc attributeDesc) {
        final Class attributeClass = attributeDesc.getAttributeClass();
        final TemporalType primaryTemporalType = attributeDesc.getPrimaryTemporalType();
        if (primaryTemporalType != null) {
            attributeModel.setTemporalType(primaryTemporalType);
        } else if (useTemporalType && attributeDesc.isTemporal()) {
            attributeModel.setTemporalType(attributeDesc.getTemporalType());
            attributeModel.setAttributeClass(LocalDate.class);
        }

        if (attributeClass == java.sql.Date.class) {
            attributeModel.setAttributeClass(LocalDate.class);
        } else if (attributeClass == java.sql.Time.class) {
            attributeModel.setAttributeClass(LocalTime.class);
        } else if (attributeClass == java.sql.Timestamp.class) {
            attributeModel.setAttributeClass(LocalDateTime.class);
        } else {
            attributeModel.setAttributeClass(attributeDesc.getAttributeClass());
        }
    }
}
