declare @stmt nvarchar(max)
 
-- procedures
select  @stmt = isnull( @stmt + '', '' ) + 'drop procedure ' + quotename(schema_name(schema_id)) + '.' + quotename(name) + ";" from sys.procedures where schema_id = SCHEMA_ID('$(SCHEMA)')
 
-- check constraints
select  @stmt = isnull( @stmt + '', '' ) + 'alter table ' + quotename(schema_name(schema_id)) + '.' + quotename(object_name( parent_object_id )) + ' drop constraint ' + quotename(name) + ";" from sys.check_constraints where schema_id = SCHEMA_ID('$(SCHEMA)')
 
-- functions
select  @stmt = isnull( @stmt + '', '' ) + 'drop function ' + quotename(schema_name(schema_id)) + '.' + quotename(name) + ";" from sys.objects where type in ( 'FN', 'IF', 'TF' ) and schema_id = SCHEMA_ID('$(SCHEMA)')
 
-- views
select  @stmt = isnull( @stmt + '', '' ) + 'drop view ' + quotename(schema_name(schema_id)) + '.' + quotename(name) + ";" from sys.views where schema_id = SCHEMA_ID('$(SCHEMA)')
 
-- foreign keys
select  @stmt = isnull( @stmt + '', '' ) + 'alter table ' + quotename(schema_name(schema_id)) + '.' + quotename(object_name( parent_object_id )) + ' drop constraint ' + quotename(name) + ";" from sys.foreign_keys where schema_id = SCHEMA_ID('$(SCHEMA)')
 
-- tables
select  @stmt = isnull( @stmt + '', '' ) + 'drop table ' + quotename(schema_name(schema_id)) + '.' + quotename(name) + ";" from sys.tables where schema_id = SCHEMA_ID('$(SCHEMA)')
 
-- user defined typesselect  @stmt = isnull( @stmt + '', '' ) + 'drop type ' + quotename(schema_name(schema_id)) + '.' + quotename(name) + ";" from sys.types where is_user_defined = 1 and  schema_id = SCHEMA_ID('$(SCHEMA)')
 
-- schemas
select @stmt = isnull( @stmt + '', '' ) + 'drop schema ' + quotename(name) + ";" from sys.schemas where schema_id = SCHEMA_ID('$(SCHEMA)')
 
PRINT N'Drop Schema Execute: ' + @stmt

exec sp_executesql @stmt