package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * View1エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "VIEW1")
public class View1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** testTbl1Nameプロパティ */
    private String testTbl1Name;
    /**
     * testTbl1Nameを返します。
     *
     * @return testTbl1Name
     */
    @Column(name = "TEST_TBL1_NAME", length = 30, nullable = true, unique = false)
    public String getTestTbl1Name() {
        return testTbl1Name;
    }

    /**
     * testTbl1Nameを設定します。
     *
     * @param testTbl1Name
     */
    public void setTestTbl1Name(String testTbl1Name) {
        this.testTbl1Name = testTbl1Name;
    }
}
