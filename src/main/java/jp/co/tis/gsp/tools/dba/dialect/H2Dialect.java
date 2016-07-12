package jp.co.tis.gsp.tools.dba.dialect;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.seasar.extension.jdbc.gen.dialect.GenDialectRegistry;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.StatementUtil;

import jp.co.tis.gsp.tools.db.TypeMapper;
import jp.co.tis.gsp.tools.dba.dialect.param.ExportParams;
import jp.co.tis.gsp.tools.dba.dialect.param.ImportParams;

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
    public void exportSchema(ExportParams params) throws MojoExecutionException {
        Connection conn = null;
        try {
            File dumpFile = params.getDumpFile();
		    String user = params.getUser();
		    String password = params.getPassword();

            conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            stmt.execute("SCRIPT DROP TO '" + dumpFile.getAbsolutePath()+ "'");
            StatementUtil.close(stmt);
        } catch (SQLException e) {
            throw new MojoExecutionException("Schema export実行中にエラー", e);
        } finally {
            ConnectionUtil.close(conn);
        }
    }

    /**
     * GSPではPUBLICスキーマしか対応していないのでスキーマの生成は不要。
     * 
     */
    @Override
    public void dropAll(String user, String password, String adminUser,
            String adminPassword, String schema) throws MojoExecutionException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
        	conn = DriverManager.getConnection(url, adminUser, adminPassword);
        	
			// スキーマ内のテーブル、ビュー、シーケンス削除
			String nmzschema = normalizeSchemaName(schema);
			String dropListSql = "SELECT TABLE_NAME, CONSTRAINT_NAME FROM INFORMATION_SCHEMA.CONSTRAINTS WHERE CONSTRAINT_SCHEMA='" + nmzschema + "'";
			dropObjectsInSchema(conn, dropListSql, nmzschema, OBJECT_TYPE.FK);
			
			dropListSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA='" + nmzschema + "'"; 
			dropObjectsInSchema(conn, dropListSql, nmzschema, OBJECT_TYPE.VIEW);
			
			dropListSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='" + nmzschema + "'"; 
	        dropObjectsInSchema(conn, dropListSql, nmzschema, OBJECT_TYPE.TABLE);
			
        } catch (SQLException e) {
            throw new MojoExecutionException("DROP ALL実行中にエラー", e);
        } finally {
            StatementUtil.close(pstmt);
            ConnectionUtil.close(conn);
        }
    }
    
    @Override
    public String normalizeUserName(String userName) {
    	return StringUtils.upperCase(userName);
    }
        
	public String normalizeSchemaName(String schemaName) {
		return StringUtils.upperCase(schemaName);
	}

    @Override
    public void importSchema(ImportParams params) throws MojoExecutionException {
        Connection conn = null;
        try {
            File dumpFile = params.getDumpFile();
		    String user = params.getUser();
		    String password = params.getPassword();

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
    public TypeMapper getTypeMapper() {
        return new TypeMapper();
    }

}
