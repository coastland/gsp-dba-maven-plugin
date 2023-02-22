package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * 
 *
 */
@Generated("GSP")
@Entity
@Table(name = "view1")
public class View1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TEST_NAME */
    private String testTbl1Name;
    /**
     * TEST_NAMEを返します。
     *
     * @return TEST_NAME
     */
    @Column(name = "TEST_TBL1_NAME", length = 30, nullable = true, unique = false)
    public String getTestTbl1Name() {
        return testTbl1Name;
    }

    /**
     * TEST_NAMEを設定します。
     *
     * @param testTbl1Name TEST_NAME
     */
    public void setTestTbl1Name(String testTbl1Name) {
        this.testTbl1Name = testTbl1Name;
    }
}
