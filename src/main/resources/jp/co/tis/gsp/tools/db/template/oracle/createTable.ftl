CREATE TABLE <#if entity.schema??>${entity.schema}.</#if>${entity.name} (
<#foreach column in entity.columnList>
  ${column.name} ${column.dataType}<#if column.length != 0>(${column.length}<#if column.scale != 0>,${column.scale}</#if><#if lengthSemantics==LengthSemantics.CHAR && (column.dataType == "VARCHAR2" || column.dataType == "CHAR")> CHAR</#if>)</#if><#if column.isArray()> ARRAY</#if><#if column.defaultValue?has_content> DEFAULT ${column.defaultValue} </#if><#if !column.isNullable()> NOT NULL </#if><#if column_has_next>,</#if>
</#foreach>
);
<#if entity.label?has_content>COMMENT ON table <#if entity.schema??>${entity.schema}.</#if>${entity.name} is '${entity.label}';</#if>
<#foreach column in entity.columnList>
<#if column.label?has_content>COMMENT ON column <#if entity.schema??>${entity.schema}.</#if>${entity.name}.${column.name} is '${column.label}';</#if>
</#foreach>
<#foreach column in entity.columnList>
<#if column.isAutoIncrement()>CREATE SEQUENCE <#if entity.schema??>${entity.schema}.</#if>${column.generatorKeyName} increment by 1 start with 1;
  <#if column.generatorKeyName?ends_with("_USEQ")>
CREATE SYNONYM <#if entity.schema??>${entity.schema}.</#if>${column.generatorKeyName?replace("_USEQ", "_SEQ")} FOR ${column.generatorKeyName};
  </#if>
</#if>
</#foreach>