CREATE TABLE AUTO1 (
  AUTO1_ID BIGINT IDENTITY NOT NULL ,
  TEST_NAME VARCHAR(30) NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'自動採番１', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = AUTO1;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'AUTO1_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = AUTO1, @level2type = N'COLUMN',  @level2name = AUTO1_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_NAME', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = AUTO1, @level2type = N'COLUMN',  @level2name = TEST_NAME;
