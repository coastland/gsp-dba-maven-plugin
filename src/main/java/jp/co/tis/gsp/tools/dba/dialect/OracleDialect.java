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
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.GenerationType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.StatementUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.Maps;

import jp.co.tis.gsp.tools.db.TypeMapper;
import jp.co.tis.gsp.tools.dba.dialect.param.ExportParams;
import jp.co.tis.gsp.tools.dba.dialect.param.ImportParams;

public class OracleDialect extends Dialect {
    protected final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

	private static final List<String> USABLE_TYPE_NAMES = new ArrayList<String>();
	
	static {
		USABLE_TYPE_NAMES.add("CHAR");
		USABLE_TYPE_NAMES.add("DATE");
		USABLE_TYPE_NAMES.add("LONG");
		USABLE_TYPE_NAMES.add("FLOAT");
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
	public void exportSchema(ExportParams params) throws MojoExecutionException {
		BufferedReader reader = null;
		try {
		    File dumpFile = params.getDumpFile();
		    String user = params.getUser();
		    String password = params.getPassword();
			String connectIdentifier = params.getConnectIdentifier();
		    String schema = params.getSchema();

            createDirectory(user, password, dumpFile.getParentFile());
			ProcessBuilder pb = new ProcessBuilder(
					"expdp",
					user + "/" + password + "@" + connectIdentifier,
                    "directory=exp_dir",
					"dumpfile=" + dumpFile.getName(),
					"schemas=" + schema,
                    "reuse_dumpfiles=y",
                    "nologfile=y");
			pb.redirectErrorStream(true);
			Process process = pb.start();

            Charset terminalCharset = System.getProperty("os.name").toLowerCase().contains("windows") ?
                    Charset.forName("Shift_JIS") : Charset.forName("UTF-8");

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
	public void importSchema(ImportParams params) throws MojoExecutionException{
		BufferedReader reader = null;

		try {
			File dumpFile = params.getDumpFile();

		    if (!dumpFile.exists())
		        throw new MojoExecutionException(dumpFile.getName() + " is not found?");

		    String user = params.getAdminUser();
		    String password = params.getAdminPassword();
			String connectIdentifier = params.getConnectIdentifier();
		    String schema = params.getSchema();

            createDirectory(user, password, dumpFile.getParentFile());
            
            // Oracleの場合はオブジェクトのドロップをする
            dropAllObjects(user, password, schema);
            
			ProcessBuilder pb = new ProcessBuilder(
					"impdp",
					user + "/" + password + "@" + connectIdentifier,
                    "directory=exp_dir",
					"dumpfile=" + dumpFile.getName(),
					"schemas=" + schema,
                    "nologfile=y",
                    "exclude=user");
			pb.redirectErrorStream(true);
			Process process = pb.start();
            Charset terminalCharset = System.getProperty("os.name").toLowerCase().contains("windows") ?
                    Charset.forName("Shift_JIS") : Charset.forName("UTF-8");

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createUser(String user, String password, String adminUser, String adminPassword) throws MojoExecutionException {
		Connection conn = null;
		Statement stmt = null;
		try {
            Properties props = new Properties();
            props.put("user", adminUser);
            props.put("password", adminPassword);
            conn = DriverManager.getConnection(url, props);
			stmt = conn.createStatement();
			
			if(existsUser(conn, user)) {
				// 既にユーザが存在している場合でも必要な権限を付与しておく。
  			    String grantSql = "GRANT UNLIMITED TABLESPACE, DATAPUMP_EXP_FULL_DATABASE, DATAPUMP_IMP_FULL_DATABASE TO " + user;
			    stmt.execute(grantSql);
                System.err.println("GRANT文を実行しました:\n" + grantSql);
				return;
			}

			stmt.execute("CREATE USER "+ user + " IDENTIFIED BY "+ password + " DEFAULT TABLESPACE users");
			String grantSql = "GRANT UNLIMITED TABLESPACE, DATAPUMP_EXP_FULL_DATABASE, DATAPUMP_IMP_FULL_DATABASE TO " + user;
			stmt.execute(grantSql);
            System.err.println("GRANT文を実行しました:\n" + grantSql);

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
			stmt.setString(1, user);
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
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, adminUser, adminPassword);
			// 指定スキーマがいなければ作成する。
			// Oracleの場合はユーザ＝スキーマなのでcreateUserで作成。
			if(!existsUser(conn, schema)) {
				createUser(schema, password, adminUser, adminPassword);
				return;
			}
			
			// テーブル、ビュー、シーケンスのみ削除
			ArrayList<String> dropObjectTypeList = new ArrayList<String>();
			dropObjectTypeList.add("TABLE");
			dropObjectTypeList.add("VIEW");
			dropObjectTypeList.add("SEQUENCE");
			
			dropObjectSpecifiedTypes(schema, adminUser, adminPassword, dropObjectTypeList);
			
		} catch (SQLException e) {
			throw new MojoExecutionException("データ削除中にエラー", e);
		} finally{
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
	public TypeMapper getTypeMapper() {
		return new TypeMapper(typeToNameMap);
	}
	
    @Override
    public String normalizeUserName(String userName) {
    	return StringUtils.upperCase(userName);
    }

	@Override
	public String normalizeSchemaName(String schemaName) {
		return StringUtils.upperCase(schemaName);
	}

	/**
	 * stmtの指定されたインデックスに指定された値をセットする。
	 * <p>
	 * Oracleの場合、JDBCドライバによっては、オブジェクト設定時に{@link PreparedStatement#setObject(int, Object, int)} を
	 * 使用するとデータ型の詳細を誤判定する(小数部がある型を少数部無しと判定する)。
	 * そのため、{@link PreparedStatement#setObject(int, Object)}を使用している。
	 * </p>
	 * @param stmt I/O
	 * @param parameterIndex parameter index
	 * @param value set value
	 * @param sqlType sql type
	 * @throws SQLException error
	 */
	@Override
	public void setObjectInStmt(PreparedStatement stmt, int parameterIndex, String value, int sqlType) throws SQLException {
		if(sqlType == UN_USABLE_TYPE) {
			stmt.setNull(parameterIndex, Types.NULL);
		} else if(StringUtil.isBlank(value) || "　".equals(value)) {
			stmt.setNull(parameterIndex, sqlType);
		} else {
			stmt.setObject(parameterIndex, value);
		}
	}

    /**
	 * ビュー定義を検索するSQLを返却する。
	 * @return ビュー定義を検索するSQL文
     */
	@Override
    public String getViewDefinitionSql() {
		return "select text as view_definition from dba_views where view_name= ? and owner = ?";
    }

    /**
     * シーケンス定義を検索するSQLを返却する。
     * @return シーケンス定義を検索するSQL文
     */
    @Override
    public String getSequenceDefinitionSql() {
        return "select sequence_name from dba_sequences where sequence_name= ? and sequence_owner = ?";
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
                System.err.println("[WARN] " + tableName + "." + colName + "  " + type + "型はサポートしていません。");
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
    
    /**
     * 指定スキーマ内で指定したタイプのオブジェクトを全削除します。
     * 
     * @param user
     * @param password
     * @param adminUser
     * @param adminPassword
     * @param schema
     * @param objectTypeList
     * @throws MojoExecutionException
     */
    private void dropObjectSpecifiedTypes(String schema, String adminUser, String adminPassword, 
    		 List<String> objectTypeList) throws MojoExecutionException {
    	
		PreparedStatement stmtMeta = null;
		Statement stmt = null;
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, adminUser, adminPassword);
			
			String dropObjectTypes = "'" + org.apache.commons.lang.StringUtils.join(objectTypeList, "','") + "'";
			
			// 12cで追加された自動採番機能で作成されるシーケンスオブジェクトは直接削除できないので、
			// generated = 'N' を指定して対象に入らないようにしている(generated はシステムが生成したものかどうかのフラグ)
			stmtMeta = conn.prepareStatement(
				"SELECT object_type, object_name FROM dba_objects WHERE " +
						"object_type in (" + dropObjectTypes + ") and owner = ? and generated = 'N'");
			stmtMeta.setString(1, schema);
			
			ResultSet rsMeta = stmtMeta.executeQuery();
			while(rsMeta.next()) {
				String objectType = rsMeta.getString("OBJECT_TYPE");
				String objectName = rsMeta.getString("OBJECT_NAME");
				if (!objectName.startsWith("BIN$")) {
					dropObject(conn, objectType, schema + "." + objectName);
				}
			}
			stmt = conn.createStatement();
			stmt.execute("PURGE RECYCLEBIN");
		} catch (SQLException e) {
			throw new MojoExecutionException("Drop Object実行中にエラー", e);
		} finally {
			StatementUtil.close(stmtMeta);
			StatementUtil.close(stmt);
			ConnectionUtil.close(conn);
		}
    }
    
    /**
     * 指定スキーマ内のオブジェクトを全て削除します.
     * 
     * @param adminUser
     * @param adminPassword
     * @param schema
     * @throws MojoExecutionException
     */
	private void dropAllObjects(String adminUser,
	    String adminPassword, String schema) throws MojoExecutionException {
		Connection conn = null;
		PreparedStatement stmtMeta = null;
		Statement stmt = null;
		try {
			conn = DriverManager.getConnection(url, adminUser, adminPassword);
			
			stmtMeta = conn.prepareStatement("SELECT DISTINCT OBJECT_TYPE FROM ALL_OBJECTS WHERE OWNER = ?");
			stmtMeta.setString(1, schema);
			ResultSet rsMeta = stmtMeta.executeQuery();
			ArrayList<String> tmpObjList = new ArrayList<String>(); 
			while(rsMeta.next()) {
				tmpObjList.add(rsMeta.getString(1));
			}
			stmtMeta.close();
			rsMeta.close();
			
			dropObjectSpecifiedTypes(schema, adminUser, adminPassword, tmpObjList);
			
		} catch (SQLException e) {
			throw new MojoExecutionException("データ削除中にエラー", e);
		} finally {
			StatementUtil.close(stmtMeta);
			StatementUtil.close(stmt);
			ConnectionUtil.close(conn);
		}
	}

	/**
	 * OracleでDate型・Timestamp型の時にgetString()した文字表現は、load-dataで取り込み <br />
	 * 不可能なフォーマットだったため変換する。
	 */
	@Override
    public String convertLoadData(ResultSet resultSet, int columnIndex, String columnLabel, int sqlType) 
            throws SQLException {
        String value = null;
        switch (sqlType) {
        case Types.DATE:
            Date date = resultSet.getDate(columnLabel);
            if (date != null) {
                value = sdfDate.format(date);
            }
            break;
        default:
            value = super.convertLoadData(resultSet, columnIndex, columnLabel, sqlType);
            break;
        }

        // null to blank.
        value = StringUtils.defaultIfEmpty(value, "");
        
        return value;
    }
}
