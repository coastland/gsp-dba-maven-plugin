package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * テストテーブル1
 *
 */
@Generated("GSP")
@Entity
@Table(name = "TEST_TBL1")
public class TestTbl1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TEST_TBL1_ID */
    private BigDecimal testTbl1Id;

    /** TEST_NAME */
    private String testName;

    /** testTbl2List関連プロパティ */
    private List<TestTbl2> testTbl2List;
    /**
     * TEST_TBL1_IDを返します。
     *
     * @return TEST_TBL1_ID
     */
    @Id
    @GeneratedValue(generator = "TEST_TBL1_ID_SEQ", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "TEST_TBL1_ID_SEQ", sequenceName = "TEST_TBL1_ID_SEQ", initialValue = 1, allocationSize = 1)
    @Column(name = "TEST_TBL1_ID", nullable = false, unique = true)
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
    @Column(name = "TEST_NAME", length = 30, nullable = true, unique = false)
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
     * testTbl2Listを返します。
     *
     * @return testTbl2List
     */
    @OneToMany(mappedBy = "testTbl1")
    public List<TestTbl2> getTestTbl2List() {
        return testTbl2List;
    }

    /**
     * testTbl2Listを設定します。
     *
     * @param testTbl2List testTbl2List
     */
    public void setTestTbl2List(List<TestTbl2> testTbl2List) {
        this.testTbl2List = testTbl2List;
    }
}
