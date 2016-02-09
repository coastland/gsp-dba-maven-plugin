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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.FileOutputStreamUtil;
import org.seasar.framework.util.StatementUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.framework.util.tiger.Maps;

import jp.co.tis.gsp.tools.db.AbstractDbObjectParser;
import jp.co.tis.gsp.tools.db.AlternativeGenerator;
import jp.co.tis.gsp.tools.db.TypeMapper;
import jp.co.tis.gsp.tools.dba.util.ProcessUtil;

public class MysqlDialect extends Dialect {

	private final static String DRIVER = "com.mysql.jdbc.Driver";

	private Map<Integer, String> typeToNameMap = Maps
		.map(Types.BIGINT, "BIGINT")
		.$(Types.BLOB, "BLOB")
		.$(Types.BOOLEAN, "BOOLEAN")
		.$(Types.CHAR, "CHAR")
		.$(Types.CLOB, "TEXT")
		.$(Types.DATE, "DATE")
		.$(Types.DECIMAL, "NUMBER")
		.$(Types.DOUBLE, "DOUBLE")
		.$(Types.FLOAT, "FLOAT")
		.$(Types.INTEGER, "INT")
		.$(Types.TIMESTAMP, "TIMESTAMP")
		.$(Types.VARCHAR, "VARCHAR")
		.$();


	@Override
	public void exportSchema(File dumpFile) throws MojoExecutionException {
		BufferedInputStream in = null;
		FileOutputStream out = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(
					"mysqldump",
					schema,
					"-u", user,
					"--password="+password);
			Process process = pb.start();
			in = new BufferedInputStream(process.getInputStream());

			out = FileOutputStreamUtil.create(dumpFile);
			byte[] buf = new byte[4096];
			while(true) {
				int res = in.read(buf);
				if (res <= 0) break;
				out.write(buf, 0, res);
			}

		} catch (IOException e) {
			throw new MojoExecutionException("mysqldump", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	public void dropAll() throws MojoExecutionException {
		if(adminPassword == null)
			adminPassword = "";
		try {
			ProcessUtil.exec(
					"mysqladmin",
					"-f", // force
					"-u", adminUser,
					"--password=" + adminPassword,
					"drop",
					schema
					);
			ProcessUtil.exec(
					"mysqladmin",
					"-u", adminUser,
					"--password=" + adminPassword,
					"create",
					schema
					);
		} catch (IOException e) {
			throw new MojoExecutionException("mysqldump", e);
		}
	}

	@Override
	public void createUser() throws MojoExecutionException{
		DriverManagerUtil.registerDriver(DRIVER);
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DriverManager.getConnection(url, adminUser, adminPassword);
			stmt = conn.createStatement();
			if(existsUser(conn, user)) {
				return;
			}
			try {
				stmt.execute("DROP USER '"+ user + "'");
			} catch(SQLException ignore) {
				// DROP USERに失敗しても気にしない
			}
			stmt.execute("CREATE USER '"+ user + "' IDENTIFIED BY '"+ password +"'");
			stmt.execute("GRANT ALL ON *.* TO '" + user + "'");
		} catch (SQLException e) {
			throw new MojoExecutionException("CREATE USER実行中にエラー", e);
		} finally {
			StatementUtil.close(stmt);
			ConnectionUtil.close(conn);
		}
	}

	@Override
	public void grantAllToAnotherSchema(Connection conn) throws SQLException, UnsupportedOperationException {
		throw new UnsupportedOperationException("このデータベースで実行する時は、別スキーマは指定できません。");
 	}

	@Override
	public void createSchemaIfNotExist(Connection conn) throws SQLException, UnsupportedOperationException {
		throw new UnsupportedOperationException("このデータベースで実行する時は、別スキーマは指定できません。");
	}

	private boolean existsUser(Connection conn, String user) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(
					"SELECT count(*) AS num FROM mysql.user WHERE User=?");
			stmt.setString(1, user);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return (rs.getInt("num") > 0);
		} finally {
			StatementUtil.close(stmt);
		}
	}

	@Override
	public void importSchema(File dumpFile) throws MojoExecutionException {
		try {
			ProcessUtil.execWithInput(dumpFile,
					"mysql",
					"-u", user,
					"--password="+ password,
					schema
					);
		} catch (IOException e) {
			throw new MojoExecutionException("スキーマインポート実行中にエラー", e);
		}

	}

	@Override
	public TypeMapper getTypeMapper() {
		return new TypeMapper(typeToNameMap);
	}

	@Override
	public List<AlternativeGenerator> getAlternativeGenerators() {
		List<AlternativeGenerator> generators = CollectionsUtil.newArrayList(10);
		generators.add(new AlternativeGenerator() {
			@Override public String getName() { return "sequence"; }
			@Override public void generate(List<Map<String, String>> objectDef, AbstractDbObjectParser context) {
				if (CollectionUtils.isEmpty(objectDef))
					return;

				try {
					FileUtils.write(new File(context.getOutputDirectory(), "01_CREATE_SEQUENCES.sql"),
							"create TABLE sequences("
							+ "name varchar(255) primary key,"
							+ "seq BIGINT default 1);");
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
			}
		});
		return generators;
	}

    /**
     * ビュー定義を検索するSQLを返却する。
     * @return ビュー定義を検索するSQL文
     */
	@Override
	public String getViewDefinitionSql() {
		return String.format("SELECT view_definition FROM information_schema.views WHERE table_name=? AND TABLE_SCHEMA='%s'", schema);
	}
}
