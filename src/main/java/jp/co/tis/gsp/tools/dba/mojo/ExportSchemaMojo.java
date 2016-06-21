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

package jp.co.tis.gsp.tools.dba.mojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.OutputStreamUtil;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;

import com.csvreader.CsvWriter;

import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.dialect.DialectFactory;
import jp.co.tis.gsp.tools.dba.util.DialectUtil;

/**
 * export-schema.
 * 
 * データベーススキーマをダンプする。
 * 
 * @author kawasima
 */
@Mojo(name = "export-schema", requiresProject = true)
public class ExportSchemaMojo extends AbstractDbaMojo {

    @Parameter(defaultValue = "target/export")
    protected File outputDirectory;

    @Parameter(defaultValue = "target/ddl", required = true)
    protected File ddlDirectory;

    @Parameter(required = true)
    protected File extraDdlDirectory;

    @Parameter
    protected Map<String, String> specifiedEncodingTables;

    @Component(role = Archiver.class, hint = "jar")
    protected JarArchiver jarArchiver;

    @Component
    private MavenProject project;

    // CSVデータ出力先(Mavenパラメータとする必要がない。内部的利用に限定。)
    protected File dataDirectory;

    // デフォルト設定値
    final Charset SJIS = Charset.forName("Windows-31J");
    final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    final SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /*** jar内のディレクトリ構造 **/
    private final String DDL_DIR_NAME = "ddlDirectory";
    private final String EXTRADDL_DIR_NAME = "extraDdlDirecotry";
    private final String DATA_DIR_NAME = "dataDirectory";

    @Override
    protected void executeMojoSpec() throws MojoExecutionException, MojoFailureException {

        Dialect dialect = DialectFactory.getDialect(url, driver);
        DialectUtil.setDialect(dialect);

        if (!outputDirectory.exists()) {
            try {
                FileUtils.forceMkdir(outputDirectory);
            } catch (IOException e) {
                throw new MojoExecutionException("Can't create dump output directory." + outputDirectory, e);
            }
        }

        // CSVデータ出力ディレクトリ
        // CSV出力先はoutputDirectoryの直下にdataDirectory固定
        dataDirectory = new File(outputDirectory, DATA_DIR_NAME);
        if (!dataDirectory.exists()) {
            try {
                FileUtils.forceMkdir(dataDirectory);
            } catch (IOException e) {
                throw new MojoExecutionException("Can't create dump output directory." + dataDirectory, e);
            }
        }

        // DBのテーブルからCSVデータをCSVデータ出力ディレクトリに出力。
        exportCSV();

        // DDL、extraDDLをoutputDirectoryにコピー。
        collectDdl();

        // DDL、extraDDl、csvデータをjarでpack。
        jarArchiver.addDirectory(outputDirectory);
        jarArchiver.setDestFile(new File(outputDirectory, jarName()));

        try {
            jarArchiver.createArchive();
        } catch (IOException e) {
            throw new MojoExecutionException("アーカイブに失敗しました。", e);
        }
        getLog().info(schema + "Export完了 ");
    }

    /**
     * DDLと追加DDLを出力ディレクトリに収集します。
     * 
     * @return
     * @throws MojoExecutionException
     */
    private void collectDdl() throws MojoExecutionException {
        try {
            FileUtils.copyDirectory(ddlDirectory, new File(outputDirectory, DDL_DIR_NAME));
            FileUtils.copyDirectory(extraDdlDirectory, new File(outputDirectory, EXTRADDL_DIR_NAME));
        } catch (IOException e) {
            throw new MojoExecutionException("DDLとデータファイルのコピーに失敗しました。", e);
        }
    }

    /**
     * 接続スキーマの全テーブルデータをCSVファイルに出力します。
     * 
     * @throws MojoExecutionException
     */
    private void exportCSV() throws MojoExecutionException {

        DriverManagerUtil.registerDriver(driver);
        Dialect dialect = DialectUtil.getDialect();
        Connection conn = null;
        Charset charset;

        try {
            conn = DriverManager.getConnection(url, user, password);

            final List<String> tableList = getTableNames(conn.getMetaData(), dialect.normalizeSchemaName(schema),
                    new String[] { "TABLE" });

            for (String tableName : tableList) {

                if (specifiedEncodingTables != null && specifiedEncodingTables.containsKey(tableName)) {
                    String encoding = (String) specifiedEncodingTables.get(tableName);
                    charset = Charset.forName(encoding);
                    getLog().info(" -- encoding:" + encoding);
                } else {
                    charset = SJIS;
                }

                writeToCSV(conn, tableName, new File(dataDirectory, tableName + ".csv"), charset);
            }

        } catch (SQLException e) {
            getLog().error("DBに接続できませんでした。driverおよびurlの設定が正しいか確認してください");
            throw new MojoExecutionException("", e);
        } catch (FileNotFoundException e) {
            getLog().error("CSVファイルの出力中にエラーが発生しました。", e);
            throw new MojoExecutionException("", e);
        } catch (IOException e) {
            getLog().error("CSVファイルの出力中にエラーが発生しました。", e);
        } finally {
            ConnectionUtil.close(conn);
        }
    }

