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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.GenerationType;

import jp.co.tis.gsp.tools.db.TypeMapper;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;
import org.seasar.framework.util.tiger.Maps;

public class OracleDialect extends Dialect {
	private String url;
	private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final List<String> USABLE_TYPE_NAMES = new ArrayList<String>();
	
	static {
		USABLE_TYPE_NAMES.add("CHAR");
		USABLE_TYPE_NAMES.add("DATE");
		USABLE_TYPE_NAMES.add("LONG");
		USABLE_TYPE_NAMES.add("NCHAR");
		USABLE_TYPE_NAMES.add("NUMBER");
		USABLE_TYPE_NAMES.add("NVARCHAR2");
		USABLE_TYPE_NAMES.add("VARCHAR2");
	}

	private Map<Integer, String> typeToNameMap = Maps
			.map(Types.BIGINT, "NUMBER(18,0)")
			.$(Types.BLOB, "BLOB")
			.$(Types.BOOLEAN, "NUMBER(1,0)")
			.$(Types.CHAR, "CHAR")
			.$(Types.CLOB, "CLOB")
			.$(Types.DATE, "DATE")
			.$(Types.DECIMAL, "NUMBER")
			.$(Types.DOUBLE, "DOUBLE")
			.$(Types.FLOAT, "FLOAT")
			.$(Types.INTEGER, "NUMBER(9,0)")
			.$(Types.TIMESTAMP, "TIMESTAMP")
			.$(Types.VARCHAR, "VARCHAR2")
			.$();

    public OracleDialect() {
        GenDialectRegistry.deregister(
                org.seasar.extension.jdbc.dialect.OracleDialect.class
        );
        GenDialectRegistry.register(
                org.seasar.extension.jdbc.dialect.OracleDialect.class,
                new ExtendedOracleGenDialect()
        );
    }

