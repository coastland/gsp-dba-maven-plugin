CREATE TABLE ${entity.name} (
<#foreach column in entity.columnList>
  ${column.name} ${column.dataType}<#if column.length != 0>(${column.length})</#if><#if column.isArray()> ARRAY</#if><#if !column.isNullable()> NOT NULL </#if><#if column.defaultValue?has_content> DEFAULT ${column.defaultValue} </#if>,
</#foreach>
  PRIMARY KEY(<#foreach column in entity.columnList><#if column.isPrimaryKey()>${column.name}</#if></#foreach>)
);
