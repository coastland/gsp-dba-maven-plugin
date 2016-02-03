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

import jp.co.tis.gsp.tools.db.EntityDependencyParser;
import jp.co.tis.gsp.tools.db.TypeMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.StatementUtil;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SqlserverDialect extends Dialect {
    private String url;
    private String schema;
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final List<String> USABLE_TYPE_NAMES = new ArrayList<String>();

    static {
        USABLE_TYPE_NAMES.add("bigint");
        USABLE_TYPE_NAMES.add("binary");
        USABLE_TYPE_NAMES.add("bit");
        USABLE_TYPE_NAMES.add("char");
        USABLE_TYPE_NAMES.add("date");
        USABLE_TYPE_NAMES.add("datetime");
        USABLE_TYPE_NAMES.add("datetime2");
        USABLE_TYPE_NAMES.add("datetimeoffset");
        USABLE_TYPE_NAMES.add("decimal");
        USABLE_TYPE_NAMES.add("real");
        USABLE_TYPE_NAMES.add("hierarchyid");
        USABLE_TYPE_NAMES.add("image");
        USABLE_TYPE_NAMES.add("int");
        USABLE_TYPE_NAMES.add("money");
        USABLE_TYPE_NAMES.add("nchar");
        USABLE_TYPE_NAMES.add("ntext");
        USABLE_TYPE_NAMES.add("numeric");
        USABLE_TYPE_NAMES.add("nvarchar");
        USABLE_TYPE_NAMES.add("real");
        USABLE_TYPE_NAMES.add("smalldatetime");
        USABLE_TYPE_NAMES.add("smallint");
        USABLE_TYPE_NAMES.add("smallmoney");
        USABLE_TYPE_NAMES.add("text");
        USABLE_TYPE_NAMES.add("time");
        USABLE_TYPE_NAMES.add("tinyint");
        USABLE_TYPE_NAMES.add("uniqueidentifier");
        USABLE_TYPE_NAMES.add("varbinary");
        USABLE_TYPE_NAMES.add("varchar");
        USABLE_TYPE_NAMES.add("int identity");
    }
    
    public SqlserverDialect() {
        GenDialectRegistry.deregister(
                org.seasar.extension.jdbc.dialect.MssqlDialect.class
        );
        GenDialectRegistry.register(
                org.seasar.extension.jdbc.dialect.MssqlDialect.class,
                new ExtendedMssqlGenDialect()
        );
    }
    
    /**
     * SqlServer2008ではdmpファイルのエクスポートがサポートされていないため実装しない。
     */
    @Override
    public void exportSchema(String user, String password, String schema, File dumpFile) throws MojoExecutionException {
        throw new UnsupportedOperationException("Sqlserverを用いたexport-schemaはサポートしていません。");
    }

    @Override
    public void dropAll(String user, String password, String adminUser,
            String adminPassword, String schema) throws MojoExecutionException {
        this.schema = schema;
        DriverManagerUtil.registerDriver(DRIVER);
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement dropStmt;
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            stmt = conn.createStatement();
            if (!existsSchema(conn, schema)) {
                return;
            }
            
            // 依存関係を考慮し削除するテーブルをソートする
            EntityDependencyParser parser = new EntityDependencyParser();
            parser.parse(conn, url, schema);
            final List<String> tableList = parser.getTableList();
            Collections.reverse(tableList);
            for (String table : tableList) {
                dropObject(conn, "TABLE", table);
            }
            // ↑で削除したテーブル以外のオブジェクトを削除する
            dropStmt = conn.prepareStatement("SELECT name, type_desc FROM sys.objects WHERE schema_id = SCHEMA_ID('" + schema + "')");
            ResultSet rs = dropStmt.executeQuery();
            while (rs.next()) {
                if (!tableList.contains(rs.getString("name"))) {
                    String objectType = getObjectType(rs.getString("type_desc"));
                    if (objectType != null) {  
                        dropObject(conn, objectType, rs.getString("name"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new MojoExecutionException("データ削除中にエラー", e);
        } finally {
            StatementUtil.close(stmt);
            ConnectionUtil.close(conn);
        }
    }

    /**
     * SqlServer2008ではdmpファイルのインポートがサポートされていないため実装しない。
     */
    @Override
    public void importSchema(String user, String password, String schema, File dumpFile) throws MojoExecutionException {
        throw new UnsupportedOperationException("Sqlserverを用いたimport-schemaはサポートしていません。");
    }

    @Override
    public void createUser(String user, String password, String adminUser,
            String adminPassword) throws MojoExecutionException {
        DriverManagerUtil.registerDriver(DRIVER);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            stmt = conn.createStatement();
            if (!existsSchema(conn, schema)) {
                stmt.execute("CREATE SCHEMA " + schema);
            }
            if(!existsUser(adminUser, adminPassword, user)) {
                stmt.execute("CREATE LOGIN " + user + " WITH PASSWORD = '" + password + "'");
                stmt.execute("CREATE USER " + user + " FOR LOGIN " + user + " WITH DEFAULT_SCHEMA = " + schema);
                stmt.execute("sp_addrolemember 'db_ddladmin','" + user + "'");
            }
            // dbo, sys, INFORMATION_SCHEMAのスキーマ権限は変更できないためここでスキーマごと権限移譲はしない。
            if (!StringUtils.equalsIgnoreCase(schema, "dbo")
                    && !StringUtils.equalsIgnoreCase(schema, "sys")
                    && !StringUtils.equalsIgnoreCase(schema, "INFORMATION_SCHEMA")) {
                stmt.execute("ALTER AUTHORIZATION ON SCHEMA::" + schema + " TO " + user);
            }
        } catch (SQLException e) {
            throw new MojoExecutionException("CREATE USER実行中にエラー", e);
        } finally {
            StatementUtil.close(stmt);
            ConnectionUtil.close(conn);
        }
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public TypeMapper getTypeMapper() {
        return null;
    }

    @Override
    public boolean isUsableType(String type) {
        return USABLE_TYPE_NAMES.contains(type);
    }

    /**
     * ビュー定義を検索するSQLを返却する。
     * @return ビュー定義を検索するSQL文
     */
    @Override
    public String getViewDefinitionSql() {
        return "SELECT definition AS VIEW_DEFINITION FROM sys.sql_modules WHERE object_id = OBJECT_ID(?)";
    }

    private boolean existsUser(String adminUser, String adminPassword, String user) throws SQLException {
        DriverManagerUtil.registerDriver(DRIVER);
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean existLogin = false;
        boolean existUser = false;
        try {
            conn = DriverManager.getConnection(getUrlReplaceDatabaseName("master"), adminUser, adminPassword);
            stmt = conn.prepareStatement("SELECT COUNT(*) AS num FROM syslogins WHERE name = ?");
            stmt.setString(1, user);
            existLogin = exists(stmt.executeQuery());
            ConnectionUtil.close(conn);
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            stmt = conn.prepareStatement("SELECT COUNT(*) AS num FROM sysusers WHERE name = ?");
            stmt.setString(1, user);
            existUser = exists(stmt.executeQuery());
        } finally {
            StatementUtil.close(stmt);
            ConnectionUtil.close(conn);
        }
        return (existLogin && existUser);
    }

    private boolean exists(ResultSet rs) throws SQLException {
        rs.next();
        return rs.getInt("num") > 0;
    }

    private boolean existsSchema(Connection conn, String schema) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT SCHEMA_ID('" + schema + "') as id");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.getInt("id");
            return (!rs.wasNull());
        } finally {
            StatementUtil.close(stmt);
        }
    }

    /**
     * ユーザ名とスキーマ名が不一致の時、そのスキーマ内の全オブジェクトに対して
     * オブジェクト権限を付与する。
     * SQL Serverは自分で新規作成したスキーマならスキーマごど権限を任意のユーザに委譲できるが、
     * dbo(ユーザ新規作成時のデフォルトスキーマ)などの特殊なスキーマだけは
     * スキーマの権限移譲ができないため、このように対応する必要があった。
     * @param conn DBコネクション
     * @param schema スキーマ名
     * @param user ユーザ名
     * @throws SQLException SQL実行時のエラー
     */
    @Override
    public void grantAllToAnotherSchema(Connection conn, String schema, String user)
            throws SQLException {
        PreparedStatement stmt =
                conn.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=?");
        stmt.setString(1, schema);
        ResultSet rs = stmt.executeQuery();
        StringBuilder sb = new StringBuilder();
        Statement grantStmt = conn.createStatement();
        while (rs.next()) {
            sb.append(schema).append(".").append(rs.getString("TABLE_NAME")).append(",");
            grantStmt.execute("GRANT ALL ON " + schema + "." + rs.getString("TABLE_NAME") + " TO " + user);
        }
        StatementUtil.close(stmt);
    }

    private String getUrlReplaceDatabaseName(String newDatabaseName) {
        String[] properties = url.split(";");
        String newUrl = properties[0];
        boolean isFirst = true;
        for (String property : properties) {
            if (!isFirst) {
                String propertyName = property.split("=")[0];
                if (propertyName.contains("database")) {
                    newUrl += ";database=" + newDatabaseName;
                } else {
                    newUrl += ";" + property;
                }
            } else {
                isFirst = false;
            }
        }
        return newUrl + ";";
    }

    private void dropObject(Connection conn, String objectType, String objectName) throws SQLException {
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

    private String getObjectType(String type_desc) {
        if ("USER_TABLE".equals(type_desc)) {
            return "TABLE";
        } else if ("VIEW".equals(type_desc)) {
            return "VIEW"; 
        }
        return null;
    }
}