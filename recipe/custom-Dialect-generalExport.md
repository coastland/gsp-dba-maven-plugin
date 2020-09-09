## 汎用モードのエクスポート/インポートの実装

- gsp-dba-maven-pluginで用意しているOracleDialectは、DBMS固有のエクスポート機能（expdp）を利用したスキーマのエクスポートを行っています。  
ここでは、DBMS固有のエクスポートではなくDDLファイルとCSVデータを用いた汎用モードでエクスポートするためのDialectの作成方法を示します。  
  Dialectは別途作成したjarに格納します。Dialectを格納したjarの作成は[Dialectクラスのカスタマイズ例](./custom-Dialect.md)を参考にしてください。


### 手順

1.  **jp.co.tis.gsp.tools.dba.dialect.Dialect** を継承した **Oracle12cDialect** を作成します。
    ```java
    package jp.co.tis.gsp.tools.dba.dialect;

    public class Oracle12cDialect extends Dialect {

    }
    ```

2.  **jp.co.tis.gsp.tools.dba.dialect.OracleDialect** の実装コードを全て **Oracle12cDialect** にコピーします。
    ```java
    package jp.co.tis.gsp.tools.dba.dialect;

    import java.io.BufferedReader;
    import java.io.File;
    import java.io.InputStreamReader;
    import java.nio.charset.Charset;
    ...

    /**
    * copy&paste from jp.co.tis.gsp.tools.dba.dialect.OracleDialect
    */
    public class Oracle12cDialect extends Dialect {

            private static final List<String> USABLE_TYPE_NAMES = new ArrayList<String>();

            static {
                USABLE_TYPE_NAMES.add("CHAR");
                USABLE_TYPE_NAMES.add("DATE");
    ....
    ```
    
3. Oracle12cDialectのコンストラクタを修正します。
    ```java
    //public OracleDialect() {
    public Oracle12cDialect() {
        GenDialectRegistry.deregister(
                org.seasar.extension.jdbc.dialect.OracleDialect.class
        );
        GenDialectRegistry.register(
                org.seasar.extension.jdbc.dialect.OracleDialect.class,
                new ExtendedOracleGenDialect()
        );
    }
    ```
    
4.  Oracle12cDialectのexportSchema()及びimportSchema()メソッドをコメントアウトします。
    ```java
    /***
    Call jp.co.tis.gsp.tools.dba.dialect.Dialect#exportSchema()

        @Override
        public void exportSchema(ExportParams params) throws MojoExecutionException {
            ...
        }
    ***/

    /***
    Call jp.co.tis.gsp.tools.dba.dialect.Dialect#importSchema()

        @Override
      public void importSchema(ImportParams params) throws MojoExecutionException{
          ...
      }
    ***/
    ```

5. プラグイン定義の変更
以下を設定します。  
- パッケージするDDL及び追加DDLフォルダの場所をパラメータddlDirectory、extraDdlDirectoryで指定
- Dialectが存在するjarへの依存関係を追加
    ```xml
      <plugin>
        <groupId>jp.co.tis.gsp</groupId>
        <artifactId>gsp-dba-maven-plugin</artifactId>
        <version>4.0.0</version>
        <configuration>
          ...
        </configuration>
        <executions>
          <execution>
            <id>export-schema</id>
            <phase>install</phase>
            <goals>
              <goal>export-schema</goal>
            </goals>
            <configuration>
               <!-- パッケージするDDL及び追加DDLフォルダの場所の指定 -->
              <ddlDirectory>target/ddl</ddlDirectory>
              <extraDdlDirectory>src/main/resources/extraDDL</extraDdlDirectory>
            </configuration>
          </execution>  
        </executions>
        <dependencies>
          <!-- Dialectを配置したjarを依存関係に追加 -->
          <dependency>
            <groupId>jp.co.tis.gsp.tools.dba.dialect</groupId>
            <artifactId>my-dialect</artifactId>
            <version>0.1.0</version>
          </dependency>
        </dependencies>
    ```
