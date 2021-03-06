CREATE TABLE TEST_TBL3_TEST (
  TEST_TBL3_ID IDENTITY NOT NULL ,
  SEQTEST_AUTO BIGINT,
  SEQTEST_ID BIGINT
);
COMMENT ON table TEST_TBL3 is 'テストテーブル3';
COMMENT ON column TEST_TBL3.TEST_TBL3_ID is 'TEST_TBL3_ID';
COMMENT ON column TEST_TBL3.SEQTEST_AUTO is 'SEQTEST_AUTO';
COMMENT ON column TEST_TBL3.SEQTEST_ID is 'SEQTEST_ID';
CREATE SEQUENCE TEST_TBL3_ID_SEQ increment by 1 start with 1;
