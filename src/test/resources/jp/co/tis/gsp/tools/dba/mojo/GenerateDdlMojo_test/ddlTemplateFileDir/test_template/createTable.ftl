CREATE TABLE ${entity.name}_TEST (
<#foreach column in entity.columnList>
  ${column.name} ${column.dataType}<#if column.length != 0>(${column.length}<#if column.scale != 0>,${column.scale}</#if>)</#if><#if column.isArray()> ARRAY</#if><#if !column.isNullable()> NOT NULL </#if><#if column.defaultValue?has_content> DEFAULT ${column.defaultValue} </#if><#if column_has_next>,</#if>
</#foreach>
);
<#if entity.label?has_content>COMMENT ON table ${entity.name} is '${entity.label}';</#if>
<#foreach column in entity.columnList>
<#if column.label?has_content>COMMENT ON column ${entity.name}.${column.name} is '${column.label}';</#if>
</#foreach>
<#foreach column in entity.columnList>
<#if column.isAutoIncrement()>CREATE SEQUENCE ${column.generatorKeyName} increment by 1 start with 1;
</#if>
</#foreach>