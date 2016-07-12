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

package jp.co.tis.gsp.tools.dba.mojo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import jp.co.tis.gsp.tools.dba.util.CsvLoader;

/**
 * load-data.
 * 
 * CSV形式で定義したデータをデータベースに登録する。
 * 
 * @author kawasima
 */
@Mojo(name = "load-data")
public class LoadDataMojo extends AbstractDbaMojo {
    @Parameter(property = "gsp-dba.dataDirectory", required = true)
    protected File dataDirectory;

    /**
     * Filenames which specify a character code are enumerated.
     * <p>
     * ex.)
     * 
     * <pre>
     * &lt;specifiedEncodingFiles&gt;
     *   &lt;aa.csv&gt;UTF-8&lt;/aa.csv&gt;
     *   &lt;bb.csv&gt;UTF-8&lt;/bb.csv&gt;
     * &lt;/specifiedEncodingFiles&gt;
     * </pre>
     * </p>
     */
    @Parameter
    @SuppressWarnings("rawtypes")
    protected Map specifiedEncodingFiles;

    final Charset SJIS = Charset.forName("Windows-31J");

    @Override
    protected void executeMojoSpec() throws MojoExecutionException, MojoFailureException {

        CsvLoader dataLoader = new CsvLoader(url, driver, schema, adminUser, adminPassword, dataDirectory, SJIS,
                specifiedEncodingFiles, onError, getLog());
        try {
            dataLoader.execute();
        } catch (Exception e) {
            throw new MojoExecutionException("CSVデータのロード処理で失敗しました:", e);
        }

    }

}
