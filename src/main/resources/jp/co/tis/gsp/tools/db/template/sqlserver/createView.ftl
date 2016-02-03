<#-- sql server create view template -->
<#-- SqLServerのCreate View構文にはREPLACE句は使用できない。 -->
CREATE VIEW <#if entity.schema??>${entity.schema}</#if>${view.name} AS
${view.sql}