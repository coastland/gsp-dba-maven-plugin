<#-- DB2 create table template -->
CREATE TABLE <#if entity.schema?has_content>${entity.schema}.</#if>${entity.name} (
<#foreach column in entity.columnList>
  ${column.name} ${column.dataType}<#if column.length != 0>(${column.length}<#if column.scale != 0>,${column.scale}</#if>)</#if><#if column.isArray()> ARRAY</#if><#if !column.isNullable()> NOT NULL</#if><#if column.isAutoIncrement()> GENERATED ALWAYS AS IDENTITY</#if><#if column.defaultValue?has_content> DEFAULT ${column.defaultValue}</#if><#if column_has_next>,</#if>
</#foreach>
);
<#if entity.label?has_content>COMMENT ON table <#if entity.schema?has_content>${entity.schema}.</#if>${entity.name} is '${entity.label}';</#if>
<#foreach column in entity.columnList>
<#if column.label?has_content>COMMENT ON column <#if entity.schema?has_content>${entity.schema}.</#if>${entity.name}.${column.name} is '${column.label}';</#if>
</#foreach>