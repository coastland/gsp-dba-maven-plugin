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

package jp.co.tis.gsp.tools.dba.util;

import jp.co.tis.gsp.tools.dba.dialect.Dialect;

/**
 * @author kawasima
 */
public class DialectUtil {
    static ThreadLocal<Dialect> threadDialect= new ThreadLocal<Dialect>();

    public static Dialect getDialect() {
        return threadDialect.get();
    }

    public static void setDialect(Dialect dialect) {
        threadDialect.set(dialect);
    }
}
