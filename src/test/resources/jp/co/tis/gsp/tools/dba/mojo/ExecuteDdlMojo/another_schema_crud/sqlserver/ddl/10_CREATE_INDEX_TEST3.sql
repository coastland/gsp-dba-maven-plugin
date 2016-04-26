CREATE TABLE gspanother.INDEX_TEST3 (
  INDEX_TEST3_ID BIGINT IDENTITY NOT NULL ,
  SUB_ID_1 BIGINT NOT NULL ,
  SUB_ID_2 BIGINT NOT NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'INDEX_TEST3', @level0type = N'SCHEMA', @level0name = gspanother, @level1type = N'TABLE',  @level1name = INDEX_TEST3;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'INDEX_TEST3_ID', @level0type = N'SCHEMA', @level0name = gspanother, @level1type = N'TABLE',  @level1name = INDEX_TEST3, @level2type = N'COLUMN',  @level2name = INDEX_TEST3_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'SUB_ID_1', @level0type = N'SCHEMA', @level0name = gspanother, @level1type = N'TABLE',  @level1name = INDEX_TEST3, @level2type = N'COLUMN',  @level2name = SUB_ID_1;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'SUB_ID_2', @level0type = N'SCHEMA', @level0name = gspanother, @level1type = N'TABLE',  @level1name = INDEX_TEST3, @level2type = N'COLUMN',  @level2name = SUB_ID_2;
