package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * テストテーブル2
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "GSPANOTHER", name = "TEST_TBL2")
public class TestTbl2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TEST_TBL2_ID */
    private BigDecimal testTbl2Id;

    /** TEST_TBL1_ID */
    private BigDecimal testTbl1Id;

    /** TEST_NAME */
    private String testName;

    /** testTbl1関連プロパティ */
    private TestTbl1 testTbl1;
    /**
     * TEST_TBL2_IDを返します。
     *
     * @return TEST_TBL2_ID
     */
    @Id
    @Column(name = "TEST_TBL2_ID", nullable = false, unique = false)
    public BigDecimal getTestTbl2Id() {
        return testTbl2Id;
    }

    /**
     * TEST_TBL2_IDを設定します。
     *
     * @param testTbl2Id TEST_TBL2_ID
     */
    public void setTestTbl2Id(BigDecimal testTbl2Id) {
        this.testTbl2Id = testTbl2Id;
    }
    /**
     * TEST_TBL1_IDを返します。
     *
     * @return TEST_TBL1_ID
     */
    @Id
    @Column(name = "TEST_TBL1_ID", nullable = false, unique = false, insertable = false, updatable = false)
    public BigDecimal getTestTbl1Id() {
        return testTbl1Id;
    }

    /**
     * TEST_TBL1_IDを設定します。
     *
     * @param testTbl1Id TEST_TBL1_ID
     */
    public void setTestTbl1Id(BigDecimal testTbl1Id) {
        this.testTbl1Id = testTbl1Id;
    }
    /**
     * TEST_NAMEを返します。
     *
     * @return TEST_NAME
     */
    @Column(name = "TEST_NAME", length = 20, nullable = true, unique = false)
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

    /**
     * testTbl1を返します。
     *
     * @return testTbl1
     */
    @ManyToOne
    @JoinColumn(name = "TEST_TBL1_ID", referencedColumnName = "TEST_TBL1_ID")
    public TestTbl1 getTestTbl1() {
        return testTbl1;
    }

    /**
     * testTbl1を設定します。
     *
     * @param testTbl1 testTbl1
     */
    public void setTestTbl1(TestTbl1 testTbl1) {
        this.testTbl1 = testTbl1;
    }
}
