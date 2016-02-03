<#-- postgresql create table template -->
<#-- Postgresqlは複数主キーをalter tableで指定できないため、こちらで設定する。 -->
<#-- また、SERIAL型・BIGSERIAL型は自動生成型であるため、これらの型の場合はシーケンスを生成しない。 -->
CREATE TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name} (
<#foreach column in entity.columnList>
  ${column.name} ${column.dataType}<#if column.length != 0>(${column.length}<#if column.scale!=0>,${column.scale}</#if>)</#if><#if column.isArray()> ARRAY</#if><#if !column.isNullable()> NOT NULL</#if><#if column.defaultValue?has_content> DEFAULT ${column.defaultValue}</#if><#if column_has_next>,<#else><#if entity.havePrimaryKey()>,</#if></#if>
</#foreach>
<#if entity.havePrimaryKey()>
<#assign isFirst = "true" />
  PRIMARY KEY (<#foreach column in entity.columnList><#if column.isPrimaryKey()><#if isFirst!="true">, </#if>${column.name}<#assign isFirst = "false" /></#if></#foreach>)
</#if>
);
<#if entity.label?has_content>
COMMENT ON table <#if entity.schema??>${entity.schema}</#if>${entity.name} is '${entity.label}';
</#if>
<#foreach column in entity.columnList>
<#if column.label?has_content>
COMMENT ON column <#if entity.schema??>${entity.schema}</#if>${entity.name}.${column.name} is '${column.label}';
</#if>
</#foreach>
<#foreach column in entity.columnList>
<#if column.isAutoIncrement() && column.dataType!='SERIAL' && column.dataType!='BIGSERIAL'>
CREATE SEQUENCE <#if entity.schema??>${entity.schema}</#if>${column.generatorKeyName} increment by 1 start with 1;
</#if>
</#foreach>