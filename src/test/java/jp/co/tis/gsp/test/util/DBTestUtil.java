package jp.co.tis.gsp.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;
import org.seasar.framework.util.StringUtil;

public class DBTestUtil {

	private static final String FS = System.getProperty("file.separator");

	/**
	 * 指定ユーザをドロップします。
	 * 
	 * H2はデータベース削除（ＤＢファイルも）とします。
	 * 
	 * @param schema
	 * @param user
	 * @param password
	 * @param adminUser
	 * @param adminPassword
	 * @param url
	 * @param driver
	 * @param testDb
	 * @throws SQLException
	 */
	static public void dropUser(String schema, String user, String password, String adminUser, String adminPassword,
			String url, String driver, TestDB testDb) throws SQLException {

		Connection conn = null;
		Statement stmt = null;
		String sql = null; // drop schema
		String countSql = ""; // schema存在確認
		String dropUser = "";

		try {

			DriverManagerUtil.registerDriver(driver);
			conn = DriverManager.getConnection(url, adminUser, adminPassword);
			stmt = conn.createStatement();

			switch (testDb) {
			case oracle:
				dropUser = StringUtils.upperCase(user);
				countSql = String.format("SELECT count(*) AS num FROM dba_users WHERE username='%s'", dropUser);
				if (getCount(countSql, url, adminUser, adminPassword) == 0)
					return;

				sql = String.format("DROP USER %s CASCADE", dropUser);
				stmt.execute(sql);
				break;
			case db2:
				throw new UnsupportedOperationException("DB2のユーザの考え方はOSユーザなのでDrop Userは不可。");
			case h2:
				stmt.execute("DROP ALL OBJECTS DELETE FILES");
				break;
			case postgresql:
				dropUser = StringUtils.lowerCase(user);
				countSql = String.format("SELECT count(*) AS num FROM pg_user WHERE usename='%s'", dropUser);
				if (getCount(countSql, url, adminUser, adminPassword) == 0)
					return;

				stmt.execute("DROP OWNED BY " + dropUser + " CASCADE");
				stmt.execute("DROP ROLE " + dropUser);
				break;
			case sqlserver:
				dropUser = user;

				// ログインユーザ
				countSql = String.format("SELECT COUNT(*) AS num FROM sys.syslogins WHERE name = '%s'", dropUser);
				if (getCount(countSql, url, adminUser, adminPassword) == 0)
					return;

				stmt.execute("DROP LOGIN " + dropUser);

				// ＤＢユーザ
				countSql = String.format("SELECT COUNT(*) AS num FROM sys.sysusers WHERE name = '%s'", dropUser);
				if (getCount(countSql, url, adminUser, adminPassword) == 0)
					return;

				String schemaSql = String
						.format("SELECT s.name FROM sys.schemas s WHERE s.principal_id = USER_ID('%s')", user);
				ResultSet rs = stmt.executeQuery(schemaSql);

				while (rs.next()) {
					dropSchema(rs.getString(1), dropUser, password, adminUser, adminPassword, url, driver, testDb);
				}

				stmt.execute("DROP USER " + dropUser);

				break;
			case mysql:
				dropUser = user;

				countSql = String.format("SELECT count(*) AS num FROM mysql.user WHERE User='%s'", dropUser);
				if (getCount(countSql, url, adminUser, adminPassword) == 0)
					return;

				stmt.execute("DROP USER " + dropUser);

				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("DBTestUtilエラー：ユーザのドロップに失敗しました", e);
		} finally {
			StatementUtil.close(stmt);
			ConnectionUtil.close(conn);
		}
	}

	/**
	 * 指定スキーマをドロップします。
	 * 
	 * H2はデータベース削除（ＤＢファイルも）とします。
	 * 
	 * @param schema
	 * @param user
	 * @param password
	 * @param adminUser
	 * @param adminPassword
	 * @param url
	 * @param driver
	 * @param testDb
	 * @throws SQLException
	 */
	static public void dropSchema(String schema, String user, String password, String adminUser, String adminPassword,
			String url, String driver, TestDB testDb) throws SQLException {

		Connection conn = null;
		Statement stmt = null;
		String sql = null; // drop schema
		String countSql = ""; // schema存在確認
		String dropSchema = "";

		try {

			DriverManagerUtil.registerDriver(driver);
			conn = DriverManager.getConnection(url, adminUser, adminPassword);
			stmt = conn.createStatement();

			switch (testDb) {
			case oracle:
				dropSchema = StringUtils.upperCase(schema);
				countSql = String.format("SELECT count(*) AS num FROM dba_users WHERE username='%s'", dropSchema);
				if (getCount(countSql, url, adminUser, adminPassword) == 0)
					return;

				sql = String.format("DROP USER %s CASCADE", dropSchema);
				stmt.execute(sql);
				break;
			case db2:
				dropSchema = StringUtils.upperCase(schema);
				countSql = String.format("select count(*) as num from SYSCAT.SCHEMATA where SCHEMANAME='%s'",
						dropSchema);
				if (getCount(countSql, url, adminUser, adminPassword) == 0)
					return;

				CallableStatement cs = conn.prepareCall("{CALL SYSPROC.ADMIN_DROP_SCHEMA(?, ?, ?, ?)}");
				cs.setString(1, dropSchema);
				cs.setString(2, null);
				cs.setString(3, "ERRORSCHEMA");
				cs.setString(4, "ERRORTABLE");
				cs.execute();

				break;
			case h2:
				stmt.execute("DROP ALL OBJECTS DELETE FILES");
				break;
			case postgresql:
				dropSchema = StringUtils.lowerCase(schema);
				countSql = String.format(
						"SELECT COUNT(SCHEMA_NAME) as num FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='%s'",
						dropSchema);
				if (getCount(countSql, url, adminUser, adminPassword) == 0)
					return;

				sql = String.format("DROP SCHEMA %s CASCADE", dropSchema);
				stmt.execute(sql);
				break;
			case sqlserver:
				dropSchema = schema;
				countSql = String.format("SELECT COUNT(*) as num FROM sys.schemas WHERE name ='%s'", dropSchema);
				if (getCount(countSql, url, adminUser, adminPassword) == 0)
					return;

				List<String> args = new ArrayList<String>();
				if (isWindows()) {
					// Windows
					args.add("cmd");
					args.add("/c");
					args.add("sqlcmd");
				} else {
					// Linux(Docker)
					args.add("/opt/mssql-tools/bin/sqlcmd");
				}
				args.add("-S");
				args.add(url.split("/")[2].split(":")[0]);
				args.add("-U");
				args.add(adminUser);
				args.add("-P");
				args.add(adminPassword);
				args.add("-d");
				args.add(conn.getCatalog());
				args.add("-i");
				args.add(new File(DBTestUtil.class.getResource(DBTestUtil.class.getSimpleName() + "_test" + "/dropAll.sql").getPath()).getAbsolutePath());

				ProcessBuilder pb = new ProcessBuilder(args.toArray(new String[0]));

				final Map<String, String> environment = pb.environment();
				environment.put("SCHEMA", dropSchema);

				pb.redirectErrorStream(true);
				Process process = pb.start();
				Charset terminalCharset = System.getProperty("os.name").toLowerCase().contains("windows")
						? Charset.forName("Shift_JIS") : Charset.forName("UTF-8");
				BufferedReader reader = null;
				reader = new BufferedReader(new InputStreamReader(process.getInputStream(), terminalCharset));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				process.waitFor();
				if (process.exitValue() != 0) {
					throw new MojoExecutionException("SQLServer schema Drop error");
				}
				process.destroy();

				break;
			case mysql:
				//throw new UnsupportedOperationException("gspが現状ではMySQLのスキーマ生成に対応していないので用途はないはず");
				String dbName = conn.getCatalog();
				sql = String.format("DROP DATABASE IF EXISTS %s", dbName);
				stmt.execute(sql);
				sql = String.format("CREATE DATABASE %s DEFAULT CHARACTER SET utf8", dbName);
				stmt.execute(sql);
				// break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("DBTestUtilエラー：スキーマのドロップに失敗しました", e);
		} finally {
			StatementUtil.close(stmt);
			ConnectionUtil.close(conn);
		}
	}

	/**
	 * 現在の実行環境がWindowsかどうか判定する。
	 * @return Windowsの場合はtrue
	 */
	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	/**
	 * 指定されたSQLの件数取得
	 * 
	 * @param sql
	 * @param url
	 * @param user
	 * @param password
	 * @return カウント
	 * @throws SQLException
	 */
	static public int getCount(String sql, String url, String user, String password) throws SQLException {

		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("DBTestUtilエラー", "件数カウントに失敗しました");
		} finally {
			ResultSetUtil.close(rs);
			StatementUtil.close(stmt);
			ConnectionUtil.close(conn);
		}

	}

	/**
	 * 指定されたSQLの値取得
	 * 
	 * @param sql
	 * @param url
	 * @param user
	 * @param password
	 * @return カウント
	 * @throws SQLException
	 */
	static public <T> T getValueOfSql(String sql, String url, String user, String password) throws SQLException {

		Statement stmt = null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			return (T) rs.getObject(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("DBTestUtilエラー", "SQLの実行に失敗しました");
		} finally {
			StatementUtil.close(stmt);
			ConnectionUtil.close(conn);
		}
	}

	/**
	 * バイナリデータテスト用テーブルへのデータ投入.
	 * 
	 * 下記テーブルを想定。<br />
	 * 
	 * <pre>
	 * CREATE TABLE FILE_TBL(
	 *   FILE_ID [数値型],
	 *   FILE_NAME [文字型],
	 *   CONTENT [バイナリ型]
	 * )
	 * </pre>
	 * 
	 * @param fileId
	 *            - ファイルID
	 * @param file
	 *            - ファイルオブジェクト
	 * @param url
	 *            - jdbcURL
	 * @param user
	 *            - ユーザ名
	 * @param password
	 *            - パスワード
	 * @param tableName
	 *            - デフォルト：FILE_TBL スキーマ修飾したい場合はテーブル指定する
	 * @throws IOException
	 */
	static public void insertFileTbl(Long fileId, File file, String url, String user, String password, String tableName)
			throws IOException {

		PreparedStatement psmt = null;
		Connection conn = null;
		InputStream fileIn = null;
		try {

			conn = DriverManager.getConnection(url, user, password);

			if (StringUtil.isBlank(tableName)) {
				tableName = "FILE_TBL";
			}

			String sql = "INSERT INTO " + tableName + " VALUES(?, ?, ?)";
			psmt = conn.prepareStatement(sql);
			psmt.setLong(1, fileId);
			psmt.setString(2, file.getName());

			fileIn = new FileInputStream(file);
			psmt.setBinaryStream(3, fileIn);
			psmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			StatementUtil.close(psmt);
			ConnectionUtil.close(conn);
			fileIn.close();
		}
	}

	/**
	 * 
	 * 
	 * @param sql
	 * @param file
	 * @param url
	 * @param user
	 * @param password
	 */
	static public void selectSimpleBinary(String sql, File file, String url, String user, String password) {

		PreparedStatement psmt = null;
		ResultSet rset = null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			psmt = conn.prepareStatement(sql);
			rset = psmt.executeQuery();
			rset.getBinaryStream(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			StatementUtil.close(psmt);
			ConnectionUtil.close(conn);
		}
	}
}
