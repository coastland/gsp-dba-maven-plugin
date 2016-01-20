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

import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.dialect.DialectFactory;
import jp.co.tis.gsp.tools.dba.util.SqlSplitter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.StatementUtil;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author kawasima
 */
@Mojo(name = "execute-ddl")
public class ExecuteDdlMojo extends AbstractDbaMojo {
	/**
     * DDL directory.
	 */
    @Parameter(defaultValue = "target/ddl")
	protected File ddlDirectory;

    @Parameter
    protected File extraDdlDirectory;

    protected String delimiter = ";";
    private Connection conn;

    private int successfulStatements = 0;
    private int totalStatements = 0;

	@Override
	protected void executeMojoSpec() throws MojoExecutionException, MojoFailureException {
        DriverManagerUtil.registerDriver(driver);
		Dialect dialect = DialectFactory.getDialect(url);
		dialect.dropAll(user, password, adminUser, adminPassword, schema);
		dialect.createUser(user, password, adminUser, adminPassword);

        FilenameFilter sqlFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql");
            }};

		// 拡張子.sqlが実行対象
		List<File> files = new ArrayList<File>(Arrays.asList(ddlDirectory.listFiles(sqlFileFilter)));
        if (extraDdlDirectory != null && extraDdlDirectory.isDirectory()) {
            Collections.addAll(files, extraDdlDirectory.listFiles(sqlFileFilter));
        }

		// ファイル名 アルファベット順に並べかえる
		Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });
		try {
            executeBySqlFiles(files.toArray(new File[files.size()]));
		} catch (Exception e) {
			getLog().warn(e);
		}

        if (isOracle() && !schema.equals(user)) {
            // 使用するDB製品がOracleの時は、
            // ユーザが所有していないスキーマにDDLを流し込んだ場合は個別に権限を付与する。
            try {
                grantAnyToSchema(files);
            } catch (SQLException e) {
                getLog().warn(e);
            }
        }
	}

    private void executeSql(String sql) throws SQLException {
        if ("".equals(sql.trim())) {
            return;
        }
        Statement stmt = conn.createStatement();
        try {
            getLog().debug("SQL: " + sql);
            totalStatements++;
            stmt.execute(sql);
            successfulStatements++;
        } catch (SQLException ex) {
            throw new SQLException(sql, ex);
        } finally {
            StatementUtil.close(stmt);
        }

    }

    private void runStatements(Reader reader) throws SQLException, IOException {
        BufferedReader in = new BufferedReader(reader);
        StringBuilder sql = new StringBuilder();
        int overflow = SqlSplitter.NO_END;
        String line;
        while((line = in.readLine()) != null) {
            sql.append("\n").append(line);

            overflow = SqlSplitter.containsSqlEnd(line, delimiter, overflow);
            if (overflow > 0) {
                executeSql(sql.substring(0, sql.length() - delimiter.length()));
                sql.setLength(0);
                overflow = SqlSplitter.NO_END;
            }
        }
        if (sql.length() > 0) {
            executeSql(sql.toString());
        }

    }

    private void executeBySqlFiles(File...sqlFiles) throws SQLException, IOException {
        if (schema.equals(user)) {
            conn = DriverManager.getConnection(url, user, password);
        } else {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
        }

        successfulStatements = 0;
        totalStatements = 0;
        for (File sqlFile : sqlFiles) {
            Reader reader = null;
            try {
                reader = new FileReader(sqlFile);
                runStatements(reader);
            } catch(Exception e) {
                getLog().error(e);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }
        getLog().info(successfulStatements + " of " + totalStatements
                + " SQL statements executed successfully");
    }

    private boolean isOracle() {
        // 前の処理で接続文字列自体の構造が正しいことは分かっているのでそれ自体の精査は行わない。
        // 接続文字列は文字の大小を区別しないため単純なequalsは使用しない。
        return StringUtils.equalsIgnoreCase("oracle", StringUtils.split(url, ':')[1]);
    }

    private void grantAnyToSchema(List<File> files) throws SQLException {
        for (File file : files) {
            String fileName = file.getName();
            if (!fileName.contains("10_CREATE_")) {
                // filesは昇順にソートされていることを利用して、テーブル生成DDLの読み込みが終わったらループを終了する
                break;
            }
            String tableName = fileName.replaceAll("10_CREATE_|.sql", "");
//            System.out.println("GRANT ALL ON " + schema + "." + tableName + " TO " + user);

            // PreparedStatementで埋め込めるのはキーワードだけであり、スキーマ名やテーブル名には使用できないため。
            String sql = "GRANT ALL ON " + schema + "." + tableName + " TO " + user;
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        }
    }
}
