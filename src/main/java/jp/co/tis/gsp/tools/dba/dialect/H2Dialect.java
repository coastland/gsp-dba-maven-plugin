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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import jp.co.tis.gsp.tools.db.TypeMapper;

import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.StatementUtil;

public class H2Dialect extends Dialect {
	private String url;
	private static final String DRIVER = "org.h2.Driver";

	@Override
	public void exportSchema(String user, String password, String schema,
			File dumpFile) throws MojoExecutionException {
		DriverManagerUtil.registerDriver(DRIVER);
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
			Statement stmt = conn.createStatement();
            stmt.execute("SCRIPT TO '" + dumpFile.getAbsolutePath()+ "'");
			StatementUtil.close(stmt);
		} catch (SQLException e) {
			throw new MojoExecutionException("Schema export実行中にエラー", e);
		} finally {
			ConnectionUtil.close(conn);
		}
	}

	@Override
	public void dropAll(String user, String password, String adminUser,
			String adminPassword, String schema) throws MojoExecutionException {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void importSchema(String user, String password, String schema,
			File dumpFile) throws MojoExecutionException {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void createUser(String user, String password, String adminUser,
			String adminPassword) throws MojoExecutionException {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void setUrl(String url) {
		this.url = url;

	}

	@Override
	public TypeMapper getTypeMapper() {
		return new TypeMapper();
	}

}
