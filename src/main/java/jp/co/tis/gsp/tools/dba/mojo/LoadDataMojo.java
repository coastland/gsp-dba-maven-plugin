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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import jp.co.tis.gsp.tools.db.EntityDependencyParser;
import jp.co.tis.gsp.tools.dba.CsvInsertHandler;
import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.dialect.DialectFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.FileInputStreamUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;

import com.csvreader.CsvReader;

/**
 * @author kawasima
 */
@Mojo(name = "load-data")
public class LoadDataMojo extends AbstractDbaMojo {
    @Parameter(property="gsp-dba.dataDirectory", required = true)
	protected File dataDirectory;

	/**
	 * Filenames which specify a character code are enumerated.
	 * <p>ex.)
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
		List<File> files = CollectionsUtil.newArrayList(FileUtils.listFiles(
			dataDirectory,
			new String[]{"csv"},
			true));
		DriverManagerUtil.registerDriver(driver);
		Connection conn = null;
		try {
		 conn = DriverManager.getConnection(url, user, password);
		 conn.setAutoCommit(false);
		} catch (SQLException e) {
			getLog().error("DBに接続できませんでした。driverおよびurlの設定が正しいか確認してください");
			throw new MojoExecutionException("", e);
		}

		// 依存関係を考慮し読み込むファイル順をソートする
		EntityDependencyParser parser = new EntityDependencyParser();
		Dialect dialect = DialectFactory.getDialect(url, driver);
		parser.parse(conn, url, schema);
		final List<String> tableList = parser.getTableList();
		Collections.sort(files, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				return getIndex(FilenameUtils.getBaseName(f1.getName()))
						- getIndex(FilenameUtils.getBaseName(f2.getName()));
			}

			private int getIndex(String tableName) {
				for(int i=0; i<tableList.size(); i++) {
					if(StringUtil.equalsIgnoreCase(tableName, tableList.get(i))) {
						return i;
					}
				}
				return 0;
			}

		});
		try {
			for (File file : files) {
				CsvReader reader = null;
				String fileName = file.getName();
				FileInputStream in = FileInputStreamUtil.create(file);
				try {
					getLog().info("取込を開始します:" + fileName);
					if (specifiedEncodingFiles != null && specifiedEncodingFiles.containsKey(fileName)) {
						String encoding = (String)specifiedEncodingFiles.get(fileName);
						reader = new CsvReader(in, Charset.forName(encoding));
						getLog().info(" -- encoding:" + encoding);
					} else {
						reader = new CsvReader(in, SJIS);
					}
					String tableName = StringUtils.upperCase(FilenameUtils.getBaseName(fileName));
					reader.readHeaders();
					String[] headers = reader.getHeaders();
					CsvInsertHandler insertHandler = new CsvInsertHandler(conn, dialect, dialect.normalizeSchemaName(schema), tableName, headers);
					insertHandler.prepare();

					while(reader.readRecord()) {
						String[] values = reader.getValues();
						
						for(int i=0; i<values.length; i++) {
							insertHandler.setObject(i+1, values[i]);
						}
						insertHandler.execute();
					}

					insertHandler.close();
				} catch (IOException e) {
					getLog().warn("ファイルがオープンできません:"+file, e);
				} catch (SQLException e) {
					getLog().warn("SQL実行中にエラーが発生しました:"+file, e);
				} finally {
					if (reader != null)
						reader.close();
				}
			}
		} finally {
			ConnectionUtil.close(conn);
		}
	}

}
