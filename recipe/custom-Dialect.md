## Dialectクラスのカスタマイズ例

全角空白と半角空白のみのデータの取り扱いをカスタマイズする例を記述します。

README.mdのload-dataにあるように、全角空白もしくは半角空白のみのデータはnullとしてDBに登録されます。
この設定を変更する際には、使用するDBのダイアレクトクラスを継承したクラスを作成、条件を記述し、pomにて作成したダイアレクトクラスを使用するように設定してください。  
以下は、全角空白のみのデータをnullとしてOracleDBに登録する際の例です。

### 手順
以下の手順で行います。
1. Dialectを配置したjarの作成
2. 作成したDialectを使用するように設定変更

以下に個々の手順について説明します。

#### Dialectを配置したjarの作成
* Dialectを配置するための新規プロジェクトをMavenで作成します。
  ```
  $ mvn archetype:generate -Dfilter=org.apache.maven.archetypes:maven-archetype-quickstart
  ```
  プロジェクトの作成は対話形式で進みます。  
  本ドキュメントでは以下のように入力したと仮定します。
  ```
  (中略)
  Define value for property 'groupId': jp.co.tis.gsp.tools.dba.dialect
  Define value for property 'artifactId': my-dialect
  Define value for property 'version' 1.0-SNAPSHOT: : 0.1.0
  Define value for property 'package' jp.co.tis.gsp.tools.dba.dialect: : jp.co.tis.gsp.tools.dba.dialect
  Confirm properties configuration:
  groupId: jp.co.tis.gsp.tools.dba.dialect
  artifactId: my-dialect
  version: 0.1.0
  package: jp.co.tis.gsp.tools.dba.dialect
  Y: : Y
  (中略)
  ```
* 生成されたファイルのうち、以下のファイルを削除します。
  * App.java
  * AppTest.java
* 生成されたpom.xmlを修正します。  
  修正する箇所は以下です。
  * maven.compiler.source及びmaven.compiler.targetを1.6以上にします。
  * 依存関係に使用しているバージョンのGSPを追加します。
  以下に例を記載します。
  ```xml
    <!-- 中略 -->
    <properties>
      <!-- 中略 -->
      <!-- 1.6以上を指定します -->
      <maven.compiler.source>1.6</maven.compiler.source>
      <maven.compiler.target>1.6</maven.compiler.target>
    </properties>
    <!-- 中略 -->
    <dependencies>
      <dependency>
        <!-- 使用しているGSPを追加します。 -->
        <groupId>jp.co.tis.gsp</groupId>
        <artifactId>gsp-dba-maven-plugin</artifactId>
        <version>4.4.0</version>
        <scope>provided</scope>
      </dependency>
      <!-- 中略 -->
    <dependencies>
    <!-- 中略 -->
  ```

*  **jp.co.tis.gsp.tools.dba.dialect.OracleDialect** を継承した **CustomOracleDialect** を作成します。
  ```java
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

* ビルドおよびリポジトリへのインストールを行います。
  ```
  $ mvn install
  ```

#### 作成したDialectを使用するように設定変更
* 適用するプロジェクトのpomのGSPプラグインに、作成したクラスを読み込ませるよう設定を追加してください。  
以下、設定例です。
  ```xml
  <!-- 中略 -->
  <plugins>
    <!-- 中略 -->
    <plugin>
      <groupId>jp.co.tis.gsp</groupId>
      <artifactId>gsp-dba-maven-plugin</artifactId>
      <version>
        使用するgsp-dba-maven-pluginのバージョン
      </version>
      <executions>
        <execution>
          <id>load-data</id>
          <phase>pre-integration-test</phase>
          <goals>
            <goal>load-data</goal>
          </goals>
          <configuration>
            <!-- 中略 -->
            <optionalDialects>
              <!-- 作成したクラスの完全修飾クラス名で指定する。 -->
              <oracle>jp.co.tis.gsp.tools.dba.dialect.CustomOracleDialect</oracle>
            </optionalDialects>
          </configuration>
        </execution>
      </executions>
      <dependencies>
        <!-- Dialectを配置したjarを依存関係に追加する。 -->
        <dependency>
          <groupId>jp.co.tis.gsp.tools.dba.dialect</groupId>
          <artifactId>my-dialect</artifactId>
          <version>0.1.0</version>
        </dependency>
      </dependencies>
    </plugin>
  </plugins>
  ```

  設定追加後、GSPプラグインを実行してください。半角空白のみのデータがnullにならず、半角空白としてDBに登録されます。

  補足:CSVを読み込む際に使用しているライブラリの仕様上、データの前後の半角空白は無視されますので、半角空白を登録する際は""で囲むようにしてください。
