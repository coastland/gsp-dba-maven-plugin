CREATE TABLE TEST_TBL3 (
  TEST_TBL3_ID BIGINT IDENTITY NOT NULL ,
  SEQTEST_AUTO BIGINT NULL ,
  SEQTEST_ID BIGINT NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'テストテーブル3', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TEST_TBL3;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_TBL3_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TEST_TBL3, @level2type = N'COLUMN',  @level2name = TEST_TBL3_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'SEQTEST_AUTO', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TEST_TBL3, @level2type = N'COLUMN',  @level2name = SEQTEST_AUTO;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'SEQTEST_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = TEST_TBL3, @level2type = N'COLUMN',  @level2name = SEQTEST_ID;
