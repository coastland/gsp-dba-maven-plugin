<#if index.isPrimaryKey()>
ALTER TABLE <#if entity.schema??>${entity.schema}.</#if>${entity.name}
ADD CONSTRAINT ${index.name!} PRIMARY KEY
<#else>
CREATE <#if index.type=1 || index.type=2>UNIQUE </#if>INDEX <#if entity.schema??>${entity.schema}.</#if>${index.name} ON <#if entity.schema??>${entity.schema}.</#if>${entity.name}
</#if>
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
);
