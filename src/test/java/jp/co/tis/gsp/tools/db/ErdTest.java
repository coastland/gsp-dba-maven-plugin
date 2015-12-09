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

import jp.co.tis.gsp.tools.db.beans.Column;
import jp.co.tis.gsp.tools.db.beans.Entity;
import jp.co.tis.gsp.tools.db.beans.Erd;
import jp.co.tis.gsp.tools.db.beans.ForeignKey;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * @author kawasima
 */
public class ErdTest {
    @Test
    public void test() throws Exception {
        File erdFile = new File(Thread.currentThread().getContextClassLoader()
                .getResource("test.edm")
                .toURI());

        JAXBContext context = JAXBContext.newInstance(Erd.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Erd erd = (Erd)unmarshaller.unmarshal(erdFile);
        erd.init();

        for (Entity entity : erd.entityList) {
            System.out.println(entity.getName());
            for (ForeignKey foreignKey : entity.getForeignKeyList()) {
                for (Column column : foreignKey.getColumnList()) {
                    System.out.println(column.getName() + ":" + column.isSingularPrimaryKey());

                }
            }
        }
    }
}
