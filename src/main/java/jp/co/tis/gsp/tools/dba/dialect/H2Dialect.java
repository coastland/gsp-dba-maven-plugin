package jp.co.tis.gsp.tools.dba.dialect;

import jp.co.tis.gsp.tools.db.TypeMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.StatementUtil;

import java.io.File;
import java.sql.*;

public class H2Dialect extends Dialect {
    
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
        DriverManagerUtil.registerDriver(driver);
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
        DriverManagerUtil.registerDriver(driver);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);
            stmt = conn.createStatement();
            stmt.execute("DROP ALL OBJECTS");
            
            createSchema(schema, user, password, adminUser, adminPassword);
            
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
        DriverManagerUtil.registerDriver(driver);
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
            stmt.execute("CREATE USER IF NOT EXISTS " + user + " PASSWORD '" + password + "'" + " ADMIN");
        } catch (SQLException e) {
            throw new MojoExecutionException("CREATE USER 実行中にエラー: ", e);
        } finally {
            StatementUtil.close(stmt);
            ConnectionUtil.close(conn);
        }
    }

    @Override
    public void grantAllToUser(String schema, String user, String password, String admin, String adminPassword) throws MojoExecutionException {
    	
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
    	
    	try{
    		conn = getJDBCConnection(driver, admin, adminPassword);
	        pstmt = conn.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=?");
	        pstmt.setString(1, StringUtils.upperCase(schema));
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
	        stmt = conn.createStatement();
	
	        stmt.execute("GRANT ALL ON " + tables + " TO " + user);
        
        } catch (SQLException e) {
            throw new MojoExecutionException("権限付与処理 実行中にエラー: ", e);
        } finally {
        	StatementUtil.close(pstmt);
            StatementUtil.close(stmt);
            ConnectionUtil.close(conn);
        }
    }

    private void createSchema(String schema, String user, String password, String admin, String adminPassword) throws MojoExecutionException {
        Statement stmt = null;
        Connection conn = null;
        
		try {
			conn = getJDBCConnection(driver, admin, adminPassword);
			stmt = conn.createStatement();
			stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schema);
		} catch (SQLException e) {
			throw new MojoExecutionException("CREATE SCHEMA実行中にエラー", e);
		} finally {
			StatementUtil.close(stmt);
			ConnectionUtil.close(conn);
		}
    }

    @Override
    public TypeMapper getTypeMapper() {
        return new TypeMapper();
    }

}
