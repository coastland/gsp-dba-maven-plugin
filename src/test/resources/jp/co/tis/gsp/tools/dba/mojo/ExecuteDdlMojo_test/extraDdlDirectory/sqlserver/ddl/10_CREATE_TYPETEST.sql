CREATE TABLE TYPETEST (
  TYPE1 BIGINT NULL ,
  TYPE2 BINARY(1000) NULL ,
  TYPE3 BIT NULL ,
  TYPE4 CHAR(10) NULL ,
  TYPE5 DATE NULL ,
  TYPE6 DATETIME NULL ,
  TYPE7 DATETIME2 NULL ,
  TYPE8 DATETIMEOFFSET NULL ,
  TYPE9 DECIMAL(10,2) NULL ,
  TYPE10 FLOAT(10) NULL ,
  TYPE11 HIERARCHYID NULL ,
  TYPE12 IMAGE NULL ,
  TYPE13 INT NULL ,
  TYPE14 MONEY NULL ,
  TYPE15 NCHAR(10) NULL ,
  TYPE16 NTEXT NULL ,
  TYPE17 NUMERIC(20,2) NULL ,
  TYPE18 NVARCHAR(20) NULL ,
  TYPE19 NVARCHAR(MAX) NULL ,
  TYPE20 REAL NULL ,
  TYPE21 SMALLDATETIME NULL ,
  TYPE22 SMALLINT NULL ,
  TYPE23 SMALLMONEY NULL ,
  TYPE24 TEXT NULL ,
  TYPE25 TIME NULL ,
  TYPE26 TINYINT NULL ,
  TYPE27 UNIQUEIDENTIFIER NULL ,
  TYPE28 VARBINARY(1000) NULL ,
  TYPE29 VARBINARY(MAX) NULL ,
  TYPE30 VARCHAR(100) NULL ,
  TYPE31 VARCHAR(MAX) NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'タイプテスト', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE1', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE1;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE2', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE2;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE3', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE3;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE4', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE4;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE5', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE5;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE6', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE6;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE7', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE7;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE8', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE8;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE9', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE9;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE10', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE10;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE11', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE11;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE12', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE12;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE13', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE13;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE14', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE14;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE15', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE15;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE16', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE16;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE17', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE17;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE18', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE18;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE19', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE19;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE20', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE20;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE21', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE21;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE22', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE22;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE23', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE23;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE24', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE24;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE25', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE25;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE26', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE26;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE27', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE27;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE28', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE28;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE29', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE29;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE30', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE30;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TYPE31', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TYPETEST, @level2type = N'COLUMN',  @level2name = TYPE31;
