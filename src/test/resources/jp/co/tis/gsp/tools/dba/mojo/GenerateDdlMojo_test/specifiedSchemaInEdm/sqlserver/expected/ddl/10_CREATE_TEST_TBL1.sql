CREATE TABLE SCHEMA_TEST.TEST_TBL1 (
  TEST_TBL1_ID BIGINT IDENTITY NOT NULL ,
  TEST_NAME VARCHAR(30) NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'テストテーブル1', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TEST_TBL1;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_TBL1_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TEST_TBL1, @level2type = N'COLUMN',  @level2name = TEST_TBL1_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_NAME', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TEST_TBL1, @level2type = N'COLUMN',  @level2name = TEST_NAME;
