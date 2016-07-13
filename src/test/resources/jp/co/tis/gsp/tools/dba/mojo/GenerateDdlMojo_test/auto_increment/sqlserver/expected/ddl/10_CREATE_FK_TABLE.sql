CREATE TABLE FK_TABLE (
  FK_TABLE_ID BIGINT IDENTITY NOT NULL ,
  TEST_NAME VARCHAR(30) NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'外部テーブル', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = FK_TABLE;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'FK_TABLE_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = FK_TABLE, @level2type = N'COLUMN',  @level2name = FK_TABLE_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_NAME', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = FK_TABLE, @level2type = N'COLUMN',  @level2name = TEST_NAME;
