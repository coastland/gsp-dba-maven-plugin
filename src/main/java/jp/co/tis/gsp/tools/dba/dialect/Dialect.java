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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.GenerationType;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.meta.DbTableMeta;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.OutputStreamUtil;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;
import org.seasar.framework.util.StringUtil;

import com.csvreader.CsvWriter;

import jp.co.tis.gsp.tools.db.AlternativeGenerator;
import jp.co.tis.gsp.tools.db.TypeMapper;
import jp.co.tis.gsp.tools.dba.dialect.param.ExportParams;
import jp.co.tis.gsp.tools.dba.dialect.param.ImportParams;
import jp.co.tis.gsp.tools.dba.util.CsvDataLoader;
import jp.co.tis.gsp.tools.dba.util.DialectUtil;
import jp.co.tis.gsp.tools.dba.util.SqlExecutor;

public abstract class Dialect {
	
	protected enum OBJECT_TYPE {
		FK, TABLE, VIEW, SEQUENCE;
	}

	protected DatabaseMetaData metaData = null;
	protected static final int UN_USABLE_TYPE = -999;
	
    protected final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    protected final SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    final Charset UTF8 = Charset.forName("UTF-8");
    
    /*** jar内のディレクトリ構造 **/
    protected final String DDL_DIR_NAME = "ddlDirectory";
    protected final String EXTRADDL_DIR_NAME = "extraDdlDirecotry";
    protected final String DATA_DIR_NAME = "dataDirectory";
    
	public void exportSchema(String user, String password, String schema, ExportParams params) throws MojoExecutionException {
	    
	    // 汎用タイプのエクスポート処理
	    exportSchemaGeneral(user, password, schema, params);
	}
	
