<#-- IDENTITY型に対してALTER TABLEでPKを付与しようとするとエラーが発生するため、PKは一律CREATE TABLEで付与する -->
<#if !index.isPrimaryKey()>
<#if index.type=1>
ALTER TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name} ADD CONSTRAINT ${index.name!} UNIQUE
<#elseif index.type=2 || index.type=3>
CREATE <#if index.type=2>UNIQUE </#if>INDEX <#if entity.schema??>${entity.schema}</#if>${index.name} ON <#if entity.schema??>${entity.schema}</#if>${entity.name}
</#if>
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
);
</#if>
