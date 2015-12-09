<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE components PUBLIC "-//SEASAR//DTD S2Container 2.4//EN"
    "http://www.seasar.org/dtd/components24.dtd">
<components>
    <include path="jdbc.dicon"/>
    <include path="s2jdbc-internal.dicon"/>

<#if databaseProduct == "Solr">
	<component name="propertyMetaFactory" class="net.unit8.solr.jdbc.extension.s2jdbc.meta.SolrPropertyMetaFactoryImpl"/>
	<component name="entityMetaFactory" class="org.seasar.extension.jdbc.meta.EntityMetaFactoryImpl"/>
</#if>

    <component name="jdbcManager"
      class="org.seasar.extension.jdbc.manager.JdbcManagerImpl">
        <property name="maxRows">0</property>
        <property name="fetchSize">0</property>
        <property name="queryTimeout">0</property>
		<property name="dialect">
			<component class="${dialectClass}" />
		</property>
    </component>
</components>