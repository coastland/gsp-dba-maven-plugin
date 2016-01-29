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

package jp.co.tis.gsp.tools.db;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import jp.co.tis.gsp.tools.db.beans.Column;
import jp.co.tis.gsp.tools.db.beans.Entity;
import jp.co.tis.gsp.tools.db.beans.Erd;
import jp.co.tis.gsp.tools.db.beans.ForeignKey;
import jp.co.tis.gsp.tools.db.beans.Index;
import jp.co.tis.gsp.tools.db.beans.ModelView;
import jp.co.tis.gsp.tools.db.beans.View;

import org.apache.commons.lang.StringUtils;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ObjectBrowserErParser extends AbstractDbObjectParser {
    protected boolean printTable = true;
    protected boolean printIndex = true;
    protected boolean printForeignKey = true;
    protected boolean printView  = true;
    protected LengthSemantics lengthSemantics = LengthSemantics.BYTE;

	public void parse(File erdFile) throws JAXBException, IOException, TemplateException {
		JAXBContext context = JAXBContext.newInstance(Erd.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Erd erd = (Erd)unmarshaller.unmarshal(erdFile);
		erd.init();
		setupTemplateLoader();

		Template template      = getTemplate("createTable.ftl");
		Template indexTemplate = getTemplate("createIndex.ftl");
		Template fkTemplate    = getTemplate("addForeignKey.ftl");
		Template viewTemplate  = getTemplate("createView.ftl");

		Map<String, Object> dto = new HashMap<String, Object>();
		erd.setSchema(schema);
		dto.put("erd", erd);
		List<Entity> entityList;
		List<View>   viewList;
        ModelView modelView = erd.getModelView(schema);
		if (modelView == null) {
			entityList = erd.entityList;
			viewList   = erd.getViewList();
		} else {
			entityList = modelView.getEntityList();
			viewList   = modelView.getViewList();
		}

		for(Entity entity : entityList) {
		    
		    //スキーマ != ユーザの場合、スキーマ名をセットする
		    if(!user.equals(schema)){
		      //ftlで.(ピリオド)が常に表示されるため、ここで設定しておく
		        entity.setSchema(schema + ".");
		    }
		    
		    
			// SHOWTYPEが0以外は論理のみ項目
			if (entity.getShowType() != 0) {
				continue;
			}

			// データベース依存のものがあるので型変換する
            for (Column column : entity.getColumnList()) {
                if (typeMapper != null) {
					typeMapper.convert(column);
				}
			}
            dto.put("entity", entity);
            dto.put("LengthSemantics", BeansWrapper
                    .getDefaultInstance()
                    .getEnumModels()
                    .get(LengthSemantics.class.getName()));
            dto.put("lengthSemantics", lengthSemantics);
            if (printTable)
                template.process(dto, getWriter("10_CREATE_"+entity.getName()));

            // index
			int i=0;
			for(Index index : entity.getIndexList()) {
				// 主キーなしのテーブルは、カラムリストがNULLになっている
				if (!printIndex || index.getColumnList() == null) {
					continue;
				}
				if(StringUtils.isBlank(index.getName())) {
					index.setName("IDX_"+entity.getName() + String.format("%02d", ++i));
				}
				Map<String, Object>indexDto = new HashMap<String, Object>(dto);
				indexDto.put("index", index);
				indexTemplate.process(indexDto, getWriter("20_CREATE_"+index.getName()));
			}

			// Foreign Key
			i=0;
			for(ForeignKey foreignKey : entity.getForeignKeyList()) {
				// 相手先が論理のみ項目の場合と、相手がEntityListに含まれていない場合は
				// FKを生成しない。
				if (!printForeignKey
                        || foreignKey.getReferenceEntity().getShowType() != 0
						|| !contains(entityList, foreignKey.getReferenceEntity())) {
					continue;
				}
				if(StringUtils.isBlank(foreignKey.getName())) {
					foreignKey.setName("FK_"+entity.getName() + String.format("%02d", ++i));
				}
				Map<String, Object> fkDto =  new HashMap<String, Object>(dto);
				fkDto.put("foreignKey", foreignKey);
				fkTemplate.process(fkDto, getWriter("30_CREATE_"+foreignKey.getName()));
			}
		}
		// View
		if(viewList != null) {
			for(View view : viewList) {
			    
	            //スキーマ != ユーザの場合、スキーマ名をセットする
	            if(!user.equals(schema)){
	                //ftlで.(ピリオド)が常に表示されるため、ここで設定しておく
	                view.setSchema(schema + ".");
	            }
			    
				if (!printView || view.getShowType() != 0) {
					continue;
				}
				dto.put("view", view);
				viewTemplate.process(dto, getWriter("40_CREATE_"+view.getName()));
			}
		}
	}

	private boolean contains(Collection<?> collection, Object target) {
		if (target == null)
			return false;

		for(Object obj : collection) {
			if (target.equals(obj)) {
				return true;
			}
		}
		return false;
	}

    public void setPrintTable(boolean printTable) {
        this.printTable = printTable;
    }

    public void setPrintIndex(boolean printIndex) {
        this.printIndex = printIndex;
    }

    public void setPrintForeignKey(boolean printForeignKey) {
        this.printForeignKey = printForeignKey;
    }

    public void setPrintView(boolean printView) {
        this.printView = printView;
    }

    public void setLengthSemantics(LengthSemantics lengthSemantics) {
        this.lengthSemantics = lengthSemantics;
    }
}
