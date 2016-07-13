package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * テストテーブル1
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "PUBLIC", name = "TEST_TBL1")
public class TestTbl1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TEST_TBL1_ID */
// templateFilePrimaryDir_TEST!
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.AUTO)
    @Column(name = "TEST_TBL1_ID", precision = 19, nullable = false, unique = true)
    public Long testTbl1Id;

    /** TEST_NAME */
// templateFilePrimaryDir_TEST!
    @Column(name = "TEST_NAME", length = 30, nullable = true, unique = false)
    public String testName;
}
