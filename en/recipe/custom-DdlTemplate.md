## Example of Template Customization for Use with Generate-ddl

This section describes how to customize the template used by generate-ddl goal.

The gsp-dba-maven-plugin uses FreeMaker as the template engine when creating DDL.
Therefore, create the template by following the FreeMaker rules during customization. <br />

As an example, naming the sequence as "table_name_column_name_SEQ" is described below.

> **Before using your own template, the impact of `generate-entity` on the template must be checked.** <br />
> In this example, the template for the `generate-entity` goal must also be modified since `@SequenceGenerator` of the entity class requires to be modified.
> For information on how to modify, see [Modify the generate-entity template
](#Modify-the-generate-entity-template) given below.

```diff
CREATE TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name} (
<#foreach column in entity.columnList>
${column.name} ${column.dataType}<#if column.length != 0>(${column.length}<#if column.scale != 0>,${column.scale}</#if><#if lengthSemantics==LengthSemantics.CHAR && (column.dataType == "VARCHAR2" || column.dataType == "CHAR")> CHAR</#if>)</#if><#if column.isArray()> ARRAY</#if><#if column.defaultValue?has_content> DEFAULT ${column.defaultValue} </#if><#if !column.isNullable()> NOT NULL </#if><#if column_has_next>,</#if>
</#foreach>
);
<#if entity.label?has_content>COMMENT ON table <#if entity.schema??>${entity.schema}</#if>${entity.name} is '${entity.label}';</#if>
<#foreach column in entity.columnList>
<#if column.label?has_content>COMMENT ON column <#if entity.schema??>${entity.schema}</#if>${entity.name}.${column.name} is '${column.label}';</#if>
</#foreach>
<#foreach column in entity.columnList>
- <#if column.isAutoIncrement()>CREATE SEQUENCE <#if entity.schema??>${entity.schema}</#if>${column.generatorKeyName} increment by 1 start with 1;
+ <#if column.isAutoIncrement()>CREATE SEQUENCE <#if entity.schema??>${entity.schema}</#if>${entity.name}_${column.generatorKeyName} increment by 1 start with 1;
</#if>
</#foreach>
```

The following DDL is output for the template:

```sql
CREATE TABLE BANK_DATA_RECORD (
  DATA_RECORD_ID NUMBER(9) NOT NULL ,
  RECEIVING_BANK_NUMBER CHAR(4 CHAR),
  RECEIVING_BRANCH_NUMBER CHAR(3 CHAR),
  DEPOSIT_TYPE CHAR(1 CHAR),
  ACCOUNT_NUMBER CHAR(7 CHAR),
  RECIPIENT_NAME CHAR(30 CHAR),
  TRANSFER_AMOUNT NUMBER(10),
  HEADER_RECORD_ID NUMBER(9) NOT NULL
);
COMMENT ON table BANK_DATA_RECORD is 'Bank Data Record';
COMMENT ON column BANK_DATA_RECORD.DATA_RECORD_ID is 'Record ID';
COMMENT ON column BANK_DATA_RECORD.RECEIVING_BANK_NUMBER is 'Recipient Bank Number';
COMMENT ON column BANK_DATA_RECORD.RECEIVING_BRANCH_NUMBER is 'Recipient Branch Number';
COMMENT ON column BANK_DATA_RECORD.DEPOSIT_TYPE is 'Deposit Type';
COMMENT ON column BANK_DATA_RECORD.ACCOUNT_NUMBER is 'Account Number';
COMMENT ON column BANK_DATA_RECORD.RECIPIENT_NAME is 'Recipient Name';
COMMENT ON column BANK_DATA_RECORD.TRANSFER_AMOUNT is 'Transfer Amount';
COMMENT ON column BANK_DATA_RECORD.HEADER_RECORD_ID is 'Header ID';
CREATE SEQUENCE BANK_DATA_RECORD_DATA_RECORD_ID_SEQ increment by 1 start with 1;
```

For more information on the variables used, see [Default template storage directory](../../src/main/resources/jp/co/tis/gsp/tools/db/template).

The customized template can be loaded by adding <ddlTemplateFileDir> to the <configuration> tag of the gsp-dba-maven-plugin in pom.xml.
However, the file name must be as given below:

|Application | File name|
|:-:|:-:|
|CREATE TABLE|createTable.ftl|
|CREATE INDEX|createIndex.ftl|
|CREATE FOREIGN KEY|createForeignKey.ftl|
|CREATE VIEW|createView.ftl|

The configuration is as follows when the directory `ddlTemplates` is placed in the root directory of the project.

```xml
<plugin>
  <groupId>jp.co.tis.gsp</groupId>
  <artifactId>gsp-dba-maven-plugin</artifactId>
  <configuration>
    <!-- some configurations... -->
    <ddlTemplateFileDir>/ddlTemplates</ddlTemplateFileDir>
  </configuration>
</plugin>
```

### Modify the generate-entity template

As mentioned earlier, since the naming rules for the generated sequence names were changed, the generate-entity template also needs to be customized.
The customization of the generate-entity template for the above change in sequence name is as follows.


An example of copying [gsp_entity.ftl](../../src/main/resources/org/seasar/extension/jdbc/gen/internal/generator/tempaltes/java/gsp_entity.ftl) and creating a new template followed by making changes is shown below.
```diff
<#macro printAttrAnnotations tableName attr>
  <#if attr.id>
    @Id
    <#if attr.generationType??>
      <#if attr.generationType == "SEQUENCE">
-    @GeneratedValue(generator = "<#if schemaName??>${schemaName}.</#if>${attr.columnName}_SEQ", strategy = GenerationType.AUTO)
+    @GeneratedValue(generator = "<#if schemaName??>${schemaName}.</#if>${tableName}_${attr.columnName}_SEQ", strategy = GenerationType.AUTO)
-    @SequenceGenerator(name = "<#if schemaName??>${schemaName}.</#if>${attr.columnName}_SEQ", sequenceName = "<#if schemaName??>${schemaName}.</#if>${attr.columnName}_SEQ", initialValue = ${attr.initialValue}, allocationSize = ${attr.allocationSize})
+    @SequenceGenerator(name = "<#if schemaName??>${schemaName}.</#if>${tableName}_${attr.columnName}_SEQ", sequenceName = "<#if schemaName??>${schemaName}.</#if>${tableName}_${attr.columnName}_SEQ", initialValue = ${attr.initialValue}, allocationSize = ${attr.allocationSize})
```

For more information on how to use the modified generate-entity template, see [Example of Template Customization for Use with Generate-entity](./custom-EntityTemplate.md).
