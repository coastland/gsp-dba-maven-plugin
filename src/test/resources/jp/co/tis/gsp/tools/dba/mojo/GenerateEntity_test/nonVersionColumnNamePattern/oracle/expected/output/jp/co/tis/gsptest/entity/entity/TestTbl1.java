package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * TestTbl1エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "TEST_TBL1")
public class TestTbl1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** testTbl1Idプロパティ */
    private BigDecimal testTbl1Id;

    /** testNameプロパティ */
    private String testName;

    /** versionNoプロパティ */
    private Long versionNo;
    /**
     * testTbl1Idを返します。
     *
     * @return testTbl1Id
     */
    @Column(name = "TEST_TBL1_ID", nullable = false, unique = false)
    public BigDecimal getTestTbl1Id() {
        return testTbl1Id;
    }

    /**
     * testTbl1Idを設定します。
     *
     * @param testTbl1Id
     */
    public void setTestTbl1Id(BigDecimal testTbl1Id) {
        this.testTbl1Id = testTbl1Id;
    }
    /**
     * testNameを返します。
     *
     * @return testName
     */
    @Column(name = "TEST_NAME", length = 30, nullable = true, unique = false)
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
     * versionNoを返します。
     *
     * @return versionNo
     */
    @Version
    @Column(name = "VERSION_NO", precision = 18, nullable = true, unique = false)
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * versionNoを設定します。
     *
     * @param versionNo
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }
}
