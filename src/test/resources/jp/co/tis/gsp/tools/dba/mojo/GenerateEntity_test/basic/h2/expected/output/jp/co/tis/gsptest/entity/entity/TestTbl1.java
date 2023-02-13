package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
    private Long testTbl1Id;

    /** TEST_NAME */
    private String testName;

    /** testTbl2関連プロパティ */
    private TestTbl2 testTbl2;
    /**
     * TEST_TBL1_IDを返します。
     *
     * @return TEST_TBL1_ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEST_TBL1_ID", precision = 64, nullable = false, unique = true)
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
     * testTbl2を返します。
     *
     * @return testTbl2
     */
    @OneToOne(mappedBy = "testTbl1")
    public TestTbl2 getTestTbl2() {
        return testTbl2;
    }

    /**
     * testTbl2を設定します。
     *
     * @param testTbl2 testTbl2
     */
    public void setTestTbl2(TestTbl2 testTbl2) {
        this.testTbl2 = testTbl2;
    }
}
