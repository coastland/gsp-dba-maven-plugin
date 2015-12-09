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

import jp.co.tis.gsp.tools.db.AlternativeGenerator;
import jp.co.tis.gsp.tools.db.TypeMapper;

import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StringUtil;

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

	public abstract void setUrl(String url);

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
        try (ResultSet rs = metaData.getColumns(null,
                                                normalizeSchemaName(schema),
                                                normalizeTableName(tableName),
                                                normalizeColumnName(colName))) {
            if (!rs.next()) {
                throw new SQLException(tableName + "に" + colName + "を見つけられません。");
            }

            String type = rs.getString("TYPE_NAME");
            if (!isUsableType(type)) {
                System.err.println(type + "型はサポートしていません。");
                return UN_USABLE_TYPE;
            }
            return rs.getInt("DATA_TYPE");
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
}