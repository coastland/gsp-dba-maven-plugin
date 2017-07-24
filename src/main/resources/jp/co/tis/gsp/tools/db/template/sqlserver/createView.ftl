<#-- sql server create view template -->
<#-- SqLServerのCreate View構文にはREPLACE句は使用できない。 -->
CREATE VIEW <#if entity.schema?has_content>${entity.schema}.</#if>${view.name} AS
${view.sql}