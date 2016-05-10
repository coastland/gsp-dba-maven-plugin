-- シーケンス
CREATE SEQUENCE gspanother.test_seq1 START 1;

-- シノニム
-- Postgresqlにはシノニムなし

-- プロシージャ
CREATE FUNCTION gspanother.PROC_SELECT_TEST_TBL1(INTEGER) RETURNS INTEGER AS 'DECLARE param1 ALIAS FOR $1; retval INTEGER; BEGIN SELECT TEST_TBL1_ID INTO retval FROM gspanother.TEST_TBL1 WHERE TEST_TBL1_ID = $1; RETURN retval; END;'LANGUAGE 'plpgsql';

-- ファンクション
CREATE FUNCTION gspanother.set_action() RETURNS TRIGGER AS $$ BEGIN IF (TG_OP = 'INSERT') THEN NEW.TEST_NAME := 'INSERT'; ELSIF (TG_OP = 'UPDATE') THEN NEW.TEST_NAME := 'UPDATE'; END IF; RETURN NEW; END; $$ LANGUAGE plpgsql;


-- トリガ
CREATE TRIGGER trigger_set_action BEFORE INSERT OR UPDATE ON gspanother.TEST_TBL1 FOR EACH ROW EXECUTE PROCEDURE gspanother.set_action();