	protected void exportSchemaGeneral(String user, String password, String schema, ExportParams params) throws MojoExecutionException {
	       // CSVデータ出力
        exportCsv(user, password, schema, new File(params.getOutputDirectory(), DATA_DIR_NAME), UTF8);
        
        // DDL & extraDDLの収集
        try {
            FileUtils.copyDirectory(params.getDdlDirectory(), new File(params.getOutputDirectory(), DDL_DIR_NAME));
            FileUtils.copyDirectory(params.getExtraDdlDirectory(), new File(params.getOutputDirectory(), EXTRADDL_DIR_NAME));
        } catch (IOException e) {
            throw new MojoExecutionException("DDLとデータファイルのコピーに失敗しました。", e);
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
	    
	    // 汎用タイプのエクスポートデータのインポート
	    importSchemaGeneral(user, password, schema, params);
	}

	protected void importSchemaGeneral(String user, String password, String schema, ImportParams params) throws MojoExecutionException {
	       File inputDir = params.getInputDirectory();
	        
	        // DDLの実行
	        SqlExecutor sqlExecutor = new SqlExecutor(schema, user, password, new File(inputDir, DDL_DIR_NAME), new File(inputDir, EXTRADDL_DIR_NAME), 
	                params.getDelimiter(), params.getOnError(), params.getLogger());
	        sqlExecutor.execute();
	        
	        // LoadDataの実行
	        CsvDataLoader dataLoader = new CsvDataLoader(url, driver, schema, user, password, new File(inputDir, DATA_DIR_NAME), UTF8,
	                null, params.getOnError(), params.getLogger());
	        dataLoader.execute();
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
    
    /**
     * 指定スキーマのテーブルデータをcsvで出力します。
     * 
     * @param adminUser
     * @param adminPassword
     * @param schema
     * @param outputDir
     * @param charset
     * @throws MojoExecutionException
     */
    public void exportCsv(String adminUser, String adminPassword, String schema, File outputDir, Charset charset) 
            throws MojoExecutionException {
        DriverManagerUtil.registerDriver(driver);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            String normalizeSchemaName = normalizeSchemaName(schema);
        
            final List<String> tableList = getTableNames(conn.getMetaData(), normalizeSchemaName,
                    new String[] { "TABLE" });

            for (String tableName : tableList) {
                writeToCSV(conn, normalizeSchemaName, tableName, new File(outputDir, tableName + ".csv"), charset);
            }

        } catch (SQLException e) {
            throw new MojoExecutionException("DBに接続できませんでした。driverおよびurlの設定が正しいか確認してください", e);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("CSVファイルの出力中にエラーが発生しました。", e);
        } catch (IOException e) {
            throw new MojoExecutionException("CSVファイルの出力中にエラーが発生しました。", e);
        } finally {
            ConnectionUtil.close(conn);
        }
    }

    /**
     * 指定スキーマに属するテーブル名のリストを取得します。
     * 
     * @param metaData
     * @param normalizedSchemaName
     * @param types
     * @return
     * @throws SQLException
     */
    public List<String> getTableNames(DatabaseMetaData metaData, String normalizedSchemaName, String[] types)
            throws SQLException {
        List<String> allNames = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = metaData.getTables(null, normalizedSchemaName, "%", types);
            while (rs.next()) {
                allNames.add(rs.getString("TABLE_NAME"));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return allNames;
    }

    /**
     * SELECT文の結果をCSVファイルにカラム名ヘッダー付きで出力します。
     * 
     * @param metaData
     * @param resultSet
     * @param csvFile
     * @param charset
     * @throws SQLException
     * @throws IOException
     */
    public void writeToCSV(Connection conn, String schema, String tableName, File csvFile, Charset charset)
            throws SQLException, IOException {

        Dialect dialect = DialectUtil.getDialect();
        Statement stmt = null;
        ResultSet resultSet = null;

        OutputStream out = new FileOutputStream(csvFile);
        String[] columnHeaders;
        String[] values = null;
        CsvWriter csvWriter = null;

        try {
            stmt = conn.createStatement();
            resultSet = stmt
                    .executeQuery("SELECT * FROM " + tableName + getOrderString(conn.getMetaData(), schema, tableName));
            ResultSetMetaData meta = resultSet.getMetaData();

            csvWriter = new CsvWriter(out, ',', charset);
            csvWriter.setForceQualifier(true);
            csvWriter.setRecordDelimiter('\n');

            // カラムヘッダーの出力
            int columnCount = meta.getColumnCount();
            columnHeaders = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnHeaders[i - 1] = meta.getColumnName(i);
            }
            csvWriter.writeRecord(columnHeaders);

            // データの出力
            while (resultSet.next()) {
                values = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    resultSet.getString(i);
                    int sqlType = dialect.guessType(conn, schema, tableName, meta.getColumnName(i));
                    values[i - 1] = convert(resultSet, i, sqlType);
                }
                csvWriter.writeRecord(values);
            }
        } finally {
            csvWriter.flush();
            csvWriter.close();

            ResultSetUtil.close(resultSet);
            StatementUtil.close(stmt);
            OutputStreamUtil.close(out);
        }
    }

    /**
     * SQLのタイプに応じて値の書式を変換します。
     * 
     * @param resultSet
     * @param meta
     * @param index
     * @return
     * @throws SQLException
     */
    protected String convert(ResultSet resultSet, int index, int sqlType) throws SQLException {

        String value = "";
        switch (sqlType) {
        case Types.DATE:
            Date date = resultSet.getDate(index);
            if (date != null) {
                value = sdfDate.format(date);
            }
            break;
        case Types.TIMESTAMP:
            Timestamp timestamp = resultSet.getTimestamp(index);
            if (timestamp != null) {
                value = sdfTimestamp.format(timestamp);
            }
            break;
        default:
            value = resultSet.getString(index);
            break;
        }

        return value;
    }

    /**
     * 主キー項目でソートをかけるorderBy文字列を生成します。
     * 
     * @param meta
     * @param tableName
     * @return
     * @throws SQLException
     */
    protected String getOrderString(DatabaseMetaData meta, String schema, String tableName) throws SQLException {
        String orderByString = "";
        ResultSet rs = null;
        Map<Short, String> keyMap = new TreeMap<Short, String>();

        try {
            rs = meta.getPrimaryKeys(null, schema, tableName);
            while (rs.next()) {
                keyMap.put(rs.getShort("KEY_SEQ"), rs.getString("COLUMN_NAME"));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        Set<Short> keySet = keyMap.keySet();

        if (keySet.size() > 0) {
            orderByString = " ORDER BY";
            for (Short key : keySet) {
                String preStr = key.shortValue() > 1 ? "," : " ";
                orderByString += (preStr + keyMap.get(key));
            }
        }

        return orderByString;
    }


}