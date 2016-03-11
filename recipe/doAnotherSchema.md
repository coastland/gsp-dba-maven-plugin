# 別スキーマに実行したい

* pom.xmlに定義されているスキーマ名をmvn実行時に変更して行います。
* ただ多くの場合、これだけでは上手くいかない可能性があるので注意点なども記載しておきます。

まずmvn実行時にパラメータを変更する一般的なMavenのお話をします。  
pom.xml内に定義したproperties要素の定義値をmvn実行時に変更させる方法が一番わかりやすいのでこの方式でいきます。

pom.xml
```xml
<properties>
  <db.schema>HOGE</db.schema>
  <db.user>HOGE</db.user>
  ...
</properties>
```

## Maven実行時にパラメータを変更する方法

### 1. プロファイルで変更
pom.xmlにプロファイル設定を組み込んでおき、mvnコマンド実行時にプロファイルを指定することでpropertiesの定義値を変更出来ます。

pom.xml ※db.schemaに注目！
```xml
<!-- プロファイル指定しない時のプロパティ値 -->
<properties>
    <db.schema>HOGE</db.schema>
</properties>

<!-- バックアップ用のプロファイル -->
<profiles>
  <profile>
    <id>BACKUP</id>
    <properties>
      <db.schema>TEST_BACKUP</db.schema>
    </properties>
  </profile>
<profiles>

<!-- gspプラグインの設定 -->
<build>
　　<plugins>
　　　　<plugin>
　　　　　　<groupId>jp.co.tis.gsp</groupId>
       <artifactId>gsp-dba-maven-plugin</artifactId>
       <configuration>
         <schema>${db.schema}</schema>
       </configuration>
     </plugin>
   </plugins>
...
```

```shell
## プロファイルBACKUPを-Pオプションで指定
mvn clean install -P BACKUP

↓のように解釈されて実行
<configuration>
  <schema>TEST_BACKUP</schema>
</configuration>
```

### 2. -Dオプションで`<properties>`を変更

`<properties>`要素の値はmvnコマンド実行時の-Dオプションで上書き出来ます。
※`<configuration>`要素ではないですよ。。
```shell
#### プロファイルBACKUPを-Pオプションで指定
mvn clean install -Ddb.schema=TEST_BACKUP
```

## 実際にやってみる

* 以下のようなpom.xmlで説明します。
    * 下記の例では通常は**HOGE**スキーマにテーブルが作成されたりデータがロードされたりします。
    * ユーザ名とスキーマ名が同じHOGEです。

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

<build>
	<plugins>
		<plugin>
			<artifactId>maven-jar-plugin</artifactId>
			<executions>
				<execution>
					<id>default-jar</id>
					<configuration>
						<classifier>${dba.classifier}</classifier>
					</configuration>
				</execution>
			</executions>
		</plugin>
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

* これを一時的に**TEST_BACKUP**スキーマに実行されるように考えてみます。    
  gspプラグインのスキーマ指定はplugin要素のconfiguration要素のschemaに設定するわけですが、今設定値は**${db.schema}**となっていて、これは上で定義されているproperties要素の**db.schema**の値であるとわかります。

  スキーマを変更するだけならdb.schemaを上書きすれば終わります。しかし多くの場合これでは上手くいきません。

  それは、extraDdlDirectoryにあるSQLやedmファイルのView定義にあるユーザが自前で用意したSQLが原因です。   
  スキーマだけを変更すると、ユーザ名とスキーマ名が異なることになるので、自前SQLがこける可能性が高い、望むような結果を得ることが出来ないと思われます。

 * 対応策は２つ。
    - バックアップ用のスキーマ名と同じユーザを作成して、ユーザ名とスキーマ名が同じようになるようにパラメータを変更する。
      - ユーザ名とスキーマ名が同じであればスキーマ修飾のことは考えなくてよくなります。※ただし、[注意点](#pos1)に気を付けてください。
    - extraDdlDirectoryとedmファイルなど自前SQLが存在するファイル・ディレクトリを、バックアップスキーマ用にも用意してmvn実行時にそのファイル・ディレクトリを参照するようにする。
      - バックアップ用スキーマ名で修飾したSQLを用意することになります。

* ここではバックアップスキーマ名と同じユーザを用意した方法で説明します。

    * -Dオプションで上書き    
        properties要素は、mvnコマンド -Dオプションで上書き出来るので。
        ```shell
        mvn clean install -Ddb.schema=TEST_BACKUP　-Ddb.user=TEST_BACKUP -Ddb.password=password
        ```

    * プロファイルで変更    
                  コマンドがパラメータだらけになると打ち損じることもあるので。  
                 下記のバックアップ用プロファイルを追記し、mvn実行時にプロファイルを指定します。
         
         pom.xml
        ```xml
        <properties>
          <db.schema>HOGE</db.schema>
　                     <db.user>HOGE</db.user>
　                     <db.password>password</db.password>
        ...
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
        ```
        
        ```shell
        ## プロファイルBACKUPを-Pオプションで指定
        mvn clean install -P BACKUP
        ```
        
        ちなみにプロファイルは複数指定出来ます。BACKUPとdbaプロファイルを指定したい場合は
        ```shell
        ## プロファイルBACKUPを-Pオプションで指定
        mvn clean install -P BACKUP,dba
        ```
        
* <a name ="pos1">注意点
    * extraDdlDirectoryに用意されているSQLやedmファイルのview定義がスキーマ修飾されたSQL文だとダメです。
    extraDdl/db2/hogehoge.sql
    ```sql
    CREATE TABLE TAMORI.USERS(...
    ```
