<#-- IDENTITY型に対してALTER TABLEでPKを付与しようとするとエラーが発生するため、PKは一律CREATE TABLEで付与する -->
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