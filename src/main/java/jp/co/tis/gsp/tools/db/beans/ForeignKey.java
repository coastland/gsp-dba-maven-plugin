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
import java.util.List;

public class ForeignKey {
	private String name;

	private List<Column> columnList;
	private Entity referenceEntity;
	private List<Column> referenceColumnList;

	private Relation relation;

	public ForeignKey() {
		columnList = new ArrayList<Column>();
		referenceColumnList = new ArrayList<Column>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Column> getColumnList() {
		return columnList;
	}
	
	public void addColumn(Column column) {
		this.columnList.add(column);
	}

	public void setColumnList(List<Column> columnList) {
		this.columnList = columnList;
	}

	public Entity getReferenceEntity() {
		return referenceEntity;
	}

	public void setReferenceEntity(Entity referenceEntity) {
		this.referenceEntity = referenceEntity;
	}

	public List<Column> getReferenceColumnList() {
		return referenceColumnList;
	}
	
	public void addReferenceColumn(Column column) {
		this.referenceColumnList.add(column);
	}

	public void setReferenceColumnList(List<Column> referenceColumnList) {
		this.referenceColumnList = referenceColumnList;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public Boolean hasName() {
		return false;
	}
}
