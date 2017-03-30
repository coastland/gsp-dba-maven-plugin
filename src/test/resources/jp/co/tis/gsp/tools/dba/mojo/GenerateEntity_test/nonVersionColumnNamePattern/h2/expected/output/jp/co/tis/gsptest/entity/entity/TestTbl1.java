package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * 
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "PUBLIC", name = "TEST_TBL1")
public class TestTbl1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**  */
    private Long testTbl1Id;

    /**  */
    private String testName;

    /**  */
    private Long versionNo;
    /**
     * を返します。
     *
     * @return 
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEST_TBL1_ID", precision = 19, nullable = false, unique = true)
    public Long getTestTbl1Id() {
        return testTbl1Id;
    }

    /**
     * を設定します。
     *
     * @param testTbl1Id 
     */
    public void setTestTbl1Id(Long testTbl1Id) {
        this.testTbl1Id = testTbl1Id;
    }
    /**
     * を返します。
     *
     * @return 
     */
    @Column(name = "TEST_NAME", length = 30, nullable = true, unique = false)
    public String getTestName() {
        return testName;
    }

    /**
     * を設定します。
     *
     * @param testName 
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
    @Column(name = "VERSION_NO", precision = 19, nullable = true, unique = false)
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * を設定します。
     *
     * @param versionNo 
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }
}
