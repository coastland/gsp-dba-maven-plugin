CREATE TABLE TEST_TBL2 (
  TEST_TBL2_ID BIGINT NOT NULL ,
  TEST_TBL1_ID BIGINT NOT NULL ,
  TEST_NAME VARCHAR(20) NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'テストテーブル2', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TEST_TBL2;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_TBL2_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TEST_TBL2, @level2type = N'COLUMN',  @level2name = TEST_TBL2_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_TBL1_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TEST_TBL2, @level2type = N'COLUMN',  @level2name = TEST_TBL1_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_NAME', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TEST_TBL2, @level2type = N'COLUMN',  @level2name = TEST_NAME;
