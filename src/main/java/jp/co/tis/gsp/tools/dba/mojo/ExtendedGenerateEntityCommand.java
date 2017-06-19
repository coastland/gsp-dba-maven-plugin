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

package jp.co.tis.gsp.tools.dba.mojo;

import jp.co.tis.gsp.tools.dba.s2jdbc.gen.DomaGspFactoryImpl;
import jp.co.tis.gsp.tools.dba.s2jdbc.gen.GspFactoryImpl;
import jp.co.tis.gsp.tools.dba.s2jdbc.gen.JSR310DomaGspFactoryImpl;
import jp.co.tis.gsp.tools.dba.s2jdbc.gen.JSR310GspFactoryImpl;
import org.seasar.extension.jdbc.gen.internal.command.GenerateEntityCommand;
import org.seasar.extension.jdbc.gen.internal.factory.Factory;
import org.seasar.extension.jdbc.gen.internal.util.ReflectUtil;

public class ExtendedGenerateEntityCommand extends GenerateEntityCommand{

    /** domaを表す定数 */
    private static final String ENTITY_TYPE_DOMA = "doma";

    /** エンティティクラスでJSR310を使用する場合{@code true} */
    protected boolean useJSR310 = false;

    /** エンティティタイプ */
    protected String entityType = null;

    /**
     * エンティティクラスでJSR310を使用する場合{@code true}を返します。
     *
     * @return エンティティクラスでJSR310を使用する場合{@code true}
     */
    public boolean isUseJSR310() {
        return useJSR310;
    }

    /**
     * エンティティクラスでJSR310を使用する場合{@code true}を設定します。
     *
     * @param useJSR310
     *            エンティティクラスでJSR310を使用する場合{@code true}
     */
    public void setUseJSR310(boolean useJSR310) {
        this.useJSR310 = useJSR310;
    }

    /**
     * エンティティクラスのタイプを返します。
     *
     * @return エンティティクラスのタイプ
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * エンティティクラスのタイプを設定します。
     *
     * @param entityType
     *            エンティティクラスのタイプ
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * <code>useJSR310</code>, <code>entityType</code>より、Entityクラスを生成するFactoryを決定します。
     */
    private void createEntityType() {
        if (ENTITY_TYPE_DOMA.equals(this.entityType)) {
            this.setFactoryClassName(
                    (this.useJSR310)
                            ? JSR310DomaGspFactoryImpl.class.getName()
                            : DomaGspFactoryImpl.class.getName());
        } else {
            this.setFactoryClassName(
                    (this.useJSR310)
                            ? JSR310GspFactoryImpl.class.getName()
                            : GspFactoryImpl.class.getName());
        }
    }

    @Override
    protected void doInit() {
        createEntityType();
        // doInitが実行される前、AbstractCommand#init()でFactoryのインスタンスが生成されてしまうので、ここで上書きする
        this.factory = ReflectUtil.newInstance(Factory.class, this.factoryClassName);
        super.doInit();
    }
}
