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
<div class="highlight highlight-text-xml"><pre>&lt;<span class="pl-ent">properties</span>&gt;
  &lt;<span class="pl-ent">db</span>.schema&gt;HOGE&lt;/<span class="pl-ent">db</span>.schema&gt;
  &lt;<span class="pl-ent">db</span>.user&gt;HOGE&lt;/<span class="pl-ent">db</span>.user&gt;
  &lt;<span class="pl-ent">db</span>.password&gt;password&lt;/<span class="pl-ent">db</span>.password&gt;
  &lt;<span class="pl-ent">db</span>.jdbcDriver&gt;com.ibm.db2.jcc.DB2Driver&lt;/<span class="pl-ent">db</span>.jdbcDriver&gt;
  &lt;<span class="pl-ent">db</span>.url&gt;jdbc:db2://localhost:50000/sample&lt;/<span class="pl-ent">db</span>.url&gt;
  &lt;<span class="pl-ent">db</span>.adminUser&gt;HOGE&lt;/<span class="pl-ent">db</span>.adminUser&gt;
  &lt;<span class="pl-ent">db</span>.adminPassword&gt;password&lt;/<span class="pl-ent">db</span>.adminPassword&gt;
  &lt;<span class="pl-ent">dba</span>.entity.rootPackage&gt;com.nablarch.example.app&lt;/<span class="pl-ent">dba</span>.entity.rootPackage&gt;
  &lt;<span class="pl-ent">dba</span>.entity.entityPackage&gt;entity&lt;/<span class="pl-ent">dba</span>.entity.entityPackage&gt;
  &lt;<span class="pl-ent">dba</span>.useDB&gt;db2&lt;/<span class="pl-ent">dba</span>.useDB&gt;
  &lt;<span class="pl-ent">dba</span>.classifier&gt;${dba.useDB}&lt;/<span class="pl-ent">dba</span>.classifier&gt;
  &lt;<span class="pl-ent">dba</span>.erdFile&gt;src/main/resources/entity/${dba.useDB}.edm&lt;/<span class="pl-ent">dba</span>.erdFile&gt;
  &lt;<span class="pl-ent">dba</span>.extraDdlDirectory&gt;src/main/resources/extraDdl/${dba.useDB}&lt;/<span class="pl-ent">dba</span>.extraDdlDirectory&gt;
  &lt;<span class="pl-ent">dba</span>.dataDirectory&gt;src/test/resources/data/${dba.useDB}&lt;/<span class="pl-ent">dba</span>.dataDirectory&gt;
  &lt;<span class="pl-ent">dba</span>.entity.javaFileDestDir&gt;target/generated-sources/entity&lt;/<span class="pl-ent">dba</span>.entity.javaFileDestDir&gt;
&lt;/<span class="pl-ent">properties</span>&gt;

<span class="pl-c">&lt;!-- バックアップ用プロファイル ここから --&gt;</span>
&lt;<span class="pl-ent" style="color: #F01136;"><b>profiles</b></span>&gt;
  &lt;<span class="pl-ent"><b>profile</b></span>&gt;
    &lt;<span class="pl-ent"><b>id</span>&gt;BACKUP&lt;/<span class="pl-ent">id</b></span>&gt;
    &lt;<span class="pl-ent"><b>properties</b></span>&gt;
      &lt;<span class="pl-ent"><b>db</span>.schema&gt;TEST_BACKUP&lt;/<span class="pl-ent">db</span>.schema</b>&gt;
      &lt;<span class="pl-ent"><b>db</span>.user&gt;TEST_BACKUP&lt;/<span class="pl-ent">db</span>.user</b>&gt;
      &lt;<span class="pl-ent"><b>db</span>.password&gt;password&lt;/<span class="pl-ent">db</span>.password</b>&gt;
    &lt;/<span class="pl-ent"><b>properties</b></span>&gt;
  &lt;/<span class="pl-ent"><b>profile</b></span>&gt;
&lt;/<span class="pl-ent"><b>profiles</b></span>&gt;
<span class="pl-c">&lt;!-- バックアップ用プロファイル ここまで --&gt;</span>

&lt;!-- 以下省略 --&gt;

</div>

* 上記のようなpom.xmlが用意出来たら、mvnコマンドでプロファイルを指定して実行します。  
  実行するゴールは、**generate-ddl** 、**execute-ddl** 、 **load-data** 。 -P オプションで実行。

```shell
## プロファイルBACKUPを-Pオプションで指定
mvn clean gsp-dba:generate-ddl gsp-dba:execute-ddl gsp-dba:load-data -P BACKUP
```
        
* <a name ="pos1">注意点
    * extraDdlDirectoryに用意されているSQLやedmファイルのview定義のSQLがスキーマ修飾されていると、結局そのスキーマのテーブルが作成されてしまうのでバックアップとしては不完全なものとなります。
    extraDdl/db2/hogehoge.sql
    ```sql
    CREATE TABLE TAMORI.USERS(...　-- これはNG。TEST_BACKUPスキーマに作成したいのにTAMORIスキーマに出来てしまう。
    ```
