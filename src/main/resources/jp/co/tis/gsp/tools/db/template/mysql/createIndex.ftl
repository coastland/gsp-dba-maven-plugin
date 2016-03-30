ALTER TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name} ADD
<#if index.isPrimaryKey()>
PRIMARY KEY ${index.name!}
<#else>
<#if index.type=1 || index.type=2>UNIQUE </#if>INDEX ${index.name}
</#if>
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
)
<#if index.isAutoIncrement() >,MODIFY ${index.firstColumn.name} ${index.firstColumn.dataType}<#if index.firstColumn.length != 0>(${index.firstColumn.length})</#if><#if index.firstColumn.defaultValue?has_content> DEFAULT ${index.firstColumn.defaultValue} </#if><#if !index.firstColumn.isNullable()> NOT NULL </#if><#if index.firstColumn.label?has_content> COMMENT '${index.firstColumn.label}'</#if> AUTO_INCREMENT</#if>
