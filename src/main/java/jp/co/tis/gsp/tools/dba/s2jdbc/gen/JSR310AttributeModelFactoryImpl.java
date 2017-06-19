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

import org.seasar.extension.jdbc.gen.desc.AttributeDesc;
import org.seasar.extension.jdbc.gen.internal.model.AttributeModelFactoryImpl;
import org.seasar.extension.jdbc.gen.model.AttributeModel;
import org.seasar.framework.convention.PersistenceConvention;

import javax.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class JSR310AttributeModelFactoryImpl extends AttributeModelFactoryImpl {

    public JSR310AttributeModelFactoryImpl(boolean showColumnName, boolean showColumnDefinition,
                                           boolean useTemporalType, PersistenceConvention persistenceConvention) {
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
