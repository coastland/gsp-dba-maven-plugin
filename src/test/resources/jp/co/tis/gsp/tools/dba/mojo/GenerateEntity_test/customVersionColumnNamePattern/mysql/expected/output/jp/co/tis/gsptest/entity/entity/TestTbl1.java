package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.io.Serializable;

/**
 * 
 *
 */
@Generated("GSP")
@Entity
@Table(name = "test_tbl1")
public class TestTbl1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TEST_TBL1_ID */
    private Long testTbl1Id;

    /** TEST_NAME */
    private String testName;

    /**  */
    private Long hoge;
    /**
     * TEST_TBL1_IDを返します。
     *
     * @return TEST_TBL1_ID
     */
    @Column(name = "TEST_TBL1_ID", precision = 19, nullable = false, unique = false)
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
     * を返します。
     *
     * @return 
     */
    @Version
    @Column(name = "HOGE", precision = 19, nullable = true, unique = false)
    public Long getHoge() {
        return hoge;
    }

    /**
     * を設定します。
     *
     * @param hoge 
     */
    public void setHoge(Long hoge) {
        this.hoge = hoge;
    }
}
