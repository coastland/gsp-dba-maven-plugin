CREATE OR REPLACE VIEW <#if view.schema??>${view.schema}</#if>${view.name}_TEST AS
${view.sql}
