CREATE INDEX ${index.name} ON ${entity.name}
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
)
