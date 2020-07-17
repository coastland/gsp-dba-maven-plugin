# gsp-dba-maven-plugin

The gsp-dba-maven-plugin is a tool for automating the routine work of DBA so that developers can focus on data modeling tasks.


The following operations can be performed easily.

* Generate and execute DDL from the ER diagram.
* Generate an entity class corresponding to the database table.
* Register the data in CSV format to the database.
* Acquire the dump file of the database schema.
* Reflect the dump file of the repository in the local environment.

> * To generate DDL from the ER diagram, [SI Object Browser ER](http://www.sint.co.jp/siob/er/) must be used as a modeling tool.
> If any other tools are used, the DDL generation function cannot be used. Using the DDL execution function is not recommended.
> If DDL is executed using another method, functions excluding DDL generation and DDL execution can be used without any problems.

> * gsp-dba-maven-plugin is intended for use in the development phase. The main target of this plugin is the local DB of developers.  
Using the plugin in a production environment is not recommended.  
 

The intended data modeling is based on the following documents.
* [Immutable Data Models (Beginner's Guide)](http://www.slideshare.net/kawasima/ss-40471672)  
* [Immutable Data Model (Generation Edition)](http://www.slideshare.net/kawasima/ss-44958468)  

## Overview of goal

* [generate-ddl](#generate-ddl) Analyze the data model and generate DDL.
* [execute-ddl](#execute-ddl) Execute DDL.
* [load-data](#load-data) Register the data defined in CSV format to the database.
* [generate-entity](#generate-entity) Parse the specified schema and generate the Entity class.
* [export-schema](#export-schema) Dump the database schema.
* [import-schema](#import-schema) Import the dump file obtained from the repository.

The Mojo classes for each goal are in the [jp.co.tis.gsp.tools.dba.mojo](../src/main/java/jp/co/tis/gsp/tools/dba/mojo) package.

Different databases have different behavior and restrictions.
For more information, see **Database Support Status**.

## How to Use

### Configuration

The plugin can be used by adding the following configuration to pom.xml.

```xml
<pluginManagement>
  <plugins>
    <plugin>
      <groupId>jp.co.tis.gsp</groupId>
      <artifactId>gsp-dba-maven-plugin</artifactId>
      <version>
        Version of gsp-dba-maven-plugin used
      </version>
      <dependencies>
        <!-- Modify the JDBC driver according to the DB product used in the project. -->
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

### Java11 configuration

Add the following configuration to pom.xml for using the plugin with Java11.

```xml
<pluginManagement>
  <plugins>
    <plugin>
      <groupId>jp.co.tis.gsp</groupId>
      <artifactId>gsp-dba-maven-plugin</artifactId>
      <!-- gsp-dba-maven-plugin version 4.4.0 or later can be used with Java 11. -->
      <version>4.4.0</version>
      <dependencies>
        <!-- Modify the JDBC driver according to the DB product used in the project. -->
        <dependency>
          <groupId>com.oracle</groupId>
          <artifactId>ojdbc6</artifactId>
          <version>11.2.0.2.0</version>
        </dependency>
        <!-- Make sure to add the following -->
        <dependency>
          <groupId>javax.activation</groupId>
          <artifactId>javax.activation-api</artifactId>
          <version>1.2.0</version>
        </dependency>
        <dependency>
          <groupId>javax.xml.bind</groupId>
          <artifactId>jaxb-api</artifactId>
          <version>2.3.0</version>
        </dependency>
        <dependency>
          <groupId>com.sun.xml.bind</groupId>
          <artifactId>jaxb-core</artifactId>
          <version>2.3.0</version>
        </dependency>
        <dependency>
          <groupId>com.sun.xml.bind</groupId>
          <artifactId>jaxb-impl</artifactId>
          <version>2.3.0</version>
        </dependency>
        <dependency>
          <groupId>javax.annotation</groupId>
          <artifactId>javax.annotation-api</artifactId>
          <version>1.3.2</version>
        </dependency>
      </dependencies>
    </plugin>
  </plugins>
</pluginManagement>
```

### Parameters of common goal

The following parameters are common to all goals.
Set a corresponding value.

| Configuration value   | Required | Description                                                            |
|:---------------|:-----:|:----------------------------------------------------------------|
| driver         | ○     | JDBC driver used.                                         |
| url            | ○     | URL of the database. jdbc:subprotocol:subname format.            |
| adminUser      | ○     | Admin user name of database. `sys` cannot be specified for Oracle. Specify a database creation user or a user with DBADM authority in the target database for DB2 (specifying db2admin will result in an error depending on the database version). |
| adminPassword  | ×     | Password for the user configured in adminUser.                        |
| user           | ○     | Database username. `sys` cannot be specified for Oracle. It is always converted to lower case for PostgreSQL. |
| password       | ×     | Password for the user.                             |
| schema         | ×     | Schema name of the database. <br /> Cannot be specified for H2Database and is always interpreted as PUBLIC schema. <br /> Cannot be specified for MySQL and database name of jdbc URL is configured as the schema name. <br /> Example: jdbc:mysql://localhost:3306/gspdb → gspdb is internally used as the schema name. <br /> If the schema is not specified in other DBs, it is interpreted that the same schema name as the user name is used. It is always converted to lower case for PostgreSQL, and to upper case for H2, DB2 and Oracle. |
| dmpFile        | ×     | Dump file name. If not specified, the file name will be [schema name].dmp. |
|optionalDialects | ×    | Use FQCN of [dialect class](#lnk_dialect). |
|onError | ×    | Used in generate-ddl and load-data. Specifies the behavior when an error occurs during SQL execution. <br />`abort` (default)     Aborts the process. <br />`continue`    Continues the process. |

 * optionalDialects specification method  
 To change the Dialect class that is to be used, the database and corresponding Dialect class are defined in the following format.

```xml
<configuration>
  <optionalDialects>
    <oracle>jp.co.tis.gsp.tools.dba.dialect.CustomOracleDialect</oracle>
  </optionalDialects>
</configuration>
```

 * <a name ="lnk_dialect"></a> Dialect
    * Dialect is a class that defines appropriate behavior by taking into account the specifications of each DB, and exists for each DB.
    * It is managed by the package [jp.co.tis.gsp.tools.dba.dialect](../src/main/java/jp/co/tis/gsp/tools/dba/dialect).
    * By default, gsp-dba-maven-plugin determines the Dialect class of the corresponding DB (prepared by gsp) based on the JDBC URL. <br /> If there are any problems with the Dialect class prepared by gsp-dba-maven-plugin, the behavior of the class can be changed by preparing the optionalDialects parameter and customized Dialect class mentioned above.

### generate-ddl

Analyzes the data model and generates DDL.
The correspondence between the generated DDL and file name is as follows

| DDL type        | File name                                    |
|:-----------------|:----------------------------------------------|
| Table definition     | 10_CREATE_<Default name>.sql                    |
| Index definition | 20_create_<Physical name of index>.sql |
| Foreign key definition | 30_CREATE_FK_<Table name><serial number>.sql |           |
| View definition | 40_create_<Physical name of view>.sql |

For the rules to reflect auto-numbering in DDL, [Click here](./recipe/spec-generateDdl.md).


    Define objects of the data model as "logical/ physical model".
    The target DDL is not generated when defined as "logical model only" or "physical model only".
    When setting the default value for a string or date type column, an error will occur during execute-ddl if the value is not enclosed in single quotes.


To use, add the following to pom.xml.

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      Version of gsp-dba-maven-plugin used
    </version>
    <executions>
      <execution>
        <id>generate-ddl</id>
        <phase>generate-sources</phase>
        <goals>
          <goal>generate-ddl</goal>
        </goals>
        <configuration>
          <! -- Add Configuration -->
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```

#### Available parameters

| Configuration value   | Required | Description                                                            |
|:---------------------------|:-----:|:----------------------------------------------------------------|
| erdFile                    | ○     | Path of the erd file. Specifies a relative path from the work directory. |
| outputDirectory            | ×     | Output directory of DDL. Default is "target/ddl".             |
| lengthSemantics            | ×     | Length semantics. The default is bytes.                        |
| ddlTemplateFileDir         | ×     | Specifies the directory where the project-specific DDL template is placed with a relative path from the work directory. |
| allocationSize            | ×     | Increment value of sequence generation SQL (INCREMENT BY). Default is "1". <br /> The values of allocationSize and allocationSize of [generate-entity](#generate-entity) must be the same. <br />(eclipseLink) https://wiki.eclipse.org/Introduction_to_EclipseLink_JPA_(ELUG)  |
To customize the template, see [Example of Template Customization for Use with Generate-ddl](./recipe/custom-DdlTemplate.md).


### execute-ddl

* Executes DDL.
* Executes files in ascending order of the file names when multiple files are to be executed.
* Creates the specified user if the user specified in the parameter user does not exist.
* Creates the specified schema if the schema specified by the schema parameter does not exist.
  * Does not create a schema for MySQL. Must be prepared in advance with a CREATE DATABASE statement.
* Deletes all tables, views and sequences in the specified schema first if the schema specified by the parameter schema exists.

To use, add the following to pom.xml.

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      Version of gsp-dba-maven-plugin used
    </version>
    <executions>
      <execution>
        <id>execute-ddl</id>
        <phase>generate-sources</phase>
        <goals>
          <goal>execute-ddl</goal>
        </goals>
        <configuration>
          <! -- Add Configuration -->
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```

#### Available parameters

| Configuration value   | Required | Description                                                            |
|:---------------------|:-----:|:----------------------------------------------------------------|
| ddlDirectory         | ×     | DDL placement directory. Default is "target/ddl".             |
| extraDdlDirectory    | ×     | The directory where additional SQL files to execute are placed.                 |

### load-data

Registers the data defined in CSV format in the specified schema of the database.

To use, add the following to pom.xml.

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      Version of gsp-dba-maven-plugin used
    </version>
    <executions>
      <execution>
        <id>load-data</id>
        <phase>pre-integration-test</phase>
        <goals>
          <goal>load-data</goal>
        </goals>
        <configuration>
          <! -- Add Configuration -->
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```


#### Available parameters

| Configuration value                 | Required  | Description                                                                                  |
|:-----------------------|:-----:|:----------------------------------------------------------------------------|
| dataDirectory          | ○     | Data file placement directory.                                          |
| specifiedEncodingFiles | ×     | Configure when specifying the character code of a data file. Default is "Windows-31J". |


* How to specify specifiedEncodingFiles

When specifying the character code of a data file, define the file name and corresponding character code in the following format.

```xml
<configuration>
  <specifiedEncodingFiles>
    <aa.csv>UTF-8</aa.csv>
    <bb.csv>UTF-8</bb.csv>
  </specifiedEncodingFiles>
</configuration>
```


#### Data format
Create the data and data file in the following format.

* The file name is *physical name of the table*.csv.
* The first row is the physical name of the column (:column type name). Some DBs are automatically estimated and configured without specifying the type name.
* Test data is included from the second row.
* Items with only full-width spaces and half-width spaces are handled as null. To change, see [Example of Dialect Class Customization](./recipe/custom-Dialect.md).

An example of the data is described.


    ITEM,VARCHAR_ITEM:VARCHAR,DATE_ITEM:DATE,TIMESTAMP_ITEM:TIMESTAMP,ARRAY_ITEM:ARRAY
    item,item0000000000000000,2014-12-13,2014-12-13 4:15:16,"item 1, item 2, item 3"


#### Data types that can be registered

The data types that can be registered vary depending on the database.
For more information, see [Support status for load-data](./doc/db-status.md#support-status-for-load-data).

#### Load data into a different schema

To load data in a different schema, include the following in pom.xml.

* Define multiple execution tags. In the following example, the ids of load-data-without-schema and load-data-with-schema as information that identifies execution tags are defined.
* Schema parameter is not specified in load-data-without-schema.
* SCHEMA_TEST is not specified in the schema parameter of load-data-with-schema.

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      Version of gsp-dba-maven-plugin used
    </version>
    <executions>
      <execution>
        <id>load-data-without-schema</id>
        <phase>pre-integration-test</phase>
        <goals>
          <goal>load-data</goal>
        </goals>
        <configuration>
          <! -- Add Configuration -->
        </configuration>
      </execution>
      <execution>
        <id>load-data-with-schema</id>
        <phase>pre-integration-test</phase>
        <goals>
          <goal>load-data</goal>
        </goals>
        <configuration>
          <! -- Add Configuration -->
          <!-- SCHEMA_TEST is specified as the schema name below-->
          <schema>SCHEMA_TEST</schema>
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```

Execute the respective execution with the following execution command.

````
  mvn gsp-dba:load-data@load-data-without-schema gsp-dba:load-data@load-data-with-schema
````

### generate-entity

Generates entities corresponding to the table from the database metadata. For various annotations that are added during auto-generation, see [Annotations used by entities](recipe/spec-generatedEntity.md).
The generation process uses customized S2JDBC-Gen.

To use, add the following to pom.xml.

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      Version of gsp-dba-maven-plugin used
    </version>
    <executions>
      <execution>
        <id>generate-entity</id>
        <phase>generate-sources</phase>
        <goals>
          <goal>generate-entity</goal>
        </goals>
        <configuration>
          <! -- Add Configuration -->
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```


#### Available parameters

| Configuration value                 | Required  | Description                                                                                  |
|:-----------------------|:-----:|:--------------------------------------------------------------------------|
| ignoreTableNamePattern | ×    | Table name to be excluded from auto-generation. Specify with a regular expression.                      |
| versionColumnNamePattern | ×    | Specifies the column name to be annotated with @Version annotation using regular expression. Default is "VERSION([_]?NO)?". <br /> For other conditions to be assigned, see [spec-generatedEntity.md](recipe/spec-generatedEntity.md). |
| entityPackageName      | ×    | Package name of the entity. Default is "entity".                    |
| genDialectClassName    | ×    |  Implementation class name of the Dialect interface of S2JDBC-Gen. <br> To customize, see [Example of GenDialect Class Customization](./recipe/custom-genDialect.md). <br> |
| dialectClassName       | ×    | Implementation class name of the Dialect interface of S2JDBC. <br /> Note that the ExtendedGenDialect class will not be used if a class name that is different from the registration key class of ExtendedGenDialect class provided by the gsp-dba-maven-plugin is specified. |
| rootPackage            | ○    |  Root package name.                                                      |
| useAccessor            | ×    | Whether to use an accessor. Default is "false".                   |
| entityType             | ×    | Type of entity to generate. Select between jpa and doma. Default is "jpa". <br /> When specifying doma, specify "java/gsp_doma_entity.ftl" in the entityTemplate. |
| entityTemplate         | ×    | Auto-generated template of the entity. Default is "java/gsp_entity.ftl". |
|javaFileDestDir        | ×      | Directory where the java file of the generated entity is placed |
|templateFilePrimaryDir | ×      |Path up to entityTemplate. Default is "src/main/resources/org/seasar/extension/jdbc/gen/internal/generator/tempaltes". <br> Usage example: If the path to the file is "src/main/resource/template/gsp_template.ftl, configure <br> entityTemplate: gsp_template.ftl <br> templateFilePrimaryDir:src/main/resource/template <br> respectively. |
| allocationSize         | ×     | allocationSize of @SequenceGenerator. Default is "1". <br /> Make sure that the above mentioned allocationSize and allocationSize of [generate-ddl](#generate-ddl) are the same. <br />(eclipseLink) https://wiki.eclipse.org/Introduction_to_EclipseLink_JPA_(ELUG) |
| useJSR310         | ×     | Whether to generate the entity corresponding to JSR301. Default is "false".                   |
To customize the template, see [Example of Template Customization for Use with Generate-entity](./recipe/custom-EntityTemplate.md).


### export-schema

Exports the dump file of the specified schema.  
This is achieved by internally calling the DBMS-specific export function.
However, since the DBMS-specific export function cannot be used for DB2 and SqlServer, it is replaced with the general mode that packages DDL and CSV files.  
* [Advanced general mode](#General-mode)

    By combining export-schema with maven-install-plugin and maven-deploy-plugin, the schema can be installed in the local environment or deployed to a remote repository possible.
    

The file name of the dump file is in the following format.

    Project Artifact Id + "-testdata-" + Project Version + ".jar"


To use, add the following to pom.xml.

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      Version of gsp-dba-maven-plugin used
    </version>
    <executions>
      <execution>
        <id>export-schema</id>
        <phase>install</phase>
        <goals>
          <goal>export-schema</goal>
        </goals>
        <configuration>
          <! -- Add Configuration -->
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```


#### Available parameters

| Configuration value                 | Required  | Description                                                                                  |
|:-----------------------|:-----:|:--------------------------------------------------------------------------------------|
| outputDirectory        | ×     | Path of the directory to which the database schema is exported. Default is "target/dump". |
| ddlDirectory           | ×     | Used with the [General mode](#General-mode). Specifies the DDL directory.                                  |
| extraDdlDirectory      | ×     | Used with the [General mode](#General-mode). Specify the additional DDL directory.                              |

#### general mode
-The operation of the export process that works for DB2 and SQL Server does not use the DBMS-specific export function.
- The schema export process is replaced by packaging the CSV data and DDL files.
- CSV data is output by the gsp-dba-maven-plugin. Prepare DDL and additional DDLs in advance and specify the location with the parameters `ddlDirectory` and `extraDdlDirectory` mentioned above.
- The character encoding of the output CSV data is UTF-8.
- Refer to [Click here](doc/db-status.md#Restrictions-of-general-exportschemaimportschema) for data types that can be handled and restrictions.
-The process flow is described below.
    1. The table data of the specified schema is output as CSV data (to dataDirectory).
    2. DDL files under the parameter `ddlDirectory` mentioned above are collected.
    3. DDL files under the parameter `extraDdlDirectory` mentioned above are collected.
    4. The above 3 resources are packaged in a jar file.
    ```
    ex.) export-schema.jar
           ├─ META-INF/
           ├─ dataDirectory/
           ├─ ddlDirectory/
           └─ extraDdlDirectory/
    ```


### import-schema

Obtains the dump file from the repository and imports the file into the local database.

To use, add the following to pom.xml.

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      Version of gsp-dba-maven-plugin used
    </version>
    <configuration>
    <! -- Add Configuration -->
    </configuration>
  </plugin>
</plugins>
```

#### Available parameters

| Configuration value                 | Required  | Description      
|:-----------------------|:-----:|:--------------------------------------------------------------------------------------|
| inputDirectory         | ×     | Directory where the dump file is placed. Default is "target/dump".                       |
| groupId                | ×     | Group ID of the dump file. Default is the project group ID.                  |
| artifactId             | ×     | Artifact ID of the dump file. Default is the project artifact ID.  |
| version                | ×     | Version of the dump file. Default is the project version. |

#### <a name="importSchemaGeneral"></a> general mode
- Since the general mode export is used for DB2 and SQLServer, importing it will import the schema.
- Refer to [Click here](doc/db-status.md#Restrictions-of-general-exportschemaimportschema) for data types that can be handled and restrictions.
-The process flow is described below.
    1. Acquires and extracts the export jar file output in the general mode.
    2. DDL files of `ddlDirectory` and `extraDdlDirectory` are executed.
    3. CSV data in the `dataDirectory` is loaded.

### Support status of each database

* Refer to [Click here](./doc/db-status.md).

### Restrictions

Each goal has the following restrictions.

#### execute-ddl

* Oracle<br />
  When the user name and schema name do not match, a new schema is created. <br />
  When a new schema is created with this plugin, the login password of the user created simultaneously will be the same as the schema name.
* DB2<br />
  Only the same user name and schema name can be specified.
* H2<br />
  If you ** configure the same value** for the user name and the schema name, it will fail.

#### generate-entity

* MS SQL Server<br />
   Comments for tables and columns configured using extended entities are not reflected in the java file.
* H2<br />
  If you ** configure the same value** for the user name and the schema name, it will fail. <br />
  If the erd file contains a View definition, it will fail.

#### export-schema

* Common to all databases <br />
  Not guaranteed to work if the database is not running on the same machine as the machine on which gsp-dba-maven-plugin is running.  

  However, this can be achieved by running it in the [general mode](#general-mode).  
  For more information on the general mode of the existing dialect export-schema, refer to [Implementation of General Mode Export/Import](recipe/custom-Dialect-generalExport.md).
  
  Schema refers to the one specified in the schema parameter. <br />
  The schema specified in the erd file is not referenced.
  
* MS SQL Server<br />
  Works with [General mode](#general-mode).
* DB2<br />
  Works with [General mode](#general-mode).

#### import-schema

* Common to all databases <br />
  Not guaranteed to work if the database is not running on the same machine as the machine on which gsp-dba-maven-plugin is running.

  However, this can be achieved by running it in the [general mode](#importSchemaGeneral).  
  For more information on the general mode of the existing dialect import-schema, refer to [Implementation of General Mode Export/Import](recipe/custom-Dialect-generalExport.md).
  
  Schema refers to the one specified in the schema parameter. <br />
  The schema specified in the erd file is not referenced.
    
* MS SQL Server<br />
  Works with [General mode](#importSchemaGeneral).
* DB2<br />
  Works with [General mode](#importSchemaGeneral).

#### load-data
* Common to all databases <br />
  Schema refers to the one specified in the schema parameter. <br />
  The schema specified in the erd file is not referenced.
  
## License

gsp-dba-maven-plugin is distributed under Apache License 2.0.

* http://www.apache.org/licenses/LICENSE-2.0.txt
