<#-- DB2 create index template -->
<#-- 外部参照を設定するためには主キーまたはユニークキーである必要があるため、制約を追加する。 -->
<#if index.isPrimaryKey()>
ALTER TABLE <#if entity.schema?has_content>${entity.schema}.</#if>${entity.name}
ADD CONSTRAINT ${index.name!} PRIMARY KEY
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
);
<#else>
<#if index.type==1>
ALTER TABLE <#if entity.schema?has_content>${entity.schema}.</#if>${entity.name} ADD UNIQUE
<#elseif index.type=2 || index.type=3>
CREATE <#if index.type==2>UNIQUE </#if>INDEX <#if entity.schema?has_content>${entity.schema}.</#if>${index.name} ON <#if entity.schema?has_content>${entity.schema}.</#if>${entity.name}
</#if>
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
);
</#if>