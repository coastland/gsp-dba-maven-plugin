## Dialectクラスのカスタマイズ例

全角空白と半角空白のみのデータの取り扱いをカスタマイズする例を記述します。

README.mdのload-dataにあるように、全角空白もしくは半角空白のみのデータはnullとしてDBに登録されます。
この設定を変更する際には、使用するDBのダイアレクトクラスを継承したクラスを作成、条件を記述し、pomにて作成したダイアレクトクラスを使用するように設定してください。
以下は、全角空白のみのデータをnullとしてOracleDBに登録する際の設定例です。

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

カスタマイズしたら、適用するプロジェクトのpomのGSPプラグインに、作成したクラスを読み込ませるよう設定を追加してください。
以下、設定例です。

```
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      使用するGSPプラグインのバージョン
    </version>
    <executions>
      <execution>
        <id>load-data</id>
        <phase>pre-integration-test</phase>
        <goals>
          <goal>load-data</goal>
        </goals>
        <configuration>
          <optionalDialects>
            <!-- 作成したクラスの完全修飾クラス名で指定する。 -->
            <oracle>jp.co.tis.gsp.tools.dba.dialect.CustomOracleDialect</oracle>
          </optionalDialects>
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```

設定追加後、GSPプラグインを実行してください。半角スペースのみのデータがnullにならず、半角スペースとしてDBに登録されます。

補足:CSVを読み込む際に使用しているライブラリの仕様上、データの前後の半角スペースは無視されますので、半角スペースを登録する際は""で囲むようにしてください。
