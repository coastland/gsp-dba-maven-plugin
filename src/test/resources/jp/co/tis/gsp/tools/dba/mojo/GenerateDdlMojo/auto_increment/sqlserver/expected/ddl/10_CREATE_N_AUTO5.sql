CREATE TABLE N_AUTO5 (
  N_AUTO5_ID BIGINT NOT NULL ,
  SUB_ID BIGINT NOT NULL ,
  TEST_NAME VARCHAR(30) NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'NOT自動採番５', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO5;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'N_AUTO5_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO5, @level2type = N'COLUMN',  @level2name = N_AUTO5_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'SUB_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO5, @level2type = N'COLUMN',  @level2name = SUB_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_NAME', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO5, @level2type = N'COLUMN',  @level2name = TEST_NAME;
