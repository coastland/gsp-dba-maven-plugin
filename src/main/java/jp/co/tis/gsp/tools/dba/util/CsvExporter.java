package jp.co.tis.gsp.tools.dba.util;

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

import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.OutputStreamUtil;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;

import com.csvreader.CsvWriter;

import jp.co.tis.gsp.tools.dba.dialect.Dialect;

public class CsvExporter {

    protected final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    protected final SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String url;
    private String driver;
    private String schema;
    private String user;
    private String password;

    private File outputDir;
    private Charset charset;

    public CsvExporter(String url, String driver, String schema, String user, String password, File outputDir,
            Charset charset) {
        this.url = url;
        this.driver = driver;
        this.schema = schema;
        this.user = user;
        this.password = password;
        this.outputDir = outputDir;
        this.charset = charset;
    }

    public void execute() throws SQLException, IOException  {
        exportCsv(url, user, password, schema, outputDir, charset);
    }

    /**
     * 指定スキーマのテーブルデータをcsvで出力します。
     * 
     * @param adminUser
     * @param adminPassword
     * @param schema
     * @param outputDir
     * @param charset
     * @throws SQLException
     * @throws IOException
     */
    protected void exportCsv(String url, String adminUser, String adminPassword, String schema, File outputDir,
            Charset charset) throws SQLException, IOException {
        DriverManagerUtil.registerDriver(driver);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, adminUser, adminPassword);

            final List<String> tableList = getTableNames(conn.getMetaData(), schema, new String[] { "TABLE" });

            for (String tableName : tableList) {
                writeToCSV(conn, schema, tableName, new File(outputDir, tableName + ".csv"), charset);
            }

        } catch (SQLException e) {
            throw new SQLException("DBに接続できませんでした。driverおよびurlの設定が正しいか確認してください", e);
        } catch (IOException e) {
            throw new IOException("CSVファイルの出力中にエラーが発生しました。", e);
        } finally {
            ConnectionUtil.close(conn);
        }
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
    protected List<String> getTableNames(DatabaseMetaData metaData, String normalizedSchemaName, String[] types)
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
     * SELECT文の結果をCSVファイルにカラム名ヘッダー付きで出力します。
     * 
     * @param metaData
     * @param resultSet
     * @param csvFile
     * @param charset
     * @throws SQLException
     * @throws IOException
     */
    protected void writeToCSV(Connection conn, String schema, String tableName, File csvFile, Charset charset)
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
            resultSet = stmt
                    .executeQuery("SELECT * FROM " + tableName + getOrderString(conn.getMetaData(), schema, tableName));
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
    protected String convert(ResultSet resultSet, int index, int sqlType) throws SQLException {

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
     * 主キー項目でソートをかけるorderBy文字列を生成します。
     * 
     * @param meta
     * @param tableName
     * @return
     * @throws SQLException
     */
    protected String getOrderString(DatabaseMetaData meta, String schema, String tableName) throws SQLException {
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
}
