CREATE TABLE SCHEMA_TEST.INDEX_TEST3 (
  INDEX_TEST3_ID NUMBER(18) NOT NULL ,
  SUB_ID_1 NUMBER(18) NOT NULL ,
  SUB_ID_2 NUMBER(18) NOT NULL 
);
COMMENT ON table SCHEMA_TEST.INDEX_TEST3 is 'INDEX_TEST3';
COMMENT ON column SCHEMA_TEST.INDEX_TEST3.INDEX_TEST3_ID is 'INDEX_TEST3_ID';
COMMENT ON column SCHEMA_TEST.INDEX_TEST3.SUB_ID_1 is 'SUB_ID_1';
COMMENT ON column SCHEMA_TEST.INDEX_TEST3.SUB_ID_2 is 'SUB_ID_2';
CREATE SEQUENCE SCHEMA_TEST.INDEX_TEST3_ID_SEQ increment by 1 start with 1;

