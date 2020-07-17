# Back up data to a Schema that is Different from the Schema Specified in pom.xml

## Summary
* This method is executed for a schema that is different from the schema defined in pom.xml.
* Switches the target schema by specifying a profile when running the mvn command.
* It is a method to configure the schema name of default behavior in properties of pom.xml and change it when mvn is executed by profile.
* This procedure can be used outside of MySQL.

## Specific examples

* Define a default schema name in the property element of pom.xml.
* The configuration element of gsp should reference the value defined in the property element.
* Define a profile for another schema. Here, the id is specified as "TEST_BACKUP" .
* In a different schema profile, specify a different schema name (here TEST_BACKUP) for **`<db.schema>`**, another important point is to specify the same name as the different schema name for **`<db.user>`**,　so that schema name = username.

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

<span class="pl-c">&lt;!-- Backup Profile starts from here --&gt;</span>
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
<span class="pl-c">&lt;!-- Backup profile ends here --&gt;</span>

&lt;!-- Rest is omitted --&gt;

</div>

* When pom.xml like the above is prepared, execute after specifying the profile with the mvn command.
  The goals to execute are **generate-ddl**, **execute-ddl** and **load-data**. Execute with option -P.

```shell
## Specify profile BACKUP with option -P.
mvn clean gsp-dba:generate-ddl gsp-dba:execute-ddl gsp-dba:load-data -P BACKUP
```
        
* Notes
    * If the SQL in the extraDdlDirectory or the view definition of the edm file is schema-qualified, then the backup is incomplete as the table for that schema will be created in the end.
    extraDdl/db2/hogehoge.sql
    ```sql
    CREATE TABLE TAMORI.USERS(... -- This is not OK. Although it has to be created in the test_backup schema, it is created in the Tamori schema.
    ```
