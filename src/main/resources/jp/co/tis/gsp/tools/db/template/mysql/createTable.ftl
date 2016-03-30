CREATE TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name} (
<#foreach column in entity.columnList>
  ${column.name} ${column.dataType}<#if column.length != 0>(${column.length}<#if column.scale != 0>,${column.scale}</#if>)</#if><#if column.isNullable()> NULL DEFAULT NULL </#if><#if !column.isNullable()> NOT NULL </#if><#if column.defaultValue?has_content> DEFAULT ${column.defaultValue} </#if><#if column.label?has_content> COMMENT '${column.label}'</#if><#if column_has_next>,</#if>
</#foreach>
)
<#if entity.label?has_content>COMMENT='${entity.label}'</#if>