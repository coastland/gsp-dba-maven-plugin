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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.StatementUtil;

import jp.co.tis.gsp.tools.db.TypeMapper;

public class Db2Dialect extends Dialect {
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

    @Override
    public void dropAll(String user, String password, String adminUser,
            String adminPassword, String schema) throws MojoExecutionException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            stmt = conn.createStatement();
            
			// 指定スキーマがいなければ作成する。
            if(!existsSchema(conn, schema)) {
            	createSchema(schema, user, password, adminUser, adminPassword);
                return;
            } else {
            	// 指定スキーマが存在する場合はスキーマ操作権限を念のため与えておく
            	stmt.execute("GRANT CREATEIN,ALTERIN,DROPIN ON SCHEMA " + schema + " TO USER " + user);
            }
            
			// スキーマ内のテーブル、ビュー、シーケンス削除
			String nmzschema = normalizeSchemaName(schema);
			String dropListSql = "SELECT TABNAME, CONSTNAME FROM SYSCAT.TABCONST WHERE TYPE='F' AND TABSCHEMA='" + nmzschema + "'";
			dropObjectsInSchema(conn, dropListSql, nmzschema, OBJECT_TYPE.FK);
			
			dropListSql = "SELECT TABNAME FROM SYSCAT.TABLES WHERE OWNERTYPE='U' AND TYPE IN('V') AND TABSCHEMA='" + nmzschema + "'";
			dropObjectsInSchema(conn, dropListSql, nmzschema, OBJECT_TYPE.VIEW);
			
			dropListSql = "SELECT TABNAME FROM SYSCAT.TABLES WHERE OWNERTYPE='U' AND TYPE IN('T') AND TABSCHEMA='" + nmzschema + "'";
	        dropObjectsInSchema(conn, dropListSql, nmzschema, OBJECT_TYPE.TABLE);
			
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

    /**
     * ユーザは先に作成されていることを前提としている。
     * 本処理では、DBへのアクセス権限を付与するだけ。
     */
    @Override
    public void createUser(String user, String password, String adminUser,
            String adminPassword) throws MojoExecutionException {
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
    protected void grantSchemaObjToUser(Connection conn, String grantListSql, String schema, String user, OBJECT_TYPE objType) throws SQLException {
    	Statement stmt = null;
    	ResultSet rs = null;
    	
    	try {
    	  stmt = conn.createStatement();
    	  rs = stmt.executeQuery(grantListSql);
    	  String grantSql = "";
    	  
    	  while (rs.next()) {
      	      switch (objType) {
  		        case TABLE: // テーブル
  		        	grantSql = "GRANT ALL ON "  + schema + "." + rs.getString(1) + " TO USER " + user;
    		      break;
  		        case VIEW: // ビュー
  		        	grantSql = "GRANT ALL ON "  + schema + "." + rs.getString(1) + " TO USER " + user;
  			      break;
         		default:
          		  break;
  		      }
      	    
      	    stmt = conn.createStatement();
      	    System.err.println(grantSql);
      	    stmt.execute(grantSql);
    	  }
        } finally {
            StatementUtil.close(stmt);
        }
    }

    private void createSchema(String schema, String user, String password, String admin, String adminPassword) throws MojoExecutionException {
    	PreparedStatement  userStmt = null;
        Statement createUserStmt = null;
        Connection conn = null;
    	
        try{
			conn = DriverManager.getConnection(url, admin, adminPassword);
			createUserStmt = conn.createStatement();
			createUserStmt.execute("CREATE SCHEMA " + schema + " AUTHORIZATION " + user);
		
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
    public String normalizeUserName(String userName) {
    	return StringUtils.upperCase(userName);
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