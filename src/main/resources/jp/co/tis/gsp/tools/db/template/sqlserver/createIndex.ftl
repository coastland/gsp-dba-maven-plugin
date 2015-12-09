<#-- SQL Server create index template -->
<#-- SQL Serverは主キーとユニークキーには自動でインデックが作成される。 -->
<#if index.isPrimaryKey()>
ALTER TABLE ${entity.name}
ADD CONSTRAINT ${index.name!} PRIMARY KEY
<#elseif index.type=1>
ALTER TABLE ${entity.name}
ADD CONSTRAINT ${index.name!} UNIQUE
<#else>
CREATE <#if index.type=2>UNIQUE </#if>INDEX ${index.name} ON ${entity.name}
</#if>
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
);
