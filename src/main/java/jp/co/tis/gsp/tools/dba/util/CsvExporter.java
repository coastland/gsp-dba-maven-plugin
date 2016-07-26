package jp.co.tis.gsp.tools.dba.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import jp.co.tis.gsp.tools.dba.dialect.DialectFactory;

public class CsvExporter {


    private String url;
    private String driver;
    private String schema;
    private String user;
    private String password;

    private File outputDir;
    private Charset charset;
    
    private Dialect dialect;

    public CsvExporter(String url, String driver, String schema, String user, String password, File outputDir,
            Charset charset) {
        this.url = url;
        this.driver = driver;
        this.schema = schema;
        this.user = user;
        this.password = password;
        this.outputDir = outputDir;
        this.charset = charset;

        dialect = DialectFactory.getDialect(url, driver);
    }

    public void execute() throws SQLException, IOException {
        exportCsv(url, user, password, schema, outputDir, charset);
    }

    /**
     * 指定スキーマのテーブルデータをcsvで出力します。
     * 
     * @param url url
     * @param adminUser 管理者ユーザ
     * @param adminPassword 管理者パスワード
     * @param schema スキーマ
     * @param outputDir 出力フォルダ
     * @param charset 文字セット
     * @throws SQLException 例外
     * @throws IOException 例外
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
     * @param metaData コネクションメタデータ
     * @param normalizedSchemaName スキーマ
     * @param types データベースオブジェクトタイプ
     * @return テーブル名のリスト
     * @throws SQLException 例外
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
     * @param conn コネクション
     * @param schema スキーマ
     * @param tableName テーブル名
     * @param csvFile CSVファイル
     * @param charset 文字セット
     * @throws SQLException 例外
     * @throws IOException 例外
     */
    protected void writeToCSV(Connection conn, String schema, String tableName, File csvFile, Charset charset)
            throws SQLException, IOException {

        
        Statement stmt = null;
        ResultSet resultSet = null;

        OutputStream out = new FileOutputStream(csvFile);
        String[] values = null;
        CsvWriter csvWriter = null;

        try {
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery("SELECT * FROM " + schema + "." + tableName
                    + getOrderString(conn.getMetaData(), schema, tableName));
            ResultSetMetaData meta = resultSet.getMetaData();

            csvWriter = new CsvWriter(out, ',', charset);
            csvWriter.setForceQualifier(true);
            csvWriter.setRecordDelimiter('\n');

            // 出力対象カラムの絞り込みとヘッダー出力
            int columnCount = meta.getColumnCount();
            Map<String, Integer> columnTypeMap = new LinkedHashMap<String, Integer>();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = meta.getColumnName(i);
                int sqlType = dialect.guessType(conn, schema, tableName, columnName);
                if (sqlType == Dialect.UN_USABLE_TYPE)
                    continue;

                if (meta.isAutoIncrement(i))
                    continue;

                columnTypeMap.put(columnName, sqlType);
            }

            List<String> columnList = new ArrayList<String>(columnTypeMap.keySet());
            csvWriter.writeRecord((String[]) columnList.toArray(new String[0]));

            // データの出力
            while (resultSet.next()) {
                values = new String[columnList.size()];

                int i = 0;
                for (String columnName : columnList) {
                    values[i] = dialect.convertLoadData(resultSet, i + 1, columnName, 
                                                            columnTypeMap.get(columnName));
                    i++;
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
     * 主キー項目でソートをかけるorderBy文字列を生成します。
     * 
     * @param meta コネクションメタデータ
     * @param tableName テーブル名
     * @return orderby文字列
     * @throws SQLException 例外
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
