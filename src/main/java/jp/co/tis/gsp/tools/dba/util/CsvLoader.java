package jp.co.tis.gsp.tools.dba.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.FileInputStreamUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;

import com.csvreader.CsvReader;

import jp.co.tis.gsp.tools.db.EntityDependencyParser;
import jp.co.tis.gsp.tools.dba.CsvInsertHandler;
import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.dialect.DialectFactory;

public class CsvLoader {

    private String url;
    private String driver;
    private String schema;
    private String user;
    private String password;

    private File dataDirectory;
    private Charset charset;
    private Map specifiedEncodingFiles;
    private String onError;
    private Log logger;

    public CsvLoader(String url, String driver, String schema, String user, String password, File dataDirectory,
            Charset charset, Map specifiedEncodingFiles, String onError, Log logger) {
        this.url = url;
        this.driver = driver;
        this.schema = schema;
        this.user = user;
        this.password = password;
        this.dataDirectory = dataDirectory;
        this.charset = charset;
        this.specifiedEncodingFiles = specifiedEncodingFiles;
        this.onError = onError;
        this.logger = logger;
    }

    public void execute() throws SQLException, IOException {
        List<File> files = CollectionsUtil
                .newArrayList(FileUtils.listFiles(dataDirectory, new String[] { "csv" }, true));
        DriverManagerUtil.registerDriver(driver);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            logger.error("DBに接続できませんでした。driverおよびurlの設定が正しいか確認してください");
            throw e;
        }

        // 依存関係を考慮し読み込むファイル順をソートする
        EntityDependencyParser parser = new EntityDependencyParser();
        Dialect dialect = DialectFactory.getDialect(url, driver);
        parser.parse(conn, url, schema);
        final List<String> tableList = parser.getTableList();
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return getIndex(FilenameUtils.getBaseName(f1.getName()))
                        - getIndex(FilenameUtils.getBaseName(f2.getName()));
            }

            private int getIndex(String tableName) {
                for (int i = 0; i < tableList.size(); i++) {
                    if (StringUtil.equalsIgnoreCase(tableName, tableList.get(i))) {
                        return i;
                    }
                }
                return 0;
            }

        });
        try {
            for (File file : files) {
                CsvReader reader = null;
                String fileName = file.getName();
                FileInputStream in = FileInputStreamUtil.create(file);
                try {
                    logger.info("取込を開始します:" + fileName);
                    if (specifiedEncodingFiles != null && specifiedEncodingFiles.containsKey(fileName)) {
                        String encoding = (String) specifiedEncodingFiles.get(fileName);
                        reader = new CsvReader(in, Charset.forName(encoding));
                        logger.info(" -- encoding:" + encoding);
                    } else {
                        reader = new CsvReader(in, charset);
                    }
                    String tableName = StringUtils.upperCase(FilenameUtils.getBaseName(fileName));
                    reader.readHeaders();
                    String[] headers = reader.getHeaders();
                    CsvInsertHandler insertHandler = new CsvInsertHandler(conn, dialect,
                            dialect.normalizeSchemaName(schema), tableName, headers);
                    insertHandler.prepare();

                    while (reader.readRecord()) {
                        String[] values = reader.getValues();

                        for (int i = 0; i < values.length; i++) {
                            insertHandler.setObject(i + 1, values[i]);
                        }
                        insertHandler.execute();
                    }

                    insertHandler.close();
                } catch (IOException e) {
                    logger.warn("ファイルがオープンできません:" + file, e);
                    if (onError.equals("abort"))
                        throw e;
                } catch (SQLException e) {
                    logger.warn("SQL実行中にエラーが発生しました:" + file, e);
                    if (onError.equals("abort"))
                        throw e;
                } finally {
                    if (reader != null)
                        reader.close();
                }
            }
        } finally {
            ConnectionUtil.close(conn);
        }

    }

}