    private void createDirectory(String user, String password, File directory) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            stmt.execute("CREATE OR REPLACE DIRECTORY exp_dir as '" + directory.getAbsolutePath() + "'");
        } finally {
            StatementUtil.close(stmt);
            ConnectionUtil.close(conn);
        }
    }

	@Override
	public void exportSchema(String user, String password, String schema, File dumpFile) throws MojoExecutionException {
		BufferedReader reader = null;
		try {
            createDirectory(user, password, dumpFile.getParentFile());
			ProcessBuilder pb = new ProcessBuilder(
					"expdp",
					user + "/" + password,
                    "directory=exp_dir",
					"dumpfile=" + dumpFile.getName(),
					"schemas=" + schema,
                    "reuse_dumpfiles=y",
                    "nologfile=y");
			pb.redirectErrorStream(true);
			Process process = pb.start();

            Charset terminalCharset = System.getProperty("os.name").toLowerCase().contains("windows") ?
                    Charset.forName("Shift_JIS") : StandardCharsets.UTF_8;

            reader = new BufferedReader(
		            new InputStreamReader(process.getInputStream(), terminalCharset));
			//標準エラー出力が標準出力にマージして出力されるので、標準出力だけ読み出せばいい
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
		    }

            process.waitFor();
            if (process.exitValue() != 0) {
                throw new MojoExecutionException("oracle export error");
            }
            process.destroy();
        } catch (Exception e) {
            throw new MojoExecutionException("oracle export", e);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	@Override
	public void importSchema(String user, String password, String schema,
			File dumpFile) throws MojoExecutionException{
		BufferedReader reader = null;
		try {
            createDirectory(user, password, dumpFile.getParentFile());
			ProcessBuilder pb = new ProcessBuilder(
					"impdp",
					user + "/" + password,
                    "directory=exp_dir",
					"dumpfile=" + dumpFile.getName(),
					"schemas=" + schema,
                    "nologfile=y");
			pb.redirectErrorStream(true);
			Process process = pb.start();
            Charset terminalCharset = System.getProperty("os.name").toLowerCase().contains("windows") ?
                    Charset.forName("Shift_JIS") : StandardCharsets.UTF_8;

			reader = new BufferedReader(
		            new InputStreamReader(process.getInputStream(), terminalCharset));
			//標準エラー出力が標準出力にマージして出力されるので、標準出力だけ読み出せばいい
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
		    }

            process.waitFor();
            if (process.exitValue() != 0) {
                throw new MojoExecutionException("oracle import error");
            }
            process.destroy();
        } catch (Exception e) {
            throw new MojoExecutionException("oracle import", e);
		} finally {
			IOUtils.closeQuietly(reader);
		}

	}

	@Override
	public void createUser(String user, String password, String adminUser, String adminPassword) throws MojoExecutionException {
		DriverManagerUtil.registerDriver(DRIVER);
		Connection conn = null;
		Statement stmt = null;
		try {
            Properties props = new Properties();
            props.put("user", adminUser);
            props.put("password", adminPassword);
            conn = DriverManager.getConnection(url, props);
			stmt = conn.createStatement();
			if(existsUser(conn, user)) {
				return;
			}
			try {
				stmt.execute("DROP USER "+ user);
			} catch(SQLException ignore) {
				// DROP USERに失敗しても気にしない
			}
			stmt.execute("CREATE USER "+ user + " IDENTIFIED BY "+ password + " DEFAULT TABLESPACE users");
            String grantSql = "GRANT CONNECT, RESOURCE, SELECT ANY TABLE, CREATE VIEW, CREATE ANY TABLE, CREATE SYNONYM, CREATE ANY DIRECTORY TO " + user;
			stmt.execute(grantSql);
            System.err.println(grantSql);
            String grantExecuteSql = "GRANT EXECUTE ON SYS.DBMS_ALERT TO " + user;
            try {
                stmt.execute(grantExecuteSql);
            } catch (SQLException e) {
                System.err.println("DBMS_ALERT 権限の付与に失敗しました。\n"
                        + "本権限は、tesloggerのDBキャプチャに必要になります。\n"
                        + "DBキャプチャが必要であれば、手動で権限を設定してください。\n"
                        + "実行SQL： [" + grantExecuteSql + "]");
            }
		} catch (SQLException e) {
			throw new MojoExecutionException("CREATE USER実行中にエラー", e);
		} finally {
			StatementUtil.close(stmt);
			ConnectionUtil.close(conn);
		}
	}

	private boolean existsUser(Connection conn, String user) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(
					"SELECT count(*) AS num FROM dba_users WHERE username=?");
			stmt.setString(1, StringUtils.upperCase(user));
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return (rs.getInt("num") > 0);
		} finally {
			StatementUtil.close(stmt);
		}
	}

	@Override
	public void dropAll(String user, String password, String adminUser,
			String adminPassword, String schema) throws MojoExecutionException {
		DriverManagerUtil.registerDriver(DRIVER);
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, adminUser, adminPassword);
			// 目的のユーザがいなければ何もしない
			if(!existsUser(conn, user)) {
				return;
			}
		} catch (SQLException e) {
			throw new MojoExecutionException("データ削除中にエラー", e);
		} finally {
			ConnectionUtil.close(conn);
		}

		PreparedStatement stmtMeta = null;
		Statement stmt = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			stmtMeta = conn.prepareStatement("SELECT object_type, object_name FROM user_objects WHERE object_type in ('TABLE', 'VIEW', 'SEQUENCE', 'PACKAGE', 'FUNCTION', 'SYNONYM')");

			ResultSet rsMeta = stmtMeta.executeQuery();
			while(rsMeta.next()) {
				String objectType = rsMeta.getString("OBJECT_TYPE");
				String objectName = rsMeta.getString("OBJECT_NAME");
				if (!objectName.startsWith("BIN$")) {
					dropObject(conn, objectType, objectName);
				}
			}
			stmt = conn.createStatement();
			stmt.execute("PURGE RECYCLEBIN");
		} catch (SQLException e) {
			throw new MojoExecutionException("データ削除中にエラー", e);
		} finally {
			StatementUtil.close(stmtMeta);
			StatementUtil.close(stmt);
			ConnectionUtil.close(conn);
		}
	}

	private void dropObject(Connection conn, String objectType,
			String objectName) throws SQLException {
		Statement stmt = null;
		try {
			stmt =  conn.createStatement();
			String cascade = (StringUtils.equalsIgnoreCase(objectType, "TABLE"))?" CASCADE CONSTRAINTS": "";
			String sql = "DROP " + objectType + " " + objectName + cascade;
			System.err.println(sql);
			stmt.execute(sql);
		} catch (SQLException e) {
			throw e;
		} finally {
			StatementUtil.close(stmt);
		}
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public TypeMapper getTypeMapper() {
		return new TypeMapper(typeToNameMap);
	}

	@Override
	public String normalizeSchemaName(String schemaName) {
		return StringUtils.upperCase(schemaName);
	}

	@Override
	public GenerationType getGenerationType() { return GenerationType.SEQUENCE; }

	@Override
	public String getViewDefinitionSql() {
		return "select text as view_definition from user_views where view_name=?";
	}

	@Override
    public String getSequenceDefinitionSql() {
        return "select sequence_name from user_sequences where sequence_name=?";
    }

    @Override
    public int guessType(Connection conn, String schema, String tableName, String colName) throws SQLException {
        if (metaData == null) {
            metaData = conn.getMetaData();
        }

		ResultSet rs = null;
		try {
			rs = metaData.getColumns(null,
					normalizeSchemaName(schema),
					normalizeTableName(tableName),
					normalizeColumnName(colName));

            if (!rs.next()) {
                throw new SQLException(tableName + "に" + colName + "を見つけられません。");
            }
            String type = rs.getString("TYPE_NAME");
            if (!isUsableType(type)) {
                System.err.println(type + "型はサポートしていません。");
                return UN_USABLE_TYPE;
            } else if ("VARCHAR2".equals(type) || "NVARCHAR2".equals(type) || "NCHAR".equals(type)) {
                return Types.VARCHAR;
            } else if ("DATE".equals(type)) { // Types.TIMESTAMPとなるため。
                return Types.DATE;
            } else if (type.startsWith("TIMESTAMP")) { // TIMESTAMP(6)以外はTypes.OTHERとなるため。
                return Types.TIMESTAMP;
            }
            return rs.getInt("DATA_TYPE");
        } finally {
			if (rs != null) {
				rs.close();
			}
		}
    }

    @Override
    public boolean isUsableType(String type) {
        return USABLE_TYPE_NAMES.contains(type) 
            || type.startsWith("TIMESTAMP"); // oracleのtimestamp型名はTIMESTAMP(0)～TIMESTAMP(9)となるため
    }
}
