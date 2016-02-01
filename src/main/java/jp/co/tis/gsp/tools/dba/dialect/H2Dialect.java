package jp.co.tis.gsp.tools.dba.dialect;

import jp.co.tis.gsp.tools.db.TypeMapper;
import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.StatementUtil;

import java.io.File;
import java.sql.*;

public class H2Dialect extends Dialect {
    private String url;
    private static final String DRIVER = "org.h2.Driver";

    public H2Dialect(){
        GenDialectRegistry.deregister(
                org.seasar.extension.jdbc.dialect.H2Dialect.class
        );
        GenDialectRegistry.register(
                org.seasar.extension.jdbc.dialect.H2Dialect.class,
                new ExtendedH2GenDialect()
        );
    }

    @Override
    public void exportSchema(String user, String password, String schema,
            File dumpFile) throws MojoExecutionException {
        DriverManagerUtil.registerDriver(DRIVER);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            stmt.execute("SCRIPT TO '" + dumpFile.getAbsolutePath()+ "'");
            StatementUtil.close(stmt);
        } catch (SQLException e) {
            throw new MojoExecutionException("Schema export実行中にエラー", e);
        } finally {
            ConnectionUtil.close(conn);
        }
    }

    @Override
    public void dropAll(String user, String password, String adminUser,
            String adminPassword, String schema) throws MojoExecutionException {
        DriverManagerUtil.registerDriver(DRIVER);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            stmt = conn.createStatement();
            stmt.execute("DROP ALL OBJECTS");
        } catch (SQLException e) {
            throw new MojoExecutionException("DROP ALL OBJECTS 実行中にエラー", e);
        } finally {
            StatementUtil.close(stmt);
            ConnectionUtil.close(conn);
        }
    }

    @Override
    public void importSchema(String user, String password, String schema,
            File dumpFile) throws MojoExecutionException {
        DriverManagerUtil.registerDriver(DRIVER);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            stmt.execute("RUNSCRIPT FROM '" + dumpFile.getAbsolutePath()+ "'");
            StatementUtil.close(stmt);
        } catch (SQLException e) {
            throw new MojoExecutionException("Schema import実行中にエラー", e);
        } finally {
            ConnectionUtil.close(conn);
        }
    }

    @Override
    public void createUser(String user, String password, String adminUser,
            String adminPassword) throws MojoExecutionException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            stmt = conn.createStatement();
            stmt.execute("CREATE USER " + user + " PASSWORD '" + password + "'");
//            stmt.execute("ALTER USER " + user + " ADMIN TRUE");
        } catch (SQLException e) {
            throw new MojoExecutionException("CREATE USER 実行中にエラー: ", e);
        } finally {
            StatementUtil.close(stmt);
            ConnectionUtil.close(conn);
        }
    }

    @Override
    public void grantAllToAnotherSchema(Connection conn, String schema, String user) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=?");
        pstmt.setString(1, schema);
        ResultSet rs = pstmt.executeQuery();
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            sb.append(schema).append(".").append(rs.getString("TABLE_NAME")).append(",");
        }
        int stringLength = sb.length();
        // 最後尾にもカンマがついてしまうので手動で消す
        String tables = sb.toString().substring(0, stringLength - 1);

        // PUBLICスキーマはデフォルトスキーマなので何もする必要がない
        if ("PUBLIC".equals(schema)) return;
        Statement stmt = conn.createStatement();

        stmt.execute("GRANT ALL ON " + tables + " TO " + user);
        StatementUtil.close(stmt);
    }

    @Override
    public void createSchemaIfNotExist(Connection conn, String schema) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schema);
        StatementUtil.close(stmt);
    }

    @Override
    public void setUrl(String url) {
        this.url = url;

    }

    @Override
    public TypeMapper getTypeMapper() {
        return new TypeMapper();
    }

}
