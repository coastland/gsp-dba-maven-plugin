<#-- SQL Server create table template -->
<#-- NULLを許容する場合は"NULL"句を、NULLを許容しない場合は"NOT NULL"句を使用する。 -->
<#-- IDENTITYを指定するのはsmallint、int、bigint、decimal、numeric型で自動生成されるもののみ。 -->
<#-- IDENTITYが指定できるのは1テーブルに1つであるため、シングル主キーでない自動インクリメントのカラムにはIDENTITYは設定しない。 -->
CREATE TABLE <#if entity.schema?has_content>${entity.schema}.</#if>${entity.name} (
<#foreach column in entity.columnList>
  ${column.name} ${column.dataType}<#if column.length != 0>(${column.length}<#if column.scale != 0 && (column.dataType = 'DECIMAL' || column.dataType = 'NUMERIC')>,${column.scale}</#if>)</#if><#if column.isArray()> ARRAY</#if><#if column.isAutoIncrement()> IDENTITY</#if><#if !column.isNullable()> NOT</#if> NULL <#if column.defaultValue?has_content> DEFAULT ${column.defaultValue} </#if><#if column_has_next>,</#if>
</#foreach>
);
<#-- コメントの設定には拡張プロパティを利用する。 -->
<#if entity.label?has_content>EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'${entity.label}', @level0type = N'SCHEMA', @level0name = ${erd.schema}, @level1type = N'TABLE',  @level1name = ${entity.name};</#if>
<#foreach column in entity.columnList>
<#if column.label?has_content>EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'${column.label}', @level0type = N'SCHEMA', @level0name = ${erd.schema}, @level1type = N'TABLE',  @level1name = ${entity.name}, @level2type = N'COLUMN',  @level2name = ${column.name};</#if>
</#foreach>
<#-- SQL Server 2008ではシーケンスはサポートされていない。 -->