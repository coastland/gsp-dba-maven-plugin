<#macro printAttrAnnotations tableName attr>
  <#if attr.id>
    @Id
    <#if attr.generationType??>
      <#if attr.generationType == "SEQUENCE">
    @GeneratedValue(generator = "<#if schemaName??>${schemaName}.</#if>${attr.columnName}_SEQ", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "<#if schemaName??>${schemaName}.</#if>${attr.columnName}_SEQ", sequenceName = "<#if schemaName??>${schemaName}.</#if>${attr.columnName}_SEQ", initialValue = ${attr.initialValue}, allocationSize = ${attr.allocationSize})
      <#else>
    @GeneratedValue(strategy = GenerationType.IDENTITY)
      </#if>
    </#if>
  </#if>
  <#if attr.version>
    @Version
  </#if>
    @Column(<#if attr.columnName??>name = "${attr.columnName}"</#if>)
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
@Table(<#if schemaName??>schema = "${schemaName}"</#if><#if tableName??><#if schemaName??>, </#if>name = "${tableName}"</#if>)
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
    <@printAttrAnnotations tableName attr/>
    <#if useAccessor>private<#else>public</#if> ${attr.attributeClass.simpleName} ${attr.name};
</#list>
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
}
