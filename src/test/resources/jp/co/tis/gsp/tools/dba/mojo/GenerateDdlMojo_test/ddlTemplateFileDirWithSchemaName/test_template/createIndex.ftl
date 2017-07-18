CREATE INDEX ${index.name} ON <#if entity.schema?has_content>${entity.schema}.</#if>${entity.name}_TEST
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
)
