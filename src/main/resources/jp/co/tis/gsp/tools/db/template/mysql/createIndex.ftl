<#if index.isPrimaryKey()>
ALTER TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name} ADD
PRIMARY KEY ${index.name!}
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
)
<#if index.isAutoIncrement() >,MODIFY ${index.firstColumn.name} ${index.firstColumn.dataType}<#if index.firstColumn.length != 0>(${index.firstColumn.length})</#if><#if index.firstColumn.defaultValue?has_content> DEFAULT ${index.firstColumn.defaultValue} </#if><#if !index.firstColumn.isNullable()> NOT NULL </#if><#if index.firstColumn.label?has_content> COMMENT '${index.firstColumn.label}'</#if> AUTO_INCREMENT</#if>
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