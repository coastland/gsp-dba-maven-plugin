<#-- 引数で指定されたカラムが、関連モデルに含まれるかどうかを判定するファンクション -->
<#-- @JoinColumnと@JoinColumnsに指定したカラム名が存在するかどうかで判定する。 -->
<#function isForeignKey attr associationModelList>
  <#list associationModelList as asso>
    <#if asso.joinColumnModel?? && asso.joinColumnModel.name == attr.columnName>
      <#return true>
    </#if>
    <#if asso.joinColumnsModel??>
      <#list asso.joinColumnsModel.joinColumnModelList as joinColumn>
        <#if joinColumn.name == attr.columnName>
          <#return true>
        </#if>
      </#list>
    </#if>
  </#list>
  <#return false>
</#function>
<#macro printAttrAnnotations tableName attr>
  <#if attr.id>
    @Id
  </#if>
  <#if attr.generationType??>
    <#if attr.generationType == "SEQUENCE">
    @GeneratedValue(generator = "<#if schemaName??>${schemaName}.</#if>${attr.columnName}_SEQ", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "<#if schemaName??>${schemaName}.</#if>${attr.columnName}_SEQ", sequenceName = "<#if schemaName??>${schemaName}.</#if>${attr.columnName}_SEQ", initialValue = ${attr.initialValue}, allocationSize = ${attr.allocationSize})
    <#elseif attr.generationType == "IDENTITY">
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    </#if>
  </#if>
  <#if attr.lob>
    @Lob
  </#if>
  <#if attr.temporalType??>
    @Temporal(TemporalType.${attr.temporalType})
  </#if>
  <#if attr.version>
    @Version
  </#if>
    @Column(<#if attr.columnName??>name = "${attr.columnName}", </#if><#if attr.columnDefinition??>columnDefinition = "${attr.columnDefinition}", <#else><#if attr.length??>length = ${attr.length}, </#if><#if attr.precision??>precision = ${attr.precision}, </#if><#if attr.scale??>scale = ${attr.scale}, </#if></#if>nullable = ${attr.nullable?string}, unique = ${attr.unique?string}<#if isForeignKey(attr, associationModelList)>, insertable = false, updatable = false</#if>)
</#macro>
<#macro printAssoAnnotations asso>
    @${asso.associationType.annotation.simpleName}<#if asso.mappedBy??>(mappedBy = "${asso.mappedBy}")</#if>
  <#if asso.joinColumnModel??>
    @JoinColumn(name = "${asso.joinColumnModel.name}", referencedColumnName = "${asso.joinColumnModel.referencedColumnName}")
  <#elseif asso.joinColumnsModel??>
    @JoinColumns( {
    <#list asso.joinColumnsModel.joinColumnModelList as joinColumnModel>
        @JoinColumn(name = "${joinColumnModel.name}", referencedColumnName = "${joinColumnModel.referencedColumnName}")<#if joinColumnModel_has_next>,<#else> })</#if>
    </#list>
  </#if>
</#macro>
<#import "/lib.ftl" as lib>
<#if lib.copyright??>
${lib.copyright}
</#if>
<#if !lib.copyright??>
<#include "/copyright.ftl">
</#if>
<#if packageName??>
package ${packageName};
</#if>

<#list importNameSet as importName>
import ${importName};
</#list>
<#if staticImportNameSet?size gt 0>

  <#list staticImportNameSet as importName>
import static ${importName};
  </#list>
</#if>

/**
<#if useComment && comment??>
 * ${comment}
<#else>
 * ${shortClassName}エンティティクラス
</#if>
 *
<#if lib.author??>
 * @author ${lib.author}
</#if>
<#if lib.since??>
 * @since ${lib.since}
</#if>
 */
@Generated("GSP")
@Entity
<#if catalogName?? || schemaName?? || tableName?? || compositeUniqueConstraintModelList?size gt 0>
@Table(<#if catalogName??>catalog = "${catalogName}"</#if><#if schemaName??><#if catalogName??>, </#if>schema = "${schemaName}"</#if><#if tableName??><#if catalogName?? || schemaName??>, </#if>name = "${tableName}"</#if><#if compositeUniqueConstraintModelList?size gt 0><#if catalogName?? || schemaName?? || tableName??>, </#if>uniqueConstraints = { <#list compositeUniqueConstraintModelList as uniqueConstraint>@UniqueConstraint(columnNames = { <#list uniqueConstraint.columnNameList as columnName>"${columnName}"<#if columnName_has_next>, </#if></#list> })<#if uniqueConstraint_has_next>, </#if></#list> }</#if>)
</#if>
public class ${shortClassName}<#if shortSuperclassName??> extends ${shortSuperclassName}</#if> implements Serializable {

    private static final long serialVersionUID = 1L;
<#list attributeModelList as attr>

  <#if attr.unsupportedColumnType>
    /**
     * FIXME このプロパティに対応するカラムの型(${attr.columnTypeName})はサポート対象外です。
     */
  <#else>
    <#if useComment && attr.comment??>
    /** ${attr.comment} */
    <#else>
    /** ${attr.name}プロパティ */
    </#if>
  </#if>
  <#if !useAccessor>
  <@printAttrAnnotations tableName attr/>
  </#if>
  <#if attr.columnTypeName?ends_with("_ARRAY")>
    <#if useAccessor>private<#else>public</#if> java.util.List<${attr.attributeClass.simpleName}> ${attr.name};
  <#else>
    <#if useAccessor>private<#else>public</#if> ${attr.attributeClass.simpleName} ${attr.name};
  </#if>
</#list>
<#list associationModelList as asso>

    /** ${asso.name}関連プロパティ */
  <#if !useAccessor>
  <@printAssoAnnotations asso/>
  </#if>
    <#if useAccessor>private<#else>public</#if> ${asso.shortClassName} ${asso.name};
</#list>
<#if useAccessor>
  <#list attributeModelList as attr>
    <#if useComment && attr.comment??>
    /**
     * ${attr.comment}を返します。
     *
     * @return ${attr.comment}
     */
    <#else>
    /**
     * ${attr.name}を返します。
     *
     * @return ${attr.name}
     */
    </#if>
    <#if useAccessor>
    <@printAttrAnnotations tableName attr/>
    </#if>
    public ${attr.attributeClass.simpleName} <#if attr.attributeClass.getSimpleName()?matches("[bB]oolean")>is<#else>get</#if>${attr.name?cap_first}() {
        return ${attr.name};
    }

    <#if useComment && attr.comment??>
    /**
     * ${attr.comment}を設定します。
     *
     * @param ${attr.name} ${attr.comment}
     */
    <#else>
    /**
     * ${attr.name}を設定します。
     *
     * @param ${attr.name}
     */
    </#if>
    public void set${attr.name?cap_first}(${attr.attributeClass.simpleName} ${attr.name}) {
        this.${attr.name} = ${attr.name};
    }
  </#list>
  <#list associationModelList as asso>

    /**
     * ${asso.name}を返します。
     *
     * @return ${asso.name}
     */
     <#if useAccessor>
     <@printAssoAnnotations asso/>
     </#if>
    public ${asso.shortClassName} get${asso.name?cap_first}() {
        return ${asso.name};
    }

    /**
     * ${asso.name}を設定します。
     *
     * @param ${asso.name} ${asso.name}
     */
    public void set${asso.name?cap_first}(${asso.shortClassName} ${asso.name}) {
        this.${asso.name} = ${asso.name};
    }
  </#list>
</#if>
}
