# gsp-dba-maven-plugin

gsp-dba-maven-pluginは、DBAのルーチンワークを自動化し、本来のデータモデリング作業に
集中できるようにするためのツールです。

以下の操作が簡単に実行できるようになります。

* ER図からDDLを生成し実行する。
* データベースのテーブルに対応したEntityクラスを生成する。
* CSV形式のデータをデータベースに登録する。
* データベーススキーマのダンプファイルを取得する。
* リポジトリ上のダンプファイルをローカル環境へ反映する。

> * ER図からDDLを生成するには、モデリングツールとして[SI Object Browser ER](http://www.sint.co.jp/siob/er/)を使用する必要があります。
> それ以外のツールを使用する場合、DDL生成機能は使用できません。

> * gsp-dba-maven-pluginは開発フェーズで用いることを想定しています。本番環境での使用は推奨しません。

## ゴールの概要

* [generate-ddl](#generate-ddl) データモデルを解析し、DDLを生成する。
* [execute-ddl](#execute-ddl) DDLを実行する。
* [load-data](#load-data) CSV形式で定義したデータをデータベースに登録する。
* [generate-entity](#generate-entity) 指定したスキーマを解析し、Entityクラスを生成する。
* [export-schema](#export-schema) データベーススキーマをダンプする。
* [import-schema](#import-schema) リポジトリから取得したダンプファイルをインポートする。


データベースによって、動作が異なる場合や制約事項があります。
詳細は、 **データベースの対応状況** を参照してください。


## 使用方法

### 設定

pom.xmlに以下の設定を追加することでプラグインが使用できるようになります。

```xml
<pluginManagement>
  <plugins>
    <plugin>
      <groupId>jp.co.tis.gsp</groupId>
      <artifactId>gsp-dba-maven-plugin</artifactId>
      <version>
        使用するgsp-dba-maven-pluginのバージョン
      </version>
      <dependencies>
        <!-- プロジェクトで使用するDB製品にあわせたJDBCドライバに修正してください。 -->
        <dependency>
          <groupId>com.oracle</groupId>
          <artifactId>ojdbc6</artifactId>
          <version>11.2.0.2.0</version>
        </dependency>
      </dependencies>
    </plugin>
  </plugins>
</pluginManagement>
```

### ゴール共通のパラメータ

以下のパラメータは全てのゴールで共通です。
対応する値を設定してください。

| 設定値    | 必須  | 説明                                                            |
|:---------------|:-----:|:----------------------------------------------------------------|
| driver         | ○     | 使用するJDBCドライバ。                                         |
| url            | ○     | データベースのURL。 jdbc:subprotocol:subname 形式。            |
| adminUser      | ○     | データベースのadminユーザ名。Oracleの場合はsysは指定出来ません。DB2の場合はデータベース作成ユーザか対象データベースでDBADM権限を持つユーザを指定して下さい(db2adminを指定すると、データベースのバージョンによってはエラーになります)。|
| adminPassword  | ×     | adminUserに設定したユーザのパスワード。                        |
| user           | ○     | データベースのユーザ名。 Oracleの場合はsysは指定出来ません。PostgreSQLの場合は常に小文字に変換して処理されます。|
| password       | ×     | userに設定したユーザのパスワード。                             |
| schema         | ×     | データベースのスキーマ名。<br />H2Databaseの場合は指定不可、常にPUBLICスキーマとして解釈します。  それ以外のDBでスキーマを指定しない場合はユーザ名と同じスキーマ名を使用すると解釈されます。 PostgreSQLの場合は常に小文字に、H2、DB2、Oracleの場合は常に大文字に変換して処理されます。|
| dmpFile        | ×     | ダンプファイル名。指定しなかった場合、[スキーマ名].dmpとなる。 |
|optionalDialects | ×    | 使用するダイアレクトクラスのFQCN。|

 * optionalDialectsの指定方法  
 使用するダイアレクトクラスを変更する場合、以下の形式でデータベースと対応するダイアレクトクラスを定義します。

```xml
<configuration>
  <optionalDialects>
    <oracle>jp.co.tis.gsp.tools.dba.dialect.CustomOracleDialect</oracle>
  </optionalDialects>
</configuration>
```

### generate-ddl

データモデルを解析し、DDLを生成します。
生成するDDLとファイル名の対応は以下の通りです。

| DDLの種類        | ファイル名                                    |
|:-----------------|:----------------------------------------------|
| テーブル定義     | 10_CREATE_<テーブル名>.sql                    |
| インデックス定義 | 20_CREATE_<インデックスの物理名>.sql          |
| 外部キー定義     | 30_CREATE_FK_<テーブル名><連番>.sql           |
| ビュー定義       | 40_CREATE_<ビューの物理名>.sql                |

また自動採番がDDLに反映されるルールは[こちら](./recipe/spec-generateDdl.md)で確認してください。


    データモデル上のオブジェクトは「論理・物理モデル」として定義して下さい。
    「論理モデルのみ」または「物理モデルのみ」と定義した場合、対象のDDLは生成されません。
    また、文字列型や日付型を持つカラムのデフォルト値を設定する際は値をシングルクォーテーションで囲まないとexecute-ddl時にエラーが発生します。


使用する場合、pom.xmlに以下を追加してください。

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      使用するgsp-dba-maven-pluginのバージョン
    </version>
    <executions>
      <execution>
        <id>generate-ddl</id>
        <phase>generate-sources</phase>
        <goals>
          <goal>generate-ddl</goal>
        </goals>
        <configuration>
          <!-- 設定を追加 -->
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```

#### 使用可能なパラメータ

| 設定値                      | 必須  | 説明                                                            |
|:---------------------------|:-----:|:----------------------------------------------------------------|
| erdFile                    | ○     | erdファイルのパス。ワークディレクトリからの相対パスで指定する。 |
| outputDirectory            | ×     | DDLの出力ディレクトリ。デフォルトは、"target/ddl"。             |
| lengthSemantics            | ×     | 長さセマンティクス。デフォルトはバイト。                        |
| ddlTemplateFileDir         | ×     | プロジェクト固有のDDLテンプレートの配置ディレクトリをワークディレクトリからの相対パスで指定する。 |
テンプレートをカスタマイズする際は、[generate-ddlで使用するテンプレートのカスタマイズ例](./recipe/custom-DdlTemplate.md)を参照してください。


### execute-ddl

* DDLを実行します。
* 複数ファイルにまたがる場合、ファイル名の昇順で実行します。
* パラメータuserに指定されたユーザが存在しない場合は指定ユーザを作成します。
* パラメータschemaで指定されたスキーマが存在しない場合は、指定スキーマを作成します。
  * MySQLの場合はスキーマの作成を行いません。事前にCREATE DATABASE文などで用意しておく必要があります。
* パラメータschemaで指定されたスキーマが存在する場合は、指定スキーマ内にあるテーブル、ビュー、シーケンスを最初に全て削除します。

使用する場合、pom.xmlに以下を追加してください。

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      使用するgsp-dba-maven-pluginのバージョン
    </version>
    <executions>
      <execution>
        <id>execute-ddl</id>
        <phase>generate-sources</phase>
        <goals>
          <goal>execute-ddl</goal>
        </goals>
        <configuration>
          <!-- 設定を追加 -->
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```

#### 使用可能なパラメータ

| 設定値               | 必須  | 説明                                                            |
|:---------------------|:-----:|:----------------------------------------------------------------|
| ddlDirectory         | ×     | DDLの配置ディレクトリ。デフォルトは、"target/ddl"。             |
| extraDdlDirectory    | ×     | 追加で実行したいSQLファイルの配置ディレクトリ。                 |

### load-data

CSV形式で定義したデータを、データベースの指定したスキーマに登録します。

使用する場合、pom.xmlに以下を追加してください。

```xml
<plugins>
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
          <!-- 設定を追加 -->
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```


#### 使用可能なパラメータ

| 設定値                 | 必須  | 説明                                                                        |
|:-----------------------|:-----:|:----------------------------------------------------------------------------|
| dataDirectory          | ○     | データファイルの配置ディレクトリ。                                          |
| specifiedEncodingFiles | ×     | データファイルの文字コードを指定する場合に設定。デフォルトは"Windows-31J"。 |


* specifiedEncodingFilesの指定方法

データファイルの文字コードを指定する場合、以下の形式でファイル名と対応する文字コードを定義します。

```xml
<configuration>
  <specifiedEncodingFiles>
    <aa.csv>UTF-8</aa.csv>
    <bb.csv>UTF-8</bb.csv>
  </specifiedEncodingFiles>
</configuration>
```


#### データの形式
データおよびデータファイルは以下の形式で作成してください。

* ファイル名は、 *テーブルの物理名*.csv。
* 先頭行は、カラムの物理名(:カラムの型名)。DBによっては型名を指定しなくても自動で推定し、設定される。
* 二行目以降にテストデータを記載。
* 全角空白、半角空白のみの項目はnullとして扱われる。変更する際は[Dialectクラスのカスタマイズ例](./recipe/custom-Dialect.md)を参照すること。

データの例を記載します。


    ITEM,VARCHAR_ITEM:VARCHAR,DATE_ITEM:DATE,TIMESTAMP_ITEM:TIMESTAMP,ARRAY_ITEM:ARRAY
    item,item0000000000000000,2014-12-13,2014-12-13 4:15:16,"項目1,項目2,項目3"


#### 登録可能なデータ型

登録可能なデータ型はデータベースごとに異なります。
詳細は、 [load-dataの対応状況](./doc/db-status.md#load-dataの対応状況) を参照してください。

### generate-entity

データベースのメタデータから、テーブルに対応するエンティティを生成します。自動生成時に付与される各種アノテーションに関しては、[エンティティで使用されるアノテーション](recipe/spec-generatedEntity.md)をご確認ください。
生成処理はカスタマイズしたS2JDBC-Genを使用しています。

使用する場合、pom.xmlに以下を追加してください。

```xml
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
          <!-- 設定を追加 -->
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```


#### 使用可能なパラメータ

| 設定値                 | 必須  | 説明                                                                      |
|:-----------------------|:-----:|:--------------------------------------------------------------------------|
| ignoreTableNamePattern | ×    | 自動生成対象外とするテーブル名。正規表現で指定する。                      |
| entityPackageName      | ×    | エンティティのパッケージ名。デフォルトは、”entity”。                    |
| genDialectClassName    | ×    | S2JDBC-Genのダイアレクトインタフェースの実装クラス名。<br>カスタマイズする際は[GenDialectクラスのカスタマイズ例](./recipe/custom-genDialect.md)を参照してください。<br> |
| dialectClassName       | ×    | S2JDBCのダイアレクトインタフェースの実装クラス名。                        |
| rootPackage            | ○    | ルートパッケージ名。                                                      |
| useAccessor            | ×    | アクセッサを使用するかどうか。デフォルトは、”false”。                   |
| entityTemplate         | ×    | entity の自動生成テンプレート。デフォルトは、"java/gsp_entity.ftl"                                           |
|javaFileDestDir        | ×      | 生成されたentityのjavaファイルを配置するディレクトリ|
|templateFilePrimaryDir | ×      |entityTemplateまでのパス。デフォルトは、"src/main/resources/org/seasar/extension/jdbc/gen/internal/generator/tempaltes"。<br>使用例:ファイルまでのパスが"src/main/resource/template/gsp_template.ftlの場合、それぞれ <br> entityTemplate: gsp_template.ftl <br> templateFilePrimaryDir:src/main/resource/template <br> と設定する。|
テンプレートをカスタマイズする際は、[generate-entityで使用するテンプレートのカスタマイズ例](./recipe/custom-EntityTemplate.md)を参照してください。

### export-schema

指定したスキーマのダンプファイルをエクスポートします。

    maven-install-pluginやmaven-deploy-pluginと組み合わせることで、
    ローカル環境へのインストールやリモートリポジトリへのデプロイが可能になります。

ダンプファイルのファイル名は以下の形式です。

    プロジェクトのアーティファクトId + "-testdata-" + プロジェクトのバージョン + ".jar"


使用する場合、pom.xmlに以下を追加してください。

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      使用するgsp-dba-maven-pluginのバージョン
    </version>
    <executions>
      <execution>
        <id>export-schema</id>
        <phase>install</phase>
        <goals>
          <goal>export-schema</goal>
        </goals>
        <configuration>
          <!-- 設定を追加 -->
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```


#### 使用可能なパラメータ

| 設定値                 | 必須  | 説明                                                                                  |
|:-----------------------|:-----:|:--------------------------------------------------------------------------------------|
| outputDirectory        | ×     | データベーススキーマをエクスポートするディレクトリのパス。デフォルトは”target/dump”。 |

export-schemaはDB2とSQLServerには対応しておりません。  
これらのDBを使用する際は、上記の`<execution>~</execution>`をコメントアウト、もしくは削除してください。

### import-schema

リポジトリからダンプファイルを取得し、ローカルのデータベースにインポートする。

使用する場合、pom.xmlに以下を追加してください。

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      使用するgsp-dba-maven-pluginのバージョン
    </version>
    <configuration>
    <!-- 設定を追加 -->
    </configuration>
  </plugin>
</plugins>
```

#### 使用可能なパラメータ

| 設定値                 | 必須  | 説明                                                                                  |
|:-----------------------|:-----:|:--------------------------------------------------------------------------------------|
| inputDirectory         | ×     | ダンプファイルの配置ディレクトリ。デフォルトは、”target/dump”。                       |
| groupId                | ×     | ダンプファイルのグループID。デフォルトは、プロジェクトのグループID。                  |
| artifactId             | ×     | ダンプファイルのアーティファクトID。デフォルトは、プロジェクトのアーティファクトID。  |
| version                | ×     | ダンプファイルのバージョン。デフォルトは、プロジェクトのバージョン。                  |

import-schemaはDB2とSQLServerには対応しておりません。

### データベースごとの対応状況

* [こちら](./doc/db-status.md)を参照して下さい。

### 制約事項

各ゴールは、以下の制約事項が存在します。

#### execute-ddl

* Oracle<br />
  ユーザー名とスキーマ名が一致しない時、新たにスキーマが作成されます。<br />
  ただしスキーマを本プラグインで新規作成した時、同時に作成されるユーザのログインパスワードはスキーマ名と同じになります。
* DB2<br />
  ユーザ名とスキーマ名は同一のものしか指定できません。
* H2<br />
  ユーザ名とスキーマ名に **同一の値を設定すると** 失敗します。

#### generate-entity

* MS SQL Server<br />
   拡張エンティティを用いて設定したテーブル、カラムに対するコメントはjavaファイルに反映されません。
* H2<br />
  ユーザ名とスキーマ名に **同一の値を設定すると** 失敗します。<br />
  erdファイルにViewの定義が含まれていると失敗します。

#### export-schema

* 全データベース共通<br />
  gsp-dba-maven-pluginを起動するマシンと、同一マシンでデータベースが起動していない場合の動作は保障していません。
* MS SQL Server<br />
  使用できません。
* DB2<br />
  使用できません。
* H2<br />
  ユーザ名とスキーマ名に **同一の値を設定すると** 失敗します。

#### import-schema

* 全データベース共通<br />
  gsp-dba-maven-pluginを起動するマシンと、同一マシンでデータベースが起動していない場合の動作は保障していません。
* MS SQL Server<br />
  使用できません。
* DB2<br />
  使用できません。
* H2<br />
  ユーザ名とスキーマ名に **同一の値を設定すると** 失敗します。

## License

gsp-dba-maven-plugin はApache License 2.0 の元に配布されます。

* http://www.apache.org/licenses/LICENSE-2.0.txt
