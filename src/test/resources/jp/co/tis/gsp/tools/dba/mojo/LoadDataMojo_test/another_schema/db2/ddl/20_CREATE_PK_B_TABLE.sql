ALTER TABLE GSPANOTHER.B_TABLE
ADD CONSTRAINT PK_B_TABLE PRIMARY KEY
(
  B_ID
);
CREATE UNIQUE INDEX PK_B_TABLE ON GSPANOTHER.B_TABLE
(
  B_ID
);