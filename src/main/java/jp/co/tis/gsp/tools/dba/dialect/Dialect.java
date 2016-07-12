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
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

import javax.persistence.GenerationType;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.meta.DbTableMeta;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;
import org.seasar.framework.util.StringUtil;

import jp.co.tis.gsp.tools.db.AlternativeGenerator;
import jp.co.tis.gsp.tools.db.TypeMapper;
import jp.co.tis.gsp.tools.dba.dialect.param.ExportParams;
import jp.co.tis.gsp.tools.dba.dialect.param.ImportParams;
import jp.co.tis.gsp.tools.dba.util.CsvExporter;
import jp.co.tis.gsp.tools.dba.util.CsvLoader;
import jp.co.tis.gsp.tools.dba.util.SqlExecutor;

public abstract class Dialect {
	
	protected enum OBJECT_TYPE {
		FK, TABLE, VIEW, SEQUENCE;
	}

	protected DatabaseMetaData metaData = null;
	protected static final int UN_USABLE_TYPE = -999;
	

    final Charset UTF8 = Charset.forName("UTF-8");
    
    /*** jar内のディレクトリ構造 **/
    protected final String DDL_DIR_NAME = "ddlDirectory";
    protected final String EXTRADDL_DIR_NAME = "extraDdlDirecotry";
    protected final String DATA_DIR_NAME = "dataDirectory";
    
	public void exportSchema(String user, String password, String schema, ExportParams params) throws MojoExecutionException {
       // CSVデータ出力
	    CsvExporter exporter = new CsvExporter(url, driver, schema, user, password,  new File(params.getOutputDirectory(), DATA_DIR_NAME), UTF8);
	    try {
            exporter.execute();
        } catch (Exception e1) {
            new MojoExecutionException("CSVデータ出力処理でエラーが発生しました:", e1);
        }
        
        // DDL & extraDDLの収集
        try {
            FileUtils.copyDirectory(params.getDdlDirectory(), new File(params.getOutputDirectory(), DDL_DIR_NAME));
            FileUtils.copyDirectory(params.getExtraDdlDirectory(), new File(params.getOutputDirectory(), EXTRADDL_DIR_NAME));
        } catch (IOException e) {
            throw new MojoExecutionException("DDLとデータファイルのコピーに失敗しました:", e);
        }
	}
	
	/**
	 * 指定したスキーマ内の全テーブル・ビュー・シーケンスを削除します。
	 * 
	 * 指定したスキーマが存在しない場合はスキーマを作成します。
	 * 
	 * @param user
	 * @param password
	 * @param adminUser
	 * @param adminPassword
	 * @param schema
	 * @throws MojoExecutionException
	 */
	public abstract void dropAll(String user, String password, String adminUser,
			String adminPassword, String schema) throws MojoExecutionException;

	public void importSchema(String user, String password, String schema, ImportParams params) throws MojoExecutionException {
	    
	    File inputDir = params.getInputDirectory();
	    
        // DDLの実行
        SqlExecutor sqlExecutor = new SqlExecutor(schema, user, password, new File(inputDir, DDL_DIR_NAME), new File(inputDir, EXTRADDL_DIR_NAME), 
                params.getDelimiter(), params.getOnError(), params.getLogger());
        try {
            sqlExecutor.execute();
        } catch (SQLException e) {
            throw new MojoExecutionException("DDLの実行に失敗しました:", e);
        }

        // LoadDataの実行
        CsvLoader dataLoader = new CsvLoader(url, driver, schema, user, password, new File(inputDir, DATA_DIR_NAME), UTF8,
                null, params.getOnError(), params.getLogger());
        try {
            dataLoader.execute();
        } catch (Exception e) {
            throw new MojoExecutionException("CSVデータのロード処理で失敗しました:", e);
        }
	}

	/**
	 * ユーザを作成します。
	 * 
	 * 既にユーザが存在する場合はそのユーザを削除します。
	 * 
	 * @param user
	 * @param password
	 * @param adminUser
	 * @param adminPassword
	 * @throws MojoExecutionException
	 */
	public abstract void createUser(String user, String password, String adminUser,
			String adminPassword) throws MojoExecutionException;
	
	
    protected String url;
    protected String driver;

    public void setUrl(String url) {
        this.url = url;
    }
    
	public void setDriver(String driver) {
		DriverManagerUtil.registerDriver(driver);
		this.driver = driver;
	}

	public abstract TypeMapper getTypeMapper();
	
    public String normalizeUserName(String userName) {
        return userName;
    }

	public String normalizeSchemaName(String schemaName) {
		return schemaName;
	}

	public String normalizeTableName(String tableName) {
		return tableName;
	}

	public String normalizeColumnName(String colmunName) {
		return colmunName;
	}

