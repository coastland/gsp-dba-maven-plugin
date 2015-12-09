<#-- sql server create view template -->
<#-- SqLServerのCreate View構文にはREPLACE句は使用できない。 -->
CREATE VIEW ${view.name} AS
${view.sql}