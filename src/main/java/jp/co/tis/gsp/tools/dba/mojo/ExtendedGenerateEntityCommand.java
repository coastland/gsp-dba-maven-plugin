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

import org.seasar.extension.jdbc.gen.internal.command.GenerateEntityCommand;

public class ExtendedGenerateEntityCommand extends GenerateEntityCommand{

    /** エンティティクラスでJSR310を使用する場合{@code true} */
    protected boolean useJSR310 = false;

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
}