    /**
     * SELECT文の結果をCSVファイルにカラム名ヘッダー付きで出力します。
     * 
     * @param metaData
     * @param resultSet
     * @param csvFile
     * @param charset
     * @throws SQLException
     * @throws IOException
     */
    public void writeToCSV(Connection conn, String tableName, File csvFile, Charset charset)
            throws SQLException, IOException {

        Dialect dialect = DialectUtil.getDialect();
        Statement stmt = null;
        ResultSet resultSet = null;

        OutputStream out = new FileOutputStream(csvFile);
        String[] columnHeaders;
        String[] values = null;
        CsvWriter csvWriter = null;

        try {
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery("SELECT * FROM " + tableName + getOrderString(conn.getMetaData(), tableName));
            ResultSetMetaData meta = resultSet.getMetaData();

            csvWriter = new CsvWriter(out, ',', charset);
            csvWriter.setForceQualifier(true);
            csvWriter.setRecordDelimiter('\n');

            // カラムヘッダーの出力
            int columnCount = meta.getColumnCount();
            columnHeaders = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnHeaders[i - 1] = meta.getColumnName(i);
            }
            csvWriter.writeRecord(columnHeaders);

            // データの出力
            while (resultSet.next()) {
                values = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    resultSet.getString(i);
                    int sqlType = dialect.guessType(conn, schema, tableName, meta.getColumnName(i));
                    values[i - 1] = convert(resultSet, i, sqlType);
                }
                csvWriter.writeRecord(values);
            }
        } finally {
            csvWriter.flush();
            csvWriter.close();

            ResultSetUtil.close(resultSet);
            StatementUtil.close(stmt);
            OutputStreamUtil.close(out);
        }

    }

    /**
     * SQLのタイプに応じて値の書式を変換します。
     * 
     * @param resultSet
     * @param meta
     * @param index
     * @return
     * @throws SQLException
     */
    private String convert(ResultSet resultSet, int index, int sqlType) throws SQLException {

        String value = "";
        switch (sqlType) {
        case Types.DATE:
            Date date = resultSet.getDate(index);
            if (date != null) {
                value = sdfDate.format(date);
            }
            break;
        case Types.TIMESTAMP:
            Timestamp timestamp = resultSet.getTimestamp(index);
            if (timestamp != null) {
                value = sdfTimestamp.format(timestamp);
            }
            break;
        default:
            value = resultSet.getString(index);
            break;
        }

        return value;
    }

    /**
     * 指定スキーマに属するテーブル名のリストを取得します。
     * 
     * @param metaData
     * @param normalizedSchemaName
     * @param types
     * @return
     * @throws SQLException
     */
    public List<String> getTableNames(DatabaseMetaData metaData, String normalizedSchemaName, String[] types)
            throws SQLException {
        List<String> allNames = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = metaData.getTables(null, normalizedSchemaName, "%", types);
            while (rs.next()) {
                allNames.add(rs.getString("TABLE_NAME"));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return allNames;
    }

    /**
     * 主キー項目でソートをかけるorderBy文字列を生成します。
     * 
     * @param meta
     * @param tableName
     * @return
     * @throws SQLException
     */
    private String getOrderString(DatabaseMetaData meta, String tableName) throws SQLException {
        String orderByString = "";
        ResultSet rs = null;
        Map<Short, String> keyMap = new TreeMap<Short, String>();

        try {
            rs = meta.getPrimaryKeys(null, schema, tableName);
            while (rs.next()) {
                keyMap.put(rs.getShort("KEY_SEQ"), rs.getString("COLUMN_NAME"));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        Set<Short> keySet = keyMap.keySet();

        if (keySet.size() > 0) {
            orderByString = " ORDER BY";
            for (Short key : keySet) {
                String preStr = key.shortValue() > 1 ? "," : " ";
                orderByString += (preStr + keyMap.get(key));
            }
        }

        return orderByString;
    }

    private String jarName() {
        if (project == null) {
            return "dump-test.jar";
        } else {
            return project.getArtifactId() + "-testdata-" + project.getVersion() + ".jar";
        }
    }
}
