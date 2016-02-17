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
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

import javax.persistence.GenerationType;

import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.meta.DbTableMeta;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;
import org.seasar.framework.util.StringUtil;

import jp.co.tis.gsp.tools.db.AlternativeGenerator;
import jp.co.tis.gsp.tools.db.TypeMapper;

public abstract class Dialect {

	protected DatabaseMetaData metaData = null;
	protected static final int UN_USABLE_TYPE = -999;

	public abstract void exportSchema(String user, String password, String schema, File dumpFile)
			throws MojoExecutionException;

	public abstract void dropAll(String user, String password, String adminUser,
			String adminPassword, String schema) throws MojoExecutionException;

	public abstract void importSchema(String user, String password, String schema, File dumpFile) throws MojoExecutionException;

	public abstract void createUser(String user, String password, String adminUser,
			String adminPassword) throws MojoExecutionException;
	
	
    protected String url;

    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * ユーザ名とスキーマ名が不一致の場合、別名のスキーマに対して
     * アプリユーザが操作を行えるよう権限を付与する。
     * デフォルトでは何もしない。
     * @param conn DBコネクション
     * @param schema スキーマ名
     * @param user ユーザ名
     * @throws MojoExecutionException エラー
     */
    public void grantAllToAnotherSchema(String schema, String user, String password, String admin, String adminPassword) throws MojoExecutionException {
        // nop
    }

    /**
     * ユーザ名とスキーマ名が不一致の場合、別名のスキーマがもし存在しなければ作成する。
     * デフォルトでは何もしない。
     * @param schema スキーマ名
     * @param user TODO
     * @param password TODO
     * @param admin TODO
     * @param adminPassword TODO
     * @param conn DBコネクション
     * @throws SQLException SQL実行時のエラー
     * @throws UnsupportedOperationException サポートされていない操作を行った時に出るエラー
     */
    public void createSchema(String schema, String user, String password, String admin, String adminPassword)  throws MojoExecutionException {
        // nop
    }

	public abstract TypeMapper getTypeMapper();

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
    
    protected Connection getJDBCConnection(String driver, String user, String password) throws SQLException{
    	DriverManagerUtil.registerDriver(driver);
    	return DriverManager.getConnection(url, user, password);
    }
}