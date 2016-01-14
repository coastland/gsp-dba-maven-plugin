CREATE OR REPLACE VIEW <#if view.schema??>${view.schema}.</#if>${view.name} AS
${view.sql}
