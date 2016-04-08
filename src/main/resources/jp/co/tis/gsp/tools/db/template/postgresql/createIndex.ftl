<#-- postgresqlではalter table句で複合主キー制約を設定できないため、主キーの定義はcreateTableで行う。 -->
<#-- postgresqlではindexは常に指定テーブルと同じスキーマに作成されるためスキーマ修飾はなし -->
<#if !index.isPrimaryKey()>
<#if index.type=1>
ALTER TABLE <#if entity.schema??>${entity.schema}</#if>${entity.name} ADD CONSTRAINT ${index.name!} UNIQUE
<#elseif index.type=2 || index.type=3>
CREATE <#if index.type=2>UNIQUE </#if>INDEX ${index.name} ON <#if entity.schema??>${entity.schema}</#if>${entity.name}
</#if>
(
<#foreach column in index.columnList>
  ${column.name}<#if column_has_next>,</#if>
</#foreach>
);
</#if>
