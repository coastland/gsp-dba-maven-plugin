<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE components PUBLIC "-//SEASAR2.1//DTD S2Container//EN"
	"http://www.seasar.org/dtd/components21.dtd">
<components namespace="jdbc">
	<include path="jta.dicon" />
	<component name="xaDataSource" class="org.seasar.extension.dbcp.impl.XADataSourceImpl">
		<property name="driverClassName">"${driver}"</property>
		<property name="URL">"${url}"</property>
		<property name="user">"${user}"</property>
		<property name="password">"${password}"</property>
	</component>
	<component name="connectionPool" class="org.seasar.extension.dbcp.impl.ConnectionPoolImpl">
		<property name="timeout">600</property>
		<property name="maxPoolSize">5</property>
		<property name="allowLocalTx">true</property>
		<destroyMethod name="close" />
	</component>
	<component name="dataSource" class="org.seasar.extension.dbcp.impl.DataSourceImpl" />
</components>
