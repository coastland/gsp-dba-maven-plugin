## generate-ddlで使用するテンプレートのカスタマイズ例

generate-ddlゴールで使用するテンプレートのカスタマイズ方法を記述します。

gsp-dba-maven-pluginでは、DDL生成時のテンプレートエンジンとしてFreeMakerを使用しています。
ですので、カスタマイズする際はFreeMakerのルールに従いテンプレートを作成してください。<br />
**ただし、独自のテンプレートを使用する際は必ず、`generate-entity`のテンプレートへの影響確認を行ってください。**

例としてシーケンスの命名を「テーブル名_カラム名_SEQ」とする場合を以下に記載しています。
本例の場合はエンティティクラスの`@SequenceGenerator`に変更が必要なので、`generate-entity`ゴールのテンプレートも修正が必要です。
修正方法は[generate-entityで使用するテンプレートのカスタマイズ例](./custom-EntityTemplate.md)を参照してください。

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

このテンプレートの場合、次のようなDDLが出力されます。

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
COMMENT ON table BANK_DATA_RECORD is '銀行データレコード';
COMMENT ON column BANK_DATA_RECORD.DATA_RECORD_ID is 'データID';
COMMENT ON column BANK_DATA_RECORD.RECEIVING_BANK_NUMBER is '被仕向金融機関番号';
COMMENT ON column BANK_DATA_RECORD.RECEIVING_BRANCH_NUMBER is '被仕向支店番号';
COMMENT ON column BANK_DATA_RECORD.DEPOSIT_TYPE is '預金種目';
COMMENT ON column BANK_DATA_RECORD.ACCOUNT_NUMBER is '口座番号';
COMMENT ON column BANK_DATA_RECORD.RECIPIENT_NAME is '受取人名';
COMMENT ON column BANK_DATA_RECORD.TRANSFER_AMOUNT is '振込金額';
COMMENT ON column BANK_DATA_RECORD.HEADER_RECORD_ID is 'ヘッダID';
CREATE SEQUENCE BANK_DATA_RECORD_DATA_RECORD_ID_SEQ increment by 1 start with 1;
```

使用できる変数は[デフォルトのテンプレート格納ディレクトリ](../src/main/resources/jp/co/tis/gsp/tools/db/template)を参考にしてください。

カスタマイズしたテンプレートはpom.xmlのgsp-dba-maven-pluginの`<configuration>`タグに`<ddlTemplateFileDir>`を追加することで読み込まれるようになります。
ただし、ファイル名は以下である必要があります。

|用途|ファイル名|
|:-:|:-:|
|CREATE TABLE|createTable.ftl|
|CREATE INDEX|createIndex.ftl|
|CREATE FOREIGN KEY|createForeignKey.ftl|
|CREATE VIEW|createView.ftl|

プロジェクトのルートディレクトリに`ddlTemplates`というディレクトリを用意した時の設定は次のようになります。

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

