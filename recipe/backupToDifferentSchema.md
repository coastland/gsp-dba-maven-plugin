# pom.xmlに指定されているスキーマとは異なるスキーマにデータをバックアップする

## 概要
* pom.xmlに定義されているスキーマとは異なるスキーマに対して実行する方法です。
* mvnコマンド実行時にプロファイルを指定して対象スキーマを切り替えます。
* pom.xmlのpropertiesにデフォルト挙動のスキーマ名を設定しておき、これをプロファイルでmvn実行時に変更する方式です。

## 具体例

* pom.xmlのproperties要素にデフォルトのスキーマ名を定義しておきます。  
* gspのconfiguration要素にはこのproperties要素で定義した値を参照するようにしておきます。
* 別スキーマ用のプロファイルを定義しておきます。ここではidに「TEST_BACKUP」を指定しました。
* 別スキーマ用プロファイルには、**`<db.schema>`** に別のスキーマ名(ここではTEST_BACKUP)を、そしてもう一つ重要なのが、**`<db.user>`**　にも別スキーマ名と同じ名前を指定して、スキーマ名＝ユーザ名となるようにして下さい。

pom.xml
```xml
<properties>
  <db.schema>HOGE</db.schema>
  <db.user>HOGE</db.user>
  <db.password>password</db.password>
  <db.jdbcDriver>com.ibm.db2.jcc.DB2Driver</db.jdbcDriver>
  <db.url>jdbc:db2://localhost:50000/sample</db.url>
  <db.adminUser>HOGE</db.adminUser>
  <db.adminPassword>password</db.adminPassword>
  <dba.entity.rootPackage>com.nablarch.example.app</dba.entity.rootPackage>
  <dba.entity.entityPackage>entity</dba.entity.entityPackage>
  <dba.useDB>db2</dba.useDB>
  <dba.classifier>${dba.useDB}</dba.classifier>
  <dba.erdFile>src/main/resources/entity/${dba.useDB}.edm</dba.erdFile>
  <dba.extraDdlDirectory>src/main/resources/extraDdl/${dba.useDB}</dba.extraDdlDirectory>
  <dba.dataDirectory>src/test/resources/data/${dba.useDB}</dba.dataDirectory>
  <dba.entity.javaFileDestDir>target/generated-sources/entity</dba.entity.javaFileDestDir>
</properties>

<!-- バックアップ用プロファイル ここから -->
<profiles>
  <profile>
    <id>BACKUP</id>
    <properties>
      <db.schema>TEST_BACKUP</db.schema>
      <db.user>TEST_BACKUP</db.user>
      <db.password>password</db.password>
    </properties>
  </profile>
</profiles>
<!-- バックアップ用プロファイル ここまで -->

<build>
	<plugins>
		<plugin>
			<groupId>jp.co.tis.gsp</groupId>
			<artifactId>gsp-dba-maven-plugin</artifactId>
			<version>3.2.0-SNAPSHOT</version>
			<configuration>
				<driver>${db.jdbcDriver}</driver>
				<url>${db.url}</url>
				<adminUser>${db.adminUser}</adminUser>
				<adminPassword>${db.adminPassword}</adminPassword>
				<user>${db.user}</user>
				<password>${db.password}</password>
				<schema>${db.schema}</schema>
				<dataDirectory>src/test/resources/data</dataDirectory>
				<erdFile>${dba.erdFile}</erdFile>
				<lengthSemantics>CHAR</lengthSemantics>
				<rootPackage>${dba.entity.rootPackage}</rootPackage>
				<entityPackageName>${dba.entity.entityPackage}</entityPackageName>
				<extraDdlDirectory>${dba.extraDdlDirectory}</extraDdlDirectory>
				<dataDirectory>${dba.dataDirectory}</dataDirectory>
				<useAccessor>true</useAccessor>
				<javaFileDestDir>${dba.entity.javaFileDestDir}</javaFileDestDir>
			</configuration>
			<executions>
				<!-- DDLをObjectBrowserERから生成する -->
				<execution>
					<id>generate-ddl</id>
					<phase>generate-sources</phase>
					<goals>
						<goal>generate-ddl</goal>
					</goals>
				</execution>
				<!-- DDLを実行する -->
				<execution>
					<id>execute-ddl</id>
					<phase>generate-sources</phase>
					<goals>
						<goal>execute-ddl</goal>
					</goals>
				</execution>
				<!-- Entityを生成する -->
				<execution>
					<id>generate-entity</id>
					<phase>generate-sources</phase>
					<goals>
						<goal>generate-entity</goal>
					</goals>
				</execution>
				<!-- データをロードする -->
				<execution>
					<id>load-data</id>
					<phase>pre-integration-test</phase>
					<goals>
						<goal>load-data</goal>
					</goals>
				</execution>
				<!-- ダンプを作る -->
				<execution>
					<id>export-schema</id>
					<phase>install</phase>
					<goals>
						<goal>export-schema</goal>
					</goals>
				</execution>
			</executions>
			<dependencies>
				<dependency>
					<groupId>com.ibm</groupId>
					<artifactId>db2jcc4</artifactId>
					<version>9.7.200.358</version>
				</dependency>
			</dependencies>
		</plugin>
	</plugins>
</build>	
```
* 上記のようなpom.xmlが用意出来たら、mvnコマンドでプロファイルを指定して実行します。  
  実行するゴールは、**generate-ddl** 、**execute-ddl** 、 **load-data** 。 -P オプションで実行！
```shell
## プロファイルBACKUPを-Pオプションで指定
mvn clean gsp-dba:generate-ddl gsp-dba:execute-ddl gsp-dba:load-data -P BACKUP
```
        
* <a name ="pos1">注意点
    * extraDdlDirectoryに用意されているSQLやedmファイルのview定義がスキーマ修飾されたSQL文だとダメです。
    extraDdl/db2/hogehoge.sql
    ```sql
    CREATE TABLE TAMORI.USERS(...　-- これはNG
    ```
