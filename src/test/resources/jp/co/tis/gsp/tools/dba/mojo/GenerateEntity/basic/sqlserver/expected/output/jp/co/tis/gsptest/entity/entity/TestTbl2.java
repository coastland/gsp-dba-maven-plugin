package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * TestTbl2エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "TEST_TBL2")
public class TestTbl2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** testTbl2Idプロパティ */
    private Long testTbl2Id;

    /** testTbl1Idプロパティ */
    private Long testTbl1Id;

    /** testNameプロパティ */
    private String testName;

    /** testTbl1関連プロパティ */
    private TestTbl1 testTbl1;
    /**
     * testTbl2Idを返します。
     *
     * @return testTbl2Id
     */
    @Id
    @Column(name = "TEST_TBL2_ID", precision = 19, nullable = false, unique = false)
    public Long getTestTbl2Id() {
        return testTbl2Id;
    }

    /**
     * testTbl2Idを設定します。
     *
     * @param testTbl2Id
     */
    public void setTestTbl2Id(Long testTbl2Id) {
        this.testTbl2Id = testTbl2Id;
    }
    /**
     * testTbl1Idを返します。
     *
     * @return testTbl1Id
     */
    @Id
    @Column(name = "TEST_TBL1_ID", precision = 19, nullable = false, unique = false, insertable = false, updatable = false)
    public Long getTestTbl1Id() {
        return testTbl1Id;
    }

    /**
     * testTbl1Idを設定します。
     *
     * @param testTbl1Id
     */
    public void setTestTbl1Id(Long testTbl1Id) {
        this.testTbl1Id = testTbl1Id;
    }
    /**
     * testNameを返します。
     *
     * @return testName
     */
    @Column(name = "TEST_NAME", length = 20, nullable = true, unique = false)
    public String getTestName() {
        return testName;
    }

    /**
     * testNameを設定します。
     *
     * @param testName
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
