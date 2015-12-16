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

package jp.co.tis.gsp.tools.dba.dialect;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DialectFactory {
	public static Dialect getDialect(String url) {
		String[] urlTokens = StringUtils.split(url, ':');
		if(urlTokens.length < 3) {
			throw new IllegalArgumentException("url isn't jdbc url format.");
		}
		Dialect dialect;
		try {
            Class<?> dialectClass;
            if (classMap.containsKey(urlTokens[1])) {
                dialectClass = classMap.get(urlTokens[1]);

            } else {
                String dialectClassName;
                dialectClassName = "jp.co.tis.gsp.tools.dba.dialect."
                        +StringUtils.capitalize(urlTokens[1])+"Dialect";
                dialectClass = Class.forName(dialectClassName);
            }
			dialect = (Dialect)dialectClass.newInstance();
			dialect.setUrl(url);
		} catch(Exception e) {
			throw new IllegalArgumentException("Unsupported Database product:" + urlTokens[1], e);
		}

		return dialect;
	}

    private static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
    public static void registerOptionalDialects(Map<String, String> optionalDialectClassNames) {
        if (optionalDialectClassNames == null) {
            return;
        }
        try {
            for (String urlPrefix : optionalDialectClassNames.keySet()) {
                Class<?> dialectClass = Class.forName(optionalDialectClassNames.get(urlPrefix));
                classMap.put(urlPrefix, dialectClass);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't create dialect:" + optionalDialectClassNames, e);
        }
    }
}
