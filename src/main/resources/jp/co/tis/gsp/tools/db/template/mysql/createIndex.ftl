<#if index.isPrimaryKey()>
ALTER TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name} ADD
PRIMARY KEY ${index.name!}
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
)
<#foreach column in index.columnList>
  <#if column.isAutoIncrement() >,MODIFY ${column.name} ${column.dataType}<#if column.length != 0>(${column.length})</#if><#if column.defaultValue?has_content> DEFAULT ${column.defaultValue} </#if><#if !column.isNullable()> NOT NULL </#if><#if column.label?has_content> COMMENT '${column.label}'</#if> AUTO_INCREMENT</#if>
</#foreach>
;
</#if>

<#if index.type=1>
ALTER TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name} ADD UNIQUE
<#elseif index.type=2 || index.type=3>
CREATE <#if index.type=2>UNIQUE </#if>INDEX <#if entity.schema??>${entity.schema}</#if>${index.name} ON <#if entity.schema??>${entity.schema}</#if>${entity.name}
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
);
</#if>