    public GenerationType getGenerationType() {
        return GenerationType.IDENTITY;
    }

	public List<AlternativeGenerator> getAlternativeGenerators() {
		return Collections.emptyList();
	}

	public String getViewDefinitionSql() {
		return null;
	}

    public String getSequenceDefinitionSql() {
        return null;
    }

    public boolean canPrintTable() {
        return true;
    }

    public boolean canPrintIndex() {
        return true;
    }

    public boolean canPrintForeignKey() {
        return true;
    }

    public boolean canPrintView() {
        return true;
    }

    /**
     * 指定されたスキーマの指定されたテーブルの指定されたカラムの型に合うsqlTypeを返す。
     * @return sqlType
     */
    public int guessType(Connection conn, String schema, String tableName, String colName) throws SQLException {
        if (metaData == null) {
            metaData = conn.getMetaData();
        }

        ResultSet rs = null;
        try {
            rs = metaData.getColumns(null,
            		                            schema,
                                                normalizeTableName(tableName),
                                                normalizeColumnName(colName));
            if (!rs.next()) {
                throw new SQLException(tableName + "に" + colName + "を見つけられません。");
            }

            String type = rs.getString("TYPE_NAME");
            if (!isUsableType(type)) {
                System.err.println(type + "型はサポートしていません。");
                return UN_USABLE_TYPE;
            }
            return rs.getInt("DATA_TYPE");
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    /**
     * データ型をサポートするか。
     */
    public boolean isUsableType(String type) {
        return true;
    }

    /**
     * stmtの指定されたインデックスに指定された値をセットする。
     * @param stmt I/O
     * @param parameterIndex parameter index
     * @param value set value
     * @param sqlType sql type
     * @throws SQLException error
     */
    public void setObjectInStmt(PreparedStatement stmt, int parameterIndex, String value, int sqlType) throws SQLException {
        if(sqlType == UN_USABLE_TYPE) {
            stmt.setNull(parameterIndex, Types.NULL);
        } else if(StringUtil.isBlank(value) || "　".equals(value)) {
            stmt.setNull(parameterIndex, sqlType);
        } else {
            stmt.setObject(parameterIndex, value, sqlType);
        }
    }
    
    /**
     * ViewのDDL定義を取得する。
     * 
     * @param conn コネクション 
     * @param viewName　ビュー名
     * @param tableMeta ビュー定義のメタデータ
     * @return ViewのDDL
     * @throws SQLException SQL例外 
     */
    public String getViewDefinition(Connection conn, String viewName, DbTableMeta tableMeta) throws SQLException {
        String sql = getViewDefinitionSql();
        if (sql == null) {
            return null;
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        int idx = 1;
        
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(idx++, viewName);
            stmt.setString(idx++, tableMeta.getSchemaName());	
            
            rs = stmt.executeQuery();
            while(rs.next()) {
                return rs.getString(1);
            }
        } finally {
            ResultSetUtil.close(rs);
            StatementUtil.close(stmt);
        }
        return null;
    }

    protected void dropObjectsInSchema(Connection conn, String dropListSql, String schema, OBJECT_TYPE objType) throws SQLException {
    	Statement stmt = null;
    	ResultSet rs = null;
    	
    	try {
    	  stmt = conn.createStatement();
    	  rs = stmt.executeQuery(dropListSql);
    	  String dropSql = "";
    	  
    	  while (rs.next()) {
      	      switch (objType) {
		        case FK: // 外部キー
  		        	dropSql = "ALTER TABLE " + schema + "." + rs.getString(1) + " DROP CONSTRAINT " + rs.getString(2);
          		  break;
  		        case TABLE: // テーブル
  		        	dropSql = "DROP TABLE "  + schema + "." + rs.getString(1);
    		      break;
  		        case VIEW: // ビュー
  		        	dropSql = "DROP VIEW "  + schema + "." + rs.getString(1);
  			      break;
  		        case SEQUENCE: // シーケンス
  		        	dropSql = "DROP SEQUENCE "  + schema + "." + rs.getString(1);
          		  break;
  		      }
      	    
      	    stmt = conn.createStatement();
      	    System.err.println(dropSql);
      	    stmt.execute(dropSql);
    	  }
        } finally {
            StatementUtil.close(stmt);
        }
    }
    
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
  		        	grantSql = "GRANT ALL ON "  + schema + "." + rs.getString(1) + " TO " + user;
    		      break;
  		        case VIEW: // ビュー
  		        	grantSql = "GRANT ALL ON "  + schema + "." + rs.getString(1) + " TO " + user;
  			      break;
  		        case SEQUENCE: // シーケンス
  		        	grantSql = "GRANT ALL ON "  + schema + "." + rs.getString(1) + " TO " + user;
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
}