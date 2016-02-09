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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

import javax.persistence.GenerationType;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.framework.util.StringUtil;

import jp.co.tis.gsp.tools.db.AlternativeGenerator;
import jp.co.tis.gsp.tools.db.TypeMapper;

public abstract class Dialect {
	
	protected String user;
	protected String password;
	protected String adminUser;
	protected String adminPassword;
	protected String schema;
	protected String url;
	protected String dmpFile;
	protected File outputDirectory;
	protected File inputDirectory;

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public String getDmpFile() {
		return dmpFile;
	}

	public void setDmpFile(String dmpFile) {
		this.dmpFile = dmpFile;
	}
	
	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public File getInputDirectory() {
		return inputDirectory;
	}

	public void setInputDirectory(File inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	protected DatabaseMetaData metaData = null;
	protected static final int UN_USABLE_TYPE = -999;
	
	public abstract File exportSchema() throws MojoExecutionException;

	public abstract void dropAll() throws MojoExecutionException;

	public abstract void importSchema(File dumpFile) throws MojoExecutionException;

	public abstract void createUser() throws MojoExecutionException;

    /**
     * ユーザ名とスキーマ名が不一致の場合、別名のスキーマに対して
     * アプリユーザが操作を行えるよう権限を付与する。
     * デフォルトでは何もしない。
     * @param conn DBコネクション
     * @param schema スキーマ名
     * @param user ユーザ名
     * @throws SQLException SQL実行時のエラー
     * @throws UnsupportedOperationException サポートされていない操作を行った時に出るエラー
     */
    public void grantAllToAnotherSchema(Connection conn)
            throws SQLException, UnsupportedOperationException {
        // nop
    }

    /**
     * ユーザ名とスキーマ名が不一致の場合、別名のスキーマがもし存在しなければ作成する。
     * デフォルトでは何もしない。
     * @param conn DBコネクション
     * @param schema スキーマ名
     * @throws SQLException SQL実行時のエラー
     * @throws UnsupportedOperationException サポートされていない操作を行った時に出るエラー
     */
    public void createSchemaIfNotExist(Connection conn)
            throws SQLException, UnsupportedOperationException {
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
     * Exportで利用するファイルを作成します.
     * 
     * @return　ファイル
     */
    protected File createExportFile(){
		File exportFile = new File(outputDirectory, StringUtils.defaultIfEmpty(dmpFile, schema + ".dmp"));
		return exportFile;
    }
}