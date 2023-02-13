package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * View2エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "view2")
public class View2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** testNameプロパティ */
    private String testName;
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
}
