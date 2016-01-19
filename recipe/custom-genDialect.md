## GenDialectクラスのカスタマイズ例

OracleデータベースのNumber型をJavaのクラスに変換する際の対応関係を変更する例を記述します。

現在、OracleのNumber型は、桁数と小数部のありなしで対応付けるJavaの型を決定しています。デフォルトの設定は、  
・ 小数部あり：BigDecimal  
・ １桁：boolean  
・ 5桁未満：Short  
・ 10桁未満：Integer  
・ 19桁未満：Long  
・ 19桁以上：BigInteger  

となっています。（条件は上部のものが優先されます。）

この対応付けはExtendedOracleGenDialectで行われており、カスタマイズする場合はこのクラスを継承したクラスを
作成、条件を記述し、pomにて作成したクラスを読み込むように設定してください。  
以下はカスタマイズの例です。例では、10桁未満の数字をIntegerに、19桁以上の数字をBigDecimalに対応付けるように変更しています。

```
public class CustomOracleGenDialect extends ExtendedOracleGenDialect {

    public CustomOracleGenDialect() {
        super();
        columnTypeMap.put("number", new OracleColumnType("number($p,$s)", BigDecimal.class) {
            @Override
            public Class<?> getAttributeClass(int length, int precision, int scale) {
                if (scale != 0) {
                    return BigDecimal.class;
                }
                if (precision < 10) {
                    return Integer.class;
                }
                if (precision < 19) {
                    return Long.class;
                }

                return BigDecimal.class;
            }
        });
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
      使用するgsp-dba-maven-pluginのバージョン
    </version>
    <executions>
      <execution>
        <id>generate-entity</id>
        <phase>generate-sources</phase>
        <goals>
          <goal>generate-entity</goal>
        </goals>
        <configuration>
          <genDialectClassName>
            <!-- 作成したクラスの完全修飾クラス名で指定する。 -->
            jp.co.tis.gsp.tools.dba.dialect.CustomOracleGenDialect
          </genDialectClassName>
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```

設定追加後、GSPプラグインを実行してください。カスタマイズされたルールに則りEntityが生成されます。
