<#-- DB2 create table template -->
<#-- 数値型の自動生成主キーの場合はシーケンスではなくIDENTITYを定義する 。-->
<#-- 主キー以外の自動生成カラムの場合はシーケンスを定義する（IDENTITYは1テーブルに1つしか定義できないため） -->
CREATE TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name} (
<#foreach column in entity.columnList>
  ${column.name} ${column.dataType}<#if column.length != 0>(${column.length}<#if column.scale != 0>,${column.scale}</#if>)</#if><#if column.isArray()> ARRAY</#if><#if !column.isNullable()> NOT NULL</#if><#if column.isAutoIncrement()> GENERATED ALWAYS AS IDENTITY</#if><#if column.defaultValue?has_content> DEFAULT ${column.defaultValue}</#if><#if column_has_next>,</#if>
</#foreach>
);
<#if entity.label?has_content>COMMENT ON table <#if entity.schema??>${entity.schema}</#if>${entity.name} is '${entity.label}';</#if>
<#foreach column in entity.columnList>
<#if column.label?has_content>COMMENT ON column <#if entity.schema??>${entity.schema}</#if>${entity.name}.${column.name} is '${column.label}';</#if>
</#foreach>