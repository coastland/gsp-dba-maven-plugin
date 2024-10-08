## generate-entityで使用するテンプレートのカスタマイズ例

generate-entityで使用するテンプレートのカスタマイズ方法を記述します。

このgsp-dba-maven-pluginでは、エンティティ生成時のテンプレートエンジンとしてfreemakerを使用しています。ですので、カスタマイズする際はfreemakerのルールに従いテンプレートを作成してください。  
以下、簡単な作成例です。

```
<#-- エンティティのパッケージ名 -->
<#if packageName??>
package ${packageName};
</#if>

<#-- 必要なクラスのインポート -->
<#list importNameSet as importName>
import ${importName};
</#list>

@Generated("GSP_CUSTOM")
@Entity
public class ${shortClassName}<#if shortSuperclassName??> extends ${shortSuperclassName}</#if> implements Serializable {

<#-- カラムに対応したプロパティ -->
<#list attributeModelList as attr>
    /** ${attr.name}プロパティ */
  <#if !useAccessor>
    <#if attr.id>
    @Id
    </#if>
    <#if attr.version>
    @Version
    </#if>
    @Column(name = "${attr.columnName}")
  </#if>
    <#if useAccessor>private<#else>public</#if> ${attr.attributeClass.simpleName} ${attr.name};
</#list>

<#-- 各プロパティのアクセサ -->
<#if useAccessor>
  <#list attributeModelList as attr>
    /**
     * ${attr.name}を返します。
     *
     * @return ${attr.name}
     */
    <#if attr.id>
    @Id
    </#if>
    <#if attr.version>
    @Version
    </#if>
    @Column(name = "${attr.columnName}")
    public ${attr.attributeClass.simpleName} <#if attr.attributeClass.getSimpleName()?matches("[bB]oolean")>is<#else>get</#if>${attr.name?cap_first}() {
        return ${attr.name};
    }
    /**
     * ${attr.name}を設定します。
     *
     * @param ${attr.name}
     */
    public void set${attr.name?cap_first}(${attr.attributeClass.simpleName} ${attr.name}) {
        this.${attr.name} = ${attr.name};
    }
    
  </#list>
</#if>
}

```

このテンプレートの場合、次のようなエンティティが出力されます。

```java
package com.example.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serializable;

@Generated("GSP_CUSTOM")
@Entity
public class SystemAccount implements Serializable {

    /** userIdプロパティ */
    private String userId;
    /** userIdLockedプロパティ */
    private String userIdLocked;
    /** effectiveDateFromプロパティ */
    private String effectiveDateFrom;
    /** effectiveDateToプロパティ */
    private String effectiveDateTo;

    /**
     * userIdを返します。
     *
     * @return userId
     */
    @Id
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }
    /**
     * userIdを設定します。
     *
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    // ～以下省略～
```

使用できる変数は[デフォルトのテンプレート](../src/main/resources/org/seasar/extension/jdbc/gen/internal/generator/tempaltes/java/gsp_entity.ftl)を参考にしてください。

カスタマイズしたら、適用するプロジェクトのpomのgsp-dba-maven-pluginに、作成したテンプレートを読み込ませるよう設定を追加してください。
以下、src/main/resource/gsp/template/gsp_entity_custom.ftlに配置した場合の設定例です。

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
        使用するgsp-dba-maven-pluginのバージョン
    </version>
    <executions>
      <execution>
        <id>generate-entity</id>
        <phase>generate-sources</phase>
        <goals>
          <goal>generate-entity</goal>
        </goals>
        <configuration>
          <templateFilePrimaryDir>
            src/main/resource/gsp/template
          </templateFilePrimaryDir>
          <entityTemplate>
            gsp_entity_custom.ftl
          </entityTemplate>
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```

