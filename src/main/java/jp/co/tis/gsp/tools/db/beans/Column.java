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

package jp.co.tis.gsp.tools.db.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name="ATTR")
public class Column {
	private Integer id;

	private String label;

	private String name;

	private String dataType;

	private Integer length;

	private Integer scale;

	private String defaultValue;

	private Short pkFlg;

	private Boolean isArray = false;

    private Entity entity;

	public Boolean isPrimaryKey() {
		return pkFlg == 1;
	}

    private Boolean isForeignKey() {
        for (ForeignKey foreignKey: entity.getForeignKeyList()) {
            if (foreignKey.getColumnList().contains(this)) {
                return true;
            }
        }
        return false;
    }

    public Boolean isSingularPrimaryKey() {
        if (!isPrimaryKey() || isForeignKey())
            return false;
        for (Index index : entity.getIndexList()) {
            if (index.getColumnList().contains(this)
                    && index.getColumnList().size() == 1
                    && StringUtils.endsWithIgnoreCase(getName(), "_id"))
                return true;
        }
        return false;
    }

    public Boolean isAutoIncrement() {
        return StringUtils.equalsIgnoreCase(defaultValue, "AUTO_INCREMENT")
                || isSingularPrimaryKey();

    }

    public String getGeneratorKeyName() {
        if (isSingularPrimaryKey()) {
            return name + "_SEQ";
        } else {
            return name + "_USEQ";
        }
    }
	public Boolean isArray() {
		return isArray;
	}

	private Short required;

	public Boolean isNullable() {
		return required != 1;
	}

	private ForeignKeyColumn foreignKeyColumn;

	@XmlAttribute(name="ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlAttribute(name="L-NAME")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@XmlAttribute(name="P-NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name="DATATYPE")
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		if (StringUtils.equalsIgnoreCase(dataType, "ARRAY")
				|| StringUtils.endsWithIgnoreCase(dataType, "_ARRAY")) {
			dataType = StringUtils.replace(dataType, "_ARRAY", "");
			if (StringUtils.isEmpty(dataType)) {
				dataType = "VARCHAR";
			}
			isArray = true;
		}
		this.dataType = dataType;
	}

	@XmlAttribute(name="LENGTH")
	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	@XmlAttribute(name="SCALE")
	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	@XmlAttribute(name="PK")
	public Short getPkFlg() {
		return pkFlg;
	}

	public void setPkFlg(Short pkFlg) {
		this.pkFlg = pkFlg;
	}

	@XmlAttribute(name="DEF")
	public String getDefaultValue() {
        if (StringUtils.equalsIgnoreCase(defaultValue, "AUTO_INCREMENT")) {
            return null;
        } else {
            return defaultValue;
        }
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@XmlAttribute(name="NULL")
	public Short getRequired() {
		return required;
	}

	public void setRequired(Short required) {
		this.required = required;
	}

	@XmlElementRef(name="FK")
	public ForeignKeyColumn getForeignKeyColumn() {
		return foreignKeyColumn;
	}

	public void setForeignKeyColumn(ForeignKeyColumn foreignKeyColumn) {
		this.foreignKeyColumn = foreignKeyColumn;
	}

	public boolean hasForeignKeyColumn() {
		return foreignKeyColumn != null;
	}

    @XmlTransient
    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
