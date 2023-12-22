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

import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name="ERD")
public class Erd {
	@XmlElementRef(name="ENTITY")
	public List<Entity> entityList;

	@XmlElementRef(name="MODELVIEW")
	public List<ModelView> modelViewList;

	private List<Relation> relationList;

	private List<DataType> dataTypeList;

	private List<View> viewList;

	private String schema;


	@XmlElementRef(name="RELATION")
	public List<Relation> getRelationList() {
		return relationList;
	}

	public void setRelationList(List<Relation> relationList) {
		this.relationList = relationList;
	}

	@XmlElementRef(name="TYPETABLE")
	public List<DataType> getDataTypeList() {
		return dataTypeList;
	}

	public void setDataTypeList(List<DataType> dataTypeList) {
		this.dataTypeList = dataTypeList;
	}

	public DataType getDataType(String name) {
		for(DataType dataType : dataTypeList) {
			if (StringUtils.equals(dataType.getName(), name))
				return dataType;
		}
		return null;
	}

	public Relation getRelation(Integer relationId) {
		for(Relation relation : relationList) {
			if ((int)relation.getId() == (int)relationId) {
				return relation;
			}
		}

		return null;
	}

	public Entity getEntity(Integer entityId) {
		for(Entity entity : entityList) {
			if ((int)entity.getId() == (int)entityId) {
				return entity;
			}
		}
		return null;
	}

	@XmlElementRef(name="VIEW")
	public List<View> getViewList() {
		return viewList;
	}

	public void setViewList(List<View> viewList) {
		this.viewList = viewList;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public View getView(Integer viewId) {
		for(View view : viewList) {
			if ((int)view.getId() == (int)viewId) {
				return view;
			}
		}
		return null;
	}

	public void init() {
		for(Entity entity : entityList) {
			entity.setErd(this);
			entity.init();
		}

		for(ModelView modelView : modelViewList) {
			// MODELVIEWはENTITYのIDだけしかないので本物の参照させる
			List<Entity> modelViewEntityList = modelView.getEntityList();
			if (modelViewEntityList != null) {
				for(int i=0; i<modelViewEntityList.size(); i++) {
					Entity genuineEntity = getEntity(modelViewEntityList.get(i).getId());
					modelViewEntityList.set(i, genuineEntity);
				}
			}
			// MODELVIEWはVIEWのIDだけしかないので本物の参照させる
			List<View> modelViewViewList = modelView.getViewList();
			if(modelViewViewList != null) {
				for(int i=0; i<modelViewViewList.size(); i++) {
					View genuineView = getView(modelViewViewList.get(i).getId());
					modelViewViewList.set(i, genuineView);
				}
			}
		}

	}

	public ModelView getModelView(String schema) {
		for(ModelView modelView : modelViewList) {
			if (StringUtils.equals(modelView.getLabel(), schema)) {
				return modelView;
			}
		}
		return null;
	}
}
