CREATE TABLE N_AUTO2 (
  N_AUTO2_NID BIGINT NOT NULL,
  TEST_NAME VARCHAR(30)
);
COMMENT ON table N_AUTO2 is 'NOT自動採番２';
COMMENT ON column N_AUTO2.N_AUTO2_NID is 'N_AUTO2_NID';
COMMENT ON column N_AUTO2.TEST_NAME is 'TEST_NAME';
