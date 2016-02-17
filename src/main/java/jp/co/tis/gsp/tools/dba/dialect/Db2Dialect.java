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

import jp.co.tis.gsp.tools.db.TypeMapper;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.StatementUtil;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Db2Dialect extends Dialect {
    private static final String DRIVER = "com.ibm.db2.jcc.DB2Driver";
    private static final List<String> USABLE_TYPE_NAMES = new ArrayList<String>();
    
    static {
        USABLE_TYPE_NAMES.add("BIGINT");
        USABLE_TYPE_NAMES.add("CHAR");
        USABLE_TYPE_NAMES.add("CLOB");
        USABLE_TYPE_NAMES.add("DATE");
        USABLE_TYPE_NAMES.add("DBCLOB");
        USABLE_TYPE_NAMES.add("DECIMAL");
        USABLE_TYPE_NAMES.add("DOUBLE");
        USABLE_TYPE_NAMES.add("GRAPHIC");
        USABLE_TYPE_NAMES.add("INTEGER");
        USABLE_TYPE_NAMES.add("LONG VARCHAR");
        USABLE_TYPE_NAMES.add("LONG VARGRAPHIC");
        USABLE_TYPE_NAMES.add("DECIMAL");
        USABLE_TYPE_NAMES.add("REAL");
        USABLE_TYPE_NAMES.add("SMALLINT");
        USABLE_TYPE_NAMES.add("TIME");
        USABLE_TYPE_NAMES.add("TIMESTAMP");
        USABLE_TYPE_NAMES.add("VARCHAR");
        USABLE_TYPE_NAMES.add("VARGRAPHIC");
    }
    
    public Db2Dialect() {
        GenDialectRegistry.deregister(
                org.seasar.extension.jdbc.dialect.Db2Dialect.class
        );
        GenDialectRegistry.register(
                org.seasar.extension.jdbc.dialect.Db2Dialect.class,
                new ExtendedDb2GenDialect()
        );
    }

    /**
     * DB2ではスキーマ構造をエクスポートできないため、export-schemaをサポートしない。
     */
    @Override
    public void exportSchema(String user, String password, String schema, File dumpFile) throws MojoExecutionException {
        throw new UnsupportedOperationException("db2を用いたexport-schemaはサポートしていません。");
    }

    @Override
    public void dropAll(String user, String password, String adminUser,
            String adminPassword, String schema) throws MojoExecutionException {
        DriverManagerUtil.registerDriver(DRIVER);
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            // 目的のスキーマがなければ何もしない
            if(!existsSchema(conn, schema)) {
                return;
            }
            // テーブル・ビューの削除
            stmt = conn.prepareStatement("select TABNAME, TYPE from SYSCAT.TABLES where TABSCHEMA=? and OWNERTYPE='U' and TYPE in('T', 'V')");
            stmt.setString(1, normalizeSchemaName(schema));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dropObject(conn, normalizeSchemaName(schema), getObjectType(rs.getString("TYPE")), rs.getString("TABNAME"));
            }
            // シーケンスの削除
            stmt = conn.prepareStatement("select SEQNAME from SYSCAT.SEQUENCES where SEQSCHEMA=? and OWNERTYPE='U' and SEQTYPE in('I', 'S')");
            stmt.setString(1, normalizeSchemaName(schema));
            rs = stmt.executeQuery();
            while (rs.next()) {
                dropObject(conn, normalizeSchemaName(schema), "SEQUENCE", rs.getString("SEQNAME"));
            }
        } catch (SQLException e) {
            throw new MojoExecutionException("データ削除中にエラー", e);
        } finally {
            StatementUtil.close(stmt);
            ConnectionUtil.close(conn);
        }
    }
    
    private boolean existsSchema(Connection conn, String schema) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("select count(*) as num from SYSIBM.SYSSCHEMAAUTH where SCHEMANAME=?");
            stmt.setString(1, StringUtils.upperCase(schema));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return (rs.getInt("num") > 0);
        } finally {
            StatementUtil.close(stmt);
        }
    }
    
    private void dropObject(Connection conn, String schema, String objectType, String objectName) throws SQLException {
        Statement stmt = null;
        try {
            stmt =  conn.createStatement();
            String sql = "DROP " + objectType + " " + schema + "." + objectName;
            System.err.println(sql);
            stmt.execute(sql);
        } catch (SQLException e) {
            throw e;
        } finally {
            StatementUtil.close(stmt);
        }
    }
    
    private String getObjectType(String type) {
        if ("T".equals(type)) {
            return "TABLE";
        } else if ("V".equals(type)) {
            return "VIEW";
        }
        
        return type;
    }

    /**
     * DB2ではスキーマ構造をエクスポートできないため、import-schemaをサポートしない。
     */
    @Override
    public void importSchema(String user, String password, String schema, File dumpFile) throws MojoExecutionException {
        throw new UnsupportedOperationException("db2を用いたimport-schemaはサポートしていません。");
    }

    /**
     * ユーザは先に作成されていることを前提としている。
     * 本処理では、DBへのアクセス権限を付与するだけ。
     */
    @Override
    public void createUser(String user, String password, String adminUser,
            String adminPassword) throws MojoExecutionException {
        DriverManagerUtil.registerDriver(DRIVER);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            stmt = conn.createStatement();
            stmt.execute("grant connect on database to user " + user);
            ConnectionUtil.close(conn);
            try {
                conn = DriverManager.getConnection(url, user, password); // ログインIDが存在しない場合に失敗する。
            } catch (SQLException e) {
                throw new MojoExecutionException("指定されたユーザがOSに存在しない、またはパスワードが間違っている可能性があります。", e);
            }
        } catch (SQLException e) {
            throw new MojoExecutionException("CREATE USER実行中にエラー", e);
        } finally {
            StatementUtil.close(stmt);
            ConnectionUtil.close(conn);
        }
    }

    @Override
    public void grantAllToAnotherSchema(String schema, String user, String password, String admin, String adminPassword) throws MojoExecutionException {
    	
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try{
        	conn = getJDBCConnection(DRIVER, admin, adminPassword);
        	pstmt = conn.prepareStatement(
	                "select TABNAME from SYSCAT.TABLES where TABSCHEMA=? and OWNERTYPE='U'");
        	pstmt.setString(1, StringUtils.upperCase(schema));
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            String tableName = rs.getString("TABNAME");
	            // PreparedStatementで埋め込めるのはキーワードだけであり、スキーマ名やテーブル名には使用できないため。
	            String sql = "GRANT ALL ON " + schema + "." + tableName + " TO USER " + user;
	            conn.createStatement().execute(sql);
	        }
        
        } catch (SQLException e) {
            throw new MojoExecutionException("権限付与処理 実行中にエラー: ", e);
        } finally {
        	StatementUtil.close(pstmt);
            ConnectionUtil.close(conn);
        }
    }

    @Override
    public void createSchema(String schema, String user, String password, String admin, String adminPassword) throws MojoExecutionException {
    	
    	PreparedStatement  userStmt = null;
        Statement createUserStmt = null;
        Connection conn = null;
    	
        try{
			conn = getJDBCConnection(DRIVER, admin, adminPassword);
	        
	 		userStmt = conn.prepareStatement("SELECT COUNT(*) AS NUM FROM SYSCAT.SCHEMATA WHERE SCHEMANAME=?");
			userStmt.setString(1, StringUtils.upperCase(schema));
			ResultSet rs = userStmt.executeQuery();
			rs.next();
			if (rs.getInt("num") > 0) {
				// すでにデータ流し込み対象のスキーマが存在していれば何もしない
				return;
			}
	
			createUserStmt = conn.createStatement();
			createUserStmt.execute("CREATE SCHEMA "+ schema);
		
		} catch (SQLException e) {
			throw new MojoExecutionException("CREATE SCHEMA実行中にエラー", e);
		} finally {
			StatementUtil.close(userStmt);
			StatementUtil.close(createUserStmt);
			ConnectionUtil.close(conn);
		}
    }

    @Override
    public TypeMapper getTypeMapper() {
        return null;
    }
    
    @Override
    public String normalizeSchemaName(String schemaName) {
        return StringUtils.upperCase(schemaName);
    }
    
    @Override
    public String normalizeTableName(String tableName) {
        return StringUtils.upperCase(tableName);
    }
    
    @Override
    public String normalizeColumnName(String colmunName) {
        return StringUtils.upperCase(colmunName);
    }

    /**
     * ビュー定義を検索するSQLを返却する。
     * @return ビュー定義を検索するSQL文
     */
    @Override
    public String getViewDefinitionSql() {
        return "select TEXT as VIEW_DEFINITION from SYSCAT.VIEWS where VIEWNAME=? AND VIEWSCHEMA =?";
    }

    /**
     * シーケンス定義を検索するSQLを返却する。
     * @return シーケンス定義を検索するSQL文
     */
    @Override
    public String getSequenceDefinitionSql() {
        return "select SEQNAME from SYSCAT.SEQUENCES where SEQNAME=? AND OWNER=?";
    }
    
    @Override
    public boolean isUsableType(String type) {
        return USABLE_TYPE_NAMES.contains(type);
    }

}