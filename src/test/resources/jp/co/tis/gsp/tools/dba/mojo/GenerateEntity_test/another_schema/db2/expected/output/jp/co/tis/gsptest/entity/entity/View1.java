package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * View1エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "GSPANOTHER", name = "VIEW1")
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
