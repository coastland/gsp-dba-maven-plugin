package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * View2エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "view2")
public class View2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** test1プロパティ */
    private String test1;

    /** test2プロパティ */
    private BigDecimal test2;
    /**
     * test1を返します。
     *
     * @return test1
     */
    @Column(name = "test1", length = 30, nullable = true, unique = false)
    public String getTest1() {
        return test1;
    }

    /**
     * test1を設定します。
     *
     * @param test1
     */
    public void setTest1(String test1) {
        this.test1 = test1;
    }
    /**
     * test2を返します。
     *
     * @return test2
     */
    @Column(name = "test2", precision = 131089, nullable = true, unique = false)
    public BigDecimal getTest2() {
        return test2;
    }

    /**
     * test2を設定します。
     *
     * @param test2
     */
    public void setTest2(BigDecimal test2) {
        this.test2 = test2;
    }
}
