CREATE TABLE SCHEMA_TEST.INDEX_TEST2 (
  INDEX_TEST2_ID BIGINT IDENTITY NOT NULL ,
  SUB_ID_1 BIGINT NOT NULL ,
  SUB_ID_2 BIGINT NOT NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'INDEX_TEST2', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = INDEX_TEST2;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'INDEX_TEST2_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = INDEX_TEST2, @level2type = N'COLUMN',  @level2name = INDEX_TEST2_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'SUB_ID_1', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = INDEX_TEST2, @level2type = N'COLUMN',  @level2name = SUB_ID_1;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'SUB_ID_2', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = INDEX_TEST2, @level2type = N'COLUMN',  @level2name = SUB_ID_2;
