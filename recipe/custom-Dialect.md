## Dialectクラスのカスタマイズ例

全角空白もしくは半角空白のみのデータの取り扱いをカスタマイズする例を記述します。

現在、README.mdのload-dataにあるように、全角空白もしくは半角空白のみのデータはnullとしてDBに登録されます。
この設定を変更する際には、使用するDBのダイアレクトクラスを継承したクラスを作成、条件を記述し、pomにて作成したダイアレクトクラスを使用するように設定してください。
以下は、全角空白のみのデータをnullと見做し、半角スペースのみはそのままDBに登録する際の設定例です。
```
public class CustomOracleDialect extends OracleDialect{
    
    public void setObjectInStmt(PreparedStatement stmt, int parameterIndex, String value, int sqlType) throws SQLException {
        
        Pattern p = Pattern.compile("^　*$");
        Matcher m = p.matcher(value);
        
        if(sqlType == UN_USABLE_TYPE) {
            stmt.setNull(parameterIndex, Types.NULL);
        } else if(m.matches()) {
            stmt.setNull(parameterIndex, sqlType);
        } else {
            stmt.setObject(parameterIndex, value, sqlType);
        }
    }

}
```