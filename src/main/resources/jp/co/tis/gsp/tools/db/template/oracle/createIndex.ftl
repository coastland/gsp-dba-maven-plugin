<#if index.isPrimaryKey()>
ALTER TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name}
ADD CONSTRAINT ${index.name!} PRIMARY KEY
<#elseif index.type=1>
ALTER TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name} ADD CONSTRAINT ${index.name} UNIQUE
<#elseif index.type=2 || index.type=3>
CREATE <#if index.type=2>UNIQUE </#if>INDEX <#if entity.schema??>${entity.schema}</#if>${index.name} ON <#if entity.schema??>${entity.schema}</#if>${entity.name}
</#if>
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
);
