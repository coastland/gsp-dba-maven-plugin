CREATE TABLE gspanother.TEST_TBL1 (
  TEST_TBL1_ID BIGINT NOT NULL,
 TEST_NAME VARCHAR(30),
  PRIMARY KEY (TEST_TBL1_ID)
);
COMMENT ON table gspanother.TEST_TBL1 is 'テストテーブル1';
COMMENT ON column gspanother.TEST_TBL1.TEST_TBL1_ID is 'TEST_TBL1_ID';
COMMENT ON column gspanother.TEST_TBL1.TEST_NAME is 'TEST_NAME';
