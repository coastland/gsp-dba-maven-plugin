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

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="INDEX")
public class Index {
	private Integer id;

	/** 論理名 */
	private String label;

	/** 物理名 */
	private String name;

	/** カラム */
	private List<IndexColumn> indexColumnList;

	/** 正規のカラム*/
	private List<Column> columnList;
	
	/** インデックスの種別 */
	private Integer type;

	/** インデックスの持ち主のエンティティ */
	private Entity entity;

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

	@XmlElementRef(name="COLUMN")
	public List<IndexColumn> getIndexColumnList() {
		return indexColumnList;
	}

	public void setIndexColumnList(List<IndexColumn> indexColumnList) {
		this.indexColumnList = indexColumnList;
	}

	@XmlAttribute(name="I-TYPE")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@XmlTransient
	public List<Column> getColumnList() {
		if(columnList != null) {
			return columnList;
		}
		if(indexColumnList == null) {
			return null;
		}
		columnList = new ArrayList<Column>();
		for(IndexColumn indexColumn : indexColumnList) {
			columnList.add(entity.getColumn(indexColumn.getId()));
		}

		return columnList;
	}

	@XmlTransient
	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Boolean isPrimaryKey() {
		return type == 0;
	}

	public Column getFirstColumn() {
		if (columnList == null)
			return null;
		return columnList.get(0);
	}
}
