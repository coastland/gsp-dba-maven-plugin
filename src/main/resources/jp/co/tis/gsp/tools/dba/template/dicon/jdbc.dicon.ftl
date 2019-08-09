<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE components PUBLIC "-//SEASAR2.1//DTD S2Container//EN"
	"http://www.seasar.org/dtd/components21.dtd">
<components namespace="jdbc">
	<include path="jta.dicon" />
	<component name="dataSource" class="jp.co.tis.gsp.tools.db.DataSourceImpl" >
		<arg>"${url}"</arg>
		<arg>"${user}"</arg>
		<arg>"${password}"</arg>
	</component>
</components>
