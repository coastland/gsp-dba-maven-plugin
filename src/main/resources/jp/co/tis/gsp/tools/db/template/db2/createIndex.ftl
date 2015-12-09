<#-- DB2 create index template -->
<#-- 外部参照を設定するためには主キーまたはユニークキーである必要があるため、制約を追加する。 -->
<#if index.isPrimaryKey()>
ALTER TABLE ${entity.name}
ADD CONSTRAINT ${index.name!} PRIMARY KEY
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
);
</#if>
<#if index.type=1>
<#-- DB2はunique制約へのconstraintはサポートしていない。 -->
ALTER TABLE ${entity.name} ADD UNIQUE
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
);
</#if>
CREATE <#if index.type!=3>UNIQUE </#if>INDEX ${index.name} ON ${entity.name}
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
);