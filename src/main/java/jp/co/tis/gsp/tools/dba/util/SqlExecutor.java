package jp.co.tis.gsp.tools.dba.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.sql.SqlSplitter;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.StatementUtil;

public class SqlExecutor {

    private Connection conn;
    
    private String url;
    private String user;
    private String password;

    private File ddlDirectory;
    private File extraDdlDirectory;

    private String delimiter;
    private String onError;

    private int successfulStatements = 0;
    private int totalStatements = 0;
    
    private Log logger;

    public SqlExecutor(String url, String user, String password, File ddlDirectory, File extraDdlDirectory, String delimiter, String onError, Log logger){
        this.url = url;
        this.user = user;
        this.password = password;
        this.ddlDirectory = ddlDirectory;
        this.extraDdlDirectory = extraDdlDirectory;
        this.delimiter = delimiter;
        this.onError = onError;
        this.logger = logger;
    }

    public void execute() throws MojoExecutionException {

        FilenameFilter sqlFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql");
            }
        };

        // 拡張子.sqlが実行対象
        List<File> files = new ArrayList<File>(Arrays.asList(ddlDirectory.listFiles(sqlFileFilter)));
        if (extraDdlDirectory != null && extraDdlDirectory.isDirectory()) {
            Collections.addAll(files, extraDdlDirectory.listFiles(sqlFileFilter));
        }

        // ファイル名 アルファベット順に並べかえる
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });

        try {
            executeBySqlFiles(files.toArray(new File[files.size()]));
        } catch (Exception e) {
            logger.error(e);
            throw new MojoExecutionException("SQL実行中にエラーが発生しました:", e);
        }

        // コネクション解放
        ConnectionUtil.close(conn);

    }

    private void executeSql(String sql) throws SQLException {
        if ("".equals(sql.trim())) {
            return;
        }
        Statement stmt = conn.createStatement();
        try {
            logger.debug("SQL: " + sql);
            totalStatements++;
            stmt.execute(sql);
            successfulStatements++;
        } catch (SQLException ex) {
            throw new SQLException(sql, ex);
        } finally {
            StatementUtil.close(stmt);
        }
    }

    private void runStatements(Reader reader) throws SQLException, IOException {
        BufferedReader in = new BufferedReader(reader);
        StringBuilder sql = new StringBuilder();
        int overflow = SqlSplitter.NO_END;
        String line;
        while ((line = in.readLine()) != null) {
            sql.append("\n").append(line);

            overflow = SqlSplitter.containsSqlEnd(line, delimiter, overflow);
            if (overflow > 0) {
                executeSql(sql.substring(0, sql.length() - delimiter.length()));
                sql.setLength(0);
                overflow = SqlSplitter.NO_END;
            }
        }
        if (sql.length() > 0) {
            executeSql(sql.toString());
        }

    }

    private void executeBySqlFiles(File... sqlFiles) throws SQLException, IOException, MojoExecutionException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(url, user, password);
        }

        successfulStatements = 0;
        totalStatements = 0;
        for (File sqlFile : sqlFiles) {
            Reader reader = null;
            try {
                reader = new FileReader(sqlFile);
                runStatements(reader);
            } catch (Exception e) {
                logger.warn(e);

                if (onError.equals("abort"))
                    throw new MojoExecutionException("SQL実行中にエラーが発生しました:", e);

            } finally {
                IOUtils.closeQuietly(reader);
            }
        }
        logger.info(successfulStatements + " of " + totalStatements + " SQL statements executed successfully");
    }

}
