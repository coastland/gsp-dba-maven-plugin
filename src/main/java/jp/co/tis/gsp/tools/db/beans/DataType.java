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

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="TYPETABLE")
public class DataType {
	/** データ型の名前 */
	private String name;
	
	/** データ型の種類 0は長さを持たない*/
	private Integer lenType;

	@XmlAttribute(name="NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name="LEN-TYPE")
	public Integer getLenType() {
		return lenType;
	}

	public void setLenType(Integer lenType) {
		this.lenType = lenType;
	}
	
	public Boolean hasDataLength() {
		return (lenType != 0);
	}
}
