package jp.co.tis.gsp.tools.dba.dialect;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.StatementUtil;

import jp.co.tis.gsp.tools.db.TypeMapper;

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
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
        	conn = DriverManager.getConnection(url, adminUser, adminPassword);
        	
        	// 指定スキーマが存在しない場合は作成
			if(!existsSchema(conn, normalizeSchemaName(schema))) {
				createSchema(schema, user, password, adminUser, adminPassword);
		        // スキーマ生成
				return;
			}

			// スキーマ内のテーブル、ビュー、シーケンス削除
			String nmzschema = normalizeSchemaName(schema);
			String dropListSql = "SELECT TABLE_NAME, CONSTRAINT_NAME FROM INFORMATION_SCHEMA.CONSTRAINTS WHERE CONSTRAINT_SCHEMA='" + nmzschema + "'";
			dropObjectsInSchema(conn, dropListSql, nmzschema, OBJECT_TYPE.FK);
			
			dropListSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA='" + nmzschema + "'"; 
			dropObjectsInSchema(conn, dropListSql, nmzschema, OBJECT_TYPE.VIEW);
			
			dropListSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='" + nmzschema + "'"; 
	        dropObjectsInSchema(conn, dropListSql, nmzschema, OBJECT_TYPE.TABLE);
			
			dropListSql = "SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE IS_GENERATED=FALSE AND SEQUENCE_SCHEMA='" + nmzschema + "'";
	        dropObjectsInSchema(conn, dropListSql, nmzschema, OBJECT_TYPE.SEQUENCE);
            
        } catch (SQLException e) {
            throw new MojoExecutionException("DROP ALL実行中にエラー", e);
        } finally {
            StatementUtil.close(pstmt);
            ConnectionUtil.close(conn);
        }
    }
    
    private boolean existsSchema(Connection conn, String schema) throws SQLException {
        PreparedStatement pstmt = null;
        
        try{
          pstmt = conn.prepareStatement("SELECT COUNT(SCHEMA_NAME) AS num FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME=?");
          pstmt.setString(1, StringUtils.upperCase(schema));
          ResultSet rs = pstmt.executeQuery();
          rs.next();
          return (rs.getInt("num") > 0);
        }finally{
       	  StatementUtil.close(pstmt);
        }
    }   
        
	public String normalizeSchemaName(String schemaName) {
		return StringUtils.upperCase(schemaName);
	}

    @Override
    public void importSchema(String user, String password, String schema,
            File dumpFile) throws MojoExecutionException {
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
        PreparedStatement pstmt = null;
    	
    	try{
    		conn = DriverManager.getConnection(url, admin, adminPassword);

			String nmzschema = normalizeSchemaName(schema);
			
			String grantListSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA='" + nmzschema + "'"; 
			grantSchemaObjToUser(conn, grantListSql, nmzschema, user, OBJECT_TYPE.VIEW);
			
			grantListSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='" + nmzschema + "'"; 
			grantSchemaObjToUser(conn, grantListSql, nmzschema, user, OBJECT_TYPE.TABLE);
        
        } catch (SQLException e) {
            throw new MojoExecutionException("権限付与処理 実行中にエラー: ", e);
        } finally {
        	StatementUtil.close(pstmt);
            ConnectionUtil.close(conn);
        }
    }

    private void createSchema(String schema, String user, String password, String admin, String adminPassword) throws MojoExecutionException {
        Statement stmt = null;
        Connection conn = null;
        
		try {
			conn = DriverManager.getConnection(url, admin, adminPassword);
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
