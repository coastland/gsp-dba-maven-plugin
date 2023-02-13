package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.io.Serializable;

/**
 * TestTbl1エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "test_tbl1")
public class TestTbl1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** testTbl1Idプロパティ */
    private Integer testTbl1Id;

    /** testNameプロパティ */
    private String testName;

    /** versionNoプロパティ */
    private Long versionNo;
    /**
     * testTbl1Idを返します。
     *
     * @return testTbl1Id
     */
    @Column(name = "test_tbl1_id", precision = 10, nullable = false, unique = false)
    public Integer getTestTbl1Id() {
        return testTbl1Id;
    }

    /**
     * testTbl1Idを設定します。
     *
     * @param testTbl1Id
     */
    public void setTestTbl1Id(Integer testTbl1Id) {
        this.testTbl1Id = testTbl1Id;
    }
    /**
     * testNameを返します。
     *
     * @return testName
     */
    @Column(name = "test_name", length = 30, nullable = true, unique = false)
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
    @Column(name = "version_no", precision = 19, nullable = true, unique = false)
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
