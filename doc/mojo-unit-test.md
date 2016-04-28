GSP ユニットテスト
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
* JPA簡易検証。
    * integration-testで生成されたエンティティを使って簡単なJPA(Eclpselink)での検証をします。

## SetUp
1. [jdbc_test.properties](../src/test/resources/jdbc_test.properties)とDB接続の変更。
    * この接続情報を使ってテストを実行するので、jdbc_test.propertiesを修正するかDBのほうを変更して合わせる。
    * 一般ユーザはいなくてもよい。ただDB2はDBユーザをOSユーザとして用意しておく必要があるので用意しておくこと。
1. [](../pom.xml)のサードパーティのJDBCドライバの依存関係の定義
    * Oracle、DB2、SQLServerのJDBCドライバはMavenセントラルにいません。  
      JDBCドライバjarを入手してローカルリポジトリに入れて、pom.xmlに依存関係を定義して下さい。


        * ローカルインストールの例（バージョンとかよしなに変更して下さい）
    
        ```shell
        mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.2.0 -Dpackaging=jar
        mvn install:install-file -Dfile=db2jcc4.jar -DgroupId=com.ibm -DartifactId=db2jcc4 -Dversion=9.7.200.358 -Dpackaging=jar
        mvn install:install-file -Dfile=sqljdbc4.jar -DgroupId=com.microsoft -DartifactId=sqljdbc4 -Dversion=4.0 -Dpackaging=jar
        ```
        
        * pom.xmlに追加する依存関係定義の例
        
        ```xml
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
```
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
