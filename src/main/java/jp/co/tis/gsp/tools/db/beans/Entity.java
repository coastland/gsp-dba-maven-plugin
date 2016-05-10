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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ENTITY")
public class Entity {
	private Integer id;

	private String label;

	private String name;

	private List<Column> columnList;

	private List<Index> indexList;

	private List<ForeignKey> foreignKeyList;

	private Integer showType;

	private Erd erd;
	
	private boolean havePrimaryKey = false;
	
	private String schema;
	
	
	@XmlAttribute(name="SCHEMA")
	public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

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

	@XmlAttribute(name="SHOWTYPE")
	public Integer getShowType() {
		return showType;
	}

	public void setShowType(Integer showType) {
		this.showType = showType;
	}

	@XmlElementRef(name="ATTR")
	public List<Column> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<Column> columnList) {
		this.columnList = columnList;
	}

	@XmlElementRef(name="INDEX")
	public List<Index> getIndexList() {
		return indexList;
	}

	public void setIndexList(List<Index> indexList) {
		this.indexList = indexList;
	}

	public void setErd(Erd erd) {
		this.erd = erd;
	}
	

	public Column getColumn(Integer id) {
		for (Column column : columnList) {
			if ((int)column.getId() == (int)id) {
				return column;
			}
		}
		return null;
	}

	public List<ForeignKey> getForeignKeyList() {
		return foreignKeyList;
	}

	public void init() {
		Map<Integer, ForeignKey> foreignKeyMap = new HashMap<Integer, ForeignKey>();
		foreignKeyList = new ArrayList<ForeignKey>();
		if (columnList == null) return;
		for(Column column : columnList) {
            column.setEntity(this);
            if (column.isPrimaryKey()) {
				havePrimaryKey = true;
			}
			if (!column.hasForeignKeyColumn())
				continue;
			
			List<ForeignKeyColumn> fkColumnList = column.getForeignKeyColumnList();
			for(ForeignKeyColumn fkColumn : fkColumnList){
				Relation relation = erd.getRelation(fkColumn.getRelationId());
				if (relation.getShowType() != 0) {
					continue;
				}

				Entity parentEntity = erd.getEntity(relation.getParentEntityId());

				ForeignKey foreignKey;
				if (foreignKeyMap.containsKey(relation.getId())) {
					foreignKey = foreignKeyMap.get(relation.getId());
				} else {
					foreignKey = new ForeignKey();
					foreignKey.setReferenceEntity(parentEntity);
					foreignKeyMap.put(relation.getId(), foreignKey);
				}
				foreignKey.addColumn(column);
				foreignKey.addReferenceColumn(parentEntity.getColumn(fkColumn.getColumnId()));
			}
		}

		foreignKeyList.addAll(foreignKeyMap.values());

        for(Index index : indexList) {
            index.setEntity(this);
        }

    }

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Entity))
			return false;
		Entity entity = (Entity)obj;
		return this.id.equals(entity.getId());
	}
	
	public boolean havePrimaryKey() {
		return this.havePrimaryKey;
	}
}
