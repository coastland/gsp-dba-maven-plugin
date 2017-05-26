package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

/**
 * テストテーブル2
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "PUBLIC", name = "TEST_TBL2")
public class TestTbl2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TEST_TBL2_ID */
    @Column(name = "TEST_TBL2_ID")
    private Long testTbl2Id;

    /** TEST_TBL1_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEST_TBL1_ID")
    private Long testTbl1Id;

    /** TEST_NAME */
    @Column(name = "TEST_NAME")
    private String testName;
    /**
     * TEST_TBL2_IDを返します。
     *
     * @return TEST_TBL2_ID
     */
    public Long getTestTbl2Id() {
        return testTbl2Id;
    }

    /**
     * TEST_TBL2_IDを設定します。
     *
     * @param testTbl2Id TEST_TBL2_ID
     */
    public void setTestTbl2Id(Long testTbl2Id) {
        this.testTbl2Id = testTbl2Id;
    }
    /**
     * TEST_TBL1_IDを返します。
     *
     * @return TEST_TBL1_ID
     */
    public Long getTestTbl1Id() {
        return testTbl1Id;
    }

    /**
     * TEST_TBL1_IDを設定します。
     *
     * @param testTbl1Id TEST_TBL1_ID
     */
    public void setTestTbl1Id(Long testTbl1Id) {
        this.testTbl1Id = testTbl1Id;
    }
    /**
     * TEST_NAMEを返します。
     *
     * @return TEST_NAME
     */
    public String getTestName() {
        return testName;
    }

    /**
     * TEST_NAMEを設定します。
     *
     * @param testName TEST_NAME
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }
}
