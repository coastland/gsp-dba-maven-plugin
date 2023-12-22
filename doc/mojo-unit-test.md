gsp-dba-maven-plugin テスト
========

## Overview

gsp-dba-maven-pluginで用意しているユニットテストについて説明。

## Description

* [harness](https://maven.apache.org/plugin-testing/maven-plugin-testing-harness/) を利用してMOJO単位で実行するスタイル。
* テストケース毎にpom.xmlを用意して、そこにパラメータを定義。
* テストケースのモデルは、
```
mojo(goal) - テストケース(テストメソッド) - 対象DB(db2, h2, etc..)
```
* `mvn clean install`ではテストは実行されないようにしています。
* [JPA簡易検証](#jpa簡易検証)
    * integration-testで生成されたエンティティを使って簡単なJPA(Hibernate)での検証をします。
    * 厳密には Jakarta EE 移管後の Jakarta Persistence での検証ですが、ここでは便宜的に JPA と表記します。

## SetUp
1. テスト対象のRDBMSをローカルにインストールしていない場合は、インストールする。
    * DB共通の注意点
      * 本ツールのexport/importはDB付属のコマンドを実行する。そのためDockerでインストールすると、export/importのテストが実行できない。
    * 各DBの注意点
      * Oracle 19c
        * 再インストールする際、古いOracleを消した後、環境変数ORACLE_HOME削除し、OSを再起動してから、新しくインストールすること。  
          ORACLE_HOMEの参照先が古いと再インストールに失敗する。
        * インストーラーの途中の選択肢で、「デスクトップ」と「サーバー」のどちらにするかを尋ねられたら、「サーバー」の方を選択する。
          「デスクトップ」だと一部のオプションが選べないため、インストール後の作業が増える可能性がある。
        * マルチテナント構成に *しない* 。 マルチテナント構成にするとGSPで接続可能にするまでに必要な手順が増える。
        * SIDは `XE` にすると、後々の設定が楽になるため、 `XE` にすることを推奨する。 
        * インストール直後、意外なIPアドレスで待ち受ける設定になっていることがある。  
          ローカルからTCP/IPで接続出来ない場合は、 `listener.ora` と `tnsnames.ora` を編集し、 `127.0.0.1` で待ち受けるようにする。  
          ファイルの編集後は、OSの再起動を推奨する(サービスのみ再起動しても、うまく設定が反映されないことがある)。
      * SQLServer
        * Microsoft SQL Server Management Studio (通称SSMS)は設定に有用なのでインストールする。
      * MySQL
        * 5系でテストしている。
        * 文字コードは `utf8mb4` にすること。(MySQLのutf8は、使えないunicode文字がある)
          * `C:\ProgramData\MySQL\MySQL Server 5.7\my.ini` を編集して `character-set-server=utf8mb4` 設定する。
1. [jdbc_test.properties](../src/test/resources/jdbc_test.properties)とDB接続の変更。
    * この接続情報を使ってテストを実行するので、jdbc_test.propertiesを修正するかDBのほうを変更して合わせる。
      * DB共通
        * 以下を作成または合わせる。
          * adminUser
          * DBまたはスキーマ(どちらが必要かはRDBMSの種類によって異なる)。名前は概ね `gsptest` で統一されている。  
            ただし、Oracleの場合はテスト実行時に作られるので、作成不要。
        * 以下はなくてもよい。
          * 一般ユーザ。  
            ただし、DB2はDBユーザをOSユーザとして用意しておく必要があるので用意しておくこと。
          * import/exportのテストで使用するスキーマ。概ね `gspanother` という名前で統一されている。なければテスト実行時に作成される。
      * DB2
        * DB2の起動に失敗する場合の対処法
          * 開発環境にログインする際のOSのユーザを、OSの `DB2ADMINS` グループに所属させる。
        * DB作成
          まだ作成していない場合は以下のように作成する。
          ```
          db2 => CREATE DATABASE gsptest
          DB20000I  CREATE DATABASE コマンドが正常に完了しました。
          ```
        * 権限付与
          `db2admin` はデフォルトでGRANTの権限を持っていないため、付与する。
          ```
          CONNECT TO gsptest
          GRANT DBADM ON DATABASE TO USER db2admin
          ```
        * ユーザ
          DB2はOSのユーザを使用するため、OSのユーザを用意する。
          本プロジェクトの標準では、IDが `gsptest` 、パスワードが `Gsptest123` である。ユーザは、OSの `DB2ADMINS` グループに所属させる。
      * SQLServer
        * saユーザがデフォルトで無効になっているので有効にする。SSMSを使用して設定変更すると容易である。
        * デフォルトでTCPが有効になっていないので有効にする。  
          参考：https://qiita.com/sugasaki/items/a95c2495085e32851707

1. [pom.xml](../pom.xml)にサードパーティ製JDBCドライバの依存関係定義
    * 使用するJDBCドライバのバージョンは、https://github.com/nablarch/nablarch-parent/blob/master/pom.xml を参考にする。Nablarchのテストで使用しているJDBCドライバが記載されている。
    * OracleのJDBCドライバとJavaのバージョンの関係は https://www.oracle.com/database/technologies/faq-jdbc.html を参照。
    * 2020年現在、GSPが対応しているRDBMSの最新のドライバはMavenセントラルに存在する。  
      もし、古いドライバでテストする必要がある場合は、JDBCドライバjarを入手してローカルリポジトリに入れて、pom.xmlに依存関係を定義すること。
      * ローカルインストールの例（バージョンは適宜変更してください）
        ```shell
        mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.2.0 -Dpackaging=jar
        mvn install:install-file -Dfile=db2jcc4.jar -DgroupId=com.ibm -DartifactId=db2jcc4 -Dversion=9.7.200.358 -Dpackaging=jar
        mvn install:install-file -Dfile=sqljdbc4.jar -DgroupId=com.microsoft -DartifactId=sqljdbc4 -Dversion=4.0 -Dpackaging=jar
        ```
      * pom.xmlに追加する依存関係定義の例
        ```xml:pom.xml
        <dependency>
            <groupId>com.ibm</groupId>
            <artifactId>db2jcc4</artifactId>
            <version>9.7.200.358</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.oracle</groupId>
            <artifactId>ojdbc6</artifactId>
            <version>11.2.0.2.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.microsoft</groupId>
            <artifactId>sqljdbc4</artifactId>
            <version>4.0</version>
            <scope>test</scope>
        </dependency>
        ```

## Usage
```
mvn -P all_test clean integration-test site
```
多分30分くらいかかる。  
Mojoのテスト、簡易JPAテストが実行されます。

## TestCase Level 1

1. [テストリソース](../src/test/resources/jp/co/tis/gsp/tools/dba/mojo)を眺める。
    * Mojo毎にフォルダがある。
    * Mojoの中にテストケース毎のフォルダがある。
    * テストケースフォルダの中にDB名フォルダがある。
    * DB名フォルダの中に、pom.xmlがある。
    * pom.xmlにはそのゴールで必要となるパラメータが指定されている。
2. [テストクラスとメソッド](https://github.com/coastland/gsp-dba-maven-plugin/blob/feature-10745/src/test/java/jp/co/tis/gsp/tools/dba/mojo/GenerateDdlMojoTest.java#L33)を眺める。
    * `@TestDBPattern(testCase=..., testDb=...)`でどのテストケース、どのDBでテストをするか制御している。<br />
        * `testCase` ・・・ どのテストケースのフォルダか。
        * `testDb`   ・・・ どのDBフォルダで実施するか。
    * 上記アノテーション情報を読み込んで`mojoTestFixtureList`を生成し、グルグル回す。
```java
	@Test
	@TestDBPattern(testCase = "type", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.h2,
			TestDB.sqlserver, TestDB.mysql })
	public void testType() throws Exception {

		// @TestDBPattern情報を読み込んで、テスト実行に必要な情報をDB分リストにしたものがmojoTestFixtureList。
		// 要するに指定されたDB分ループが回る。
		for (MojoTestFixture mf : mojoTestFixtureList) {

/** 準備フェーズ */
			// この Mojoフォルダ/テストケースフォルダ/DBフォルダ/pom.xml のFileオブジェクト
			// pom.xmlにgoal実行に必要なパラメータを定義しておく。
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

/** Goal実行フェーズ */
			// pom.xmlとgoal名、DB名を指定。このあたりはお決まりコード。
			GenerateDdlMojo mojo = this.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
			mojo.execute();

/** テスト結果検証フェーズ */
			// 検証。このMojoでは予め期待値ファイルを用意しておき、実際に出力されたファイルと突合する。
			
			// 実行して出力されたファイル群
			String actualPath = mojo.outputDirectory.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			
			// 用意しておいた期待値ファイル群
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "ddl");
			
			// 突合
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}

```

## TestCase Level 2

* あるDBだけで実行したい。
    * [mojoTest.properties](../src/test/resources/jp/co/tis/gsp/tools/dba/mojo/mojoTest.properties)で指定。
```shell
testDB=db2
```
* pom.xmlによるパラメータ指定の詳細
    * [親pom](../src/test/resources/jp/co/tis/gsp/tools/dba/mojo/testParentPom.xml)にDB接続パラメータなどを定義している。
    * さらに[settings.xml](../src/test/resources/settings.xml)でプロファイルを定義して、これを利用している。
    * 解決の流れ
        * [jdbc_test.properties](../src/test/resources/jdbc_test.properties)  
            * [settings.xml](../src/test/resources/settings.xml)  
                * [testParentPom.xml](../src/test/resources/jp/co/tis/gsp/tools/dba/mojo/testParentPom.xml)  
                    * 各テストケースのpom.xml。[例えば](../src/test/resources/jp/co/tis/gsp/tools/dba/mojo/ExecuteDdlMojo/type/db2/pom.xml)
    
    
## Troubleshoot

* Mojoテストクラスを、Eclipse上でJunit実行するとすぐに`AssertionError`っぽいエラーが出て落ちることがある。
    * *プロジェクトのフルクリア・フルビルド*をすれば直ります。

## JPA簡易検証

* 事前に、開発中の `gsp-dba-maven-plugin` をローカルリポジトリに install しておく必要があります。開発中のブランチで、`mvn install -DskipTests` を実施してください。
* `integration-test`フェーズで実施。`maven-invoker-plugin`プラグインを使用。メインフォルダは[it](../src/it)フォルダ。
* DB接続情報はMojoテストクラスで利用した[jdbc_test.properties](../src/test/resources/jdbc_test.properties)を使用。
* [simple-jpa-test](../src/it/simple-jpa-test)プロジェクトを各DBごとで使い回して実行。
    1. 上記プロジェクトの[各DBのedmファイル](../src/it/simple-jpa-test/src/main/resources)をインプットにして、generate-ddl、execute-ddl、generate-entityを実行。
    1. [テストメソッド](../src/it/simple-jpa-test/src/test/java/jp/co/tis/gsp/jpatest/AppTest.java#L31)を実行。
