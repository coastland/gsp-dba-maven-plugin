CREATE TABLE N_AUTO6 (
  FK_TABLE_ID BIGINT NOT NULL ,
  TEST_NAME VARCHAR(30) NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'NOT自動採番６', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO6;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'FK_TABLE_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO6, @level2type = N'COLUMN',  @level2name = FK_TABLE_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_NAME', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO6, @level2type = N'COLUMN',  @level2name = TEST_NAME;
