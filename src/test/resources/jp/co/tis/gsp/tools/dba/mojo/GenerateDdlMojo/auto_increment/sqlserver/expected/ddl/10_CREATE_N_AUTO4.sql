CREATE TABLE N_AUTO4 (
  N_AUTO4_ID BIGINT NOT NULL ,
  TEST_NAME VARCHAR(30) NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'NOT自動採番４', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO4;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'N_AUTO4_ID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO4, @level2type = N'COLUMN',  @level2name = N_AUTO4_ID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_NAME', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO4, @level2type = N'COLUMN',  @level2name = TEST_NAME;
