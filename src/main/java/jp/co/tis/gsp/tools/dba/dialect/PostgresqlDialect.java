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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.tis.gsp.tools.db.TypeMapper;
import jp.co.tis.gsp.tools.dba.util.ProcessUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.dialect.GenDialect;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.FileOutputStreamUtil;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;

public class PostgresqlDialect extends Dialect {
    private String url;
    private String schema;
    private static final String DRIVER = "org.postgresql.Driver";
    private static final List<String> USABLE_TYPE_NAMES = new ArrayList<String>();
    
    static {
        USABLE_TYPE_NAMES.add("int8");
        USABLE_TYPE_NAMES.add("bigserial");
        USABLE_TYPE_NAMES.add("bool");
        USABLE_TYPE_NAMES.add("bpchar");
        USABLE_TYPE_NAMES.add("date");
        USABLE_TYPE_NAMES.add("float8");
        USABLE_TYPE_NAMES.add("int4");
        USABLE_TYPE_NAMES.add("numeric");
        USABLE_TYPE_NAMES.add("float4");
        USABLE_TYPE_NAMES.add("serial");
        USABLE_TYPE_NAMES.add("int2");
        USABLE_TYPE_NAMES.add("text");
        USABLE_TYPE_NAMES.add("timestamp");
        USABLE_TYPE_NAMES.add("varchar");
    }
    
    public PostgresqlDialect() {
        GenDialectRegistry.deregister(
                org.seasar.extension.jdbc.dialect.PostgreDialect.class
        );
        GenDialectRegistry.register(
                org.seasar.extension.jdbc.dialect.PostgreDialect.class,
                new ExtendedPostgreGenDialect()
        );
    }

    @Override
    public void exportSchema(String user, String password, String schema,
            File dumpFile) throws MojoExecutionException {
        BufferedInputStream in = null;
        FileOutputStream out = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "pg_dump",
                    "--host=" + getHost(),
                    "--port=" + getPort(),
                    "--username=" + user,
                    "--schema=" + schema,
                    getDatabase()
                    );
            pb.redirectErrorStream(true);
            if (StringUtils.isNotEmpty(password)) {
                // パスワードが設定されている場合は、パスワードを標準入力に書き込む
                pb.environment()
                        .put("PGPASSWORD", password);
            }

            Process process = pb.start();
            in = new BufferedInputStream(process.getInputStream());

            out = FileOutputStreamUtil.create(dumpFile);
            byte[] buf = new byte[4096];
            while(true) {
                int res = in.read(buf);
                if (res <= 0) break;
                out.write(buf, 0, res);
            }

        } catch (IOException e) {
            throw new MojoExecutionException("スキーマエクスポート実行中にエラー", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    @Override
    public void dropAll(String user, String password, String adminUser,
            String adminPassword, String schema) throws MojoExecutionException {
        this.schema = schema;
        DriverManagerUtil.registerDriver(DRIVER);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            stmt = conn.createStatement();
            try {
                stmt.execute("DROP SCHEMA " + schema + " CASCADE");
            } catch(SQLException ignore) {
                // DROP SCHEMAに失敗しても気にしない
            }
            stmt.execute("CREATE SCHEMA " + schema);
            
        } catch (SQLException e) {
            throw new MojoExecutionException("データ削除中にエラー", e);
        } finally {
            ConnectionUtil.close(conn);
            StatementUtil.close(stmt);
        }
    }

    @Override
    public void importSchema(String user, String password, String schema,
            File dumpFile) throws MojoExecutionException {

        Map<String, String> environment = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(password)) {
            environment.put("PGPASSWORD", password);
        }
        try {
            ProcessUtil.execWithInput(dumpFile,
                    environment,
                    "psql",
                    "--host", getHost(),
                    "--port", getPort(),
                    "--username", user,
                    getDatabase()
            );
        } catch (IOException e) {
            throw new MojoExecutionException("スキーマインポート実行中にエラー", e);
        }
    }

    @Override
    public void createUser(String user, String password, String adminUser,
            String adminPassword) throws MojoExecutionException {
        DriverManagerUtil.registerDriver(DRIVER);
        Connection conn = null;
        Statement stmt = null;
        String database = getDatabase();
        String role = StringUtils.lowerCase(user);
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            stmt = conn.createStatement();
            if (!existsUser(conn, role)) {
                try {
                    stmt.execute("DROP OWNED BY " + role + " CASCADE");
                    stmt.execute("DROP ROLE " + role);
                } catch(SQLException ignore) {
                    // DROP USERに失敗しても気にしない
                }
                stmt.execute("CREATE ROLE " + role + " LOGIN PASSWORD \'" + password + "\'");
                stmt.execute("GRANT CREATE, CONNECT ON DATABASE " + database + " TO " + role);
            }
            stmt.execute("ALTER SCHEMA " + schema + " OWNER TO " + role);
            stmt.execute("ALTER USER " + role + " Set search_path TO " + schema);
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
    public String normalizeSchemaName(String schemaName) {
        return StringUtils.lowerCase(schemaName);
    }

    @Override
    public String normalizeTableName(String tableName) {
        return StringUtils.lowerCase(tableName);
    }

    @Override
    public String normalizeColumnName(String colmunName) {
        return StringUtils.lowerCase(colmunName);
    }

    @Override
    public String getViewDefinitionSql() {
        return "SELECT definition AS view_definition FROM pg_views WHERE viewname=? and schemaname=?";
    }

    @Override
    public String getSequenceDefinitionSql() {
        return "SELECT relname FROM pg_statio_user_sequences WHERE relname=? and schemaname=?";
    }

    @Override
    public boolean isUsableType(String type) {
        return USABLE_TYPE_NAMES.contains(type);
    }

    private String getHost() {
        return this.url.split("/")[2].split(":")[0];
    }

    private String getPort() {
        return this.url.split("/")[2].split(":")[1];
    }

    private String getDatabase() {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private boolean existsUser(Connection conn, String user) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt =
                conn.prepareStatement("SELECT count(*) AS num FROM pg_user WHERE usename=?");
            stmt.setString(1, user);
            rs = stmt.executeQuery();
            rs.next();
            return (rs.getInt("num") > 0);
        } finally {
            ResultSetUtil.close(rs);
            StatementUtil.close(stmt);
        }
    }
}
