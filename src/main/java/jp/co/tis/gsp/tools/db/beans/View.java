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

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="VIEW")
public class View {
	private Integer id;
	
	private String label;
	
	private String name;
	
	private String where;
	
	private String sql;
	
	private Integer showType;
	
	private List<ViewEntity> viewEntityList;
	
    private String schema;
	
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

	@XmlAttribute(name="WHERE")
	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	@XmlAttribute(name="SQL")
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@XmlAttribute(name="SHOWTYPE")
	public Integer getShowType() {
		return showType;
	}

	public void setShowType(Integer showType) {
		this.showType = showType;
	}

	@XmlElementRef(name="ENTITY")
	public List<ViewEntity> getViewEntityList() {
		return viewEntityList;
	}

	public void setViewEntityList(List<ViewEntity> viewEntityList) {
		this.viewEntityList = viewEntityList;
	}

	@XmlElementRef(name="COLUMN")
	public List<ViewColumn> getViewColumnList() {
		return viewColumnList;
	}

	public void setViewColumnList(List<ViewColumn> viewColumnList) {
		this.viewColumnList = viewColumnList;
	}
	  
    @XmlAttribute(name="SCHEMA")
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

	private List<ViewColumn> viewColumnList;
}
