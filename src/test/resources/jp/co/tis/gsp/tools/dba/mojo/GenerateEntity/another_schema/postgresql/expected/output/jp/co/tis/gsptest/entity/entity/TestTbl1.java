package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * テストテーブル1
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "gspanother", name = "test_tbl1")
public class TestTbl1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TEST_TBL1_ID */
    private Integer testTbl1Id;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_tbl1_id", precision = 10, nullable = false, unique = true)
    public Integer getTestTbl1Id() {
        return testTbl1Id;
    }

    /**
     * TEST_TBL1_IDを設定します。
     *
     * @param testTbl1Id TEST_TBL1_ID
     */
    public void setTestTbl1Id(Integer testTbl1Id) {
        this.testTbl1Id = testTbl1Id;
    }
    /**
     * TEST_NAMEを返します。
     *
     * @return TEST_NAME
     */
    @Column(name = "test_name", length = 30, nullable = true, unique = false)
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
