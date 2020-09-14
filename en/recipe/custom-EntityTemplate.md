## Example of Template Customization for Use with Generate-entity

This section describes how to customize the template used by generate-entity.

The gsp-dba-maven-plugin uses FreeMaker as the template engine when creating entities. Therefore, create the template by following the FreeMaker rules during customization.
A simple example is shown below.

```
<#-- Package name of entity -->
<#if packageName??>
package ${packageName};
</#if>

<#-- Import the required classes -->
<#list importNameSet as importName>
import ${importName};
</#list>

@Generated("GSP_CUSTOM")
@Entity
public class ${shortClassName}<#if shortSuperclassName??> extends ${shortSuperclassName}</#if> implements Serializable {

<#-- Property corresponding to the column -->
<#list attributeModelList as attr>
    /** ${attr.name} property */
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

<#-- Access to each property -->
<#if useAccessor>
  <#list attributeModelList as attr>
    /**
     * Returns ${attr.name}.
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
     * Configure ${attr.name}.
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

The following entities are output for this template:

```java
package com.example.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Generated("GSP_CUSTOM")
@Entity
public class SystemAccount implements Serializable {

    /** userId property */
    private String userId;
    /** userIdLocked property */
    private String userIdLocked;
    /** effectiveDateFrom property */
    private String effectiveDateFrom;
    /** effectiveDateTo property */
    private String effectiveDateTo;

    /**
     * Returns the userId.
     *
     * @return userId
     */
    @Id
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }
    /**
     * Configure the userID.
     *
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    // ~ Rest is omitted ~
```

Refer to [default template](../../src/main/resources/org/seasar/extension/jdbc/gen/internal/generator/tempaltes/java/gsp_entity.ftl) for available variables.

Customize and add a configuration to the gsp-dba-maven-plugin of pom used in the project to import the template created.
The following is an example of a configuration for placement in src/main/resource/gsp/template/gsp_entity_custom.ftl.

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
        Version of gsp-dba-maven-plugin used
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

