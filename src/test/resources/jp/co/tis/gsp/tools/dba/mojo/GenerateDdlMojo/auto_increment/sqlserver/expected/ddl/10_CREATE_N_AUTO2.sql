CREATE TABLE N_AUTO2 (
  N_AUTO2_NID BIGINT NOT NULL ,
  TEST_NAME VARCHAR(30) NULL 
);
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'NOT自動採番２', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO2;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'N_AUTO2_NID', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO2, @level2type = N'COLUMN',  @level2name = N_AUTO2_NID;
EXEC sys.sp_addextendedproperty @name = N'Description', @value = N'TEST_NAME', @level0type = N'SCHEMA', @level0name = gsptest, @level1type = N'TABLE',  @level1name = N_AUTO2, @level2type = N'COLUMN',  @level2name = TEST_NAME;
