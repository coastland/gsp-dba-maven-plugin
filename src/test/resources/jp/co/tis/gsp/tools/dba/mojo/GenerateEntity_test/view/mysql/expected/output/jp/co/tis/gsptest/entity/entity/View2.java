package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 *
 */
@Generated("GSP")
@Entity
@Table(name = "view2")
public class View2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**  */
    private String test1;

    /**  */
    private BigDecimal test2;
    /**
     * を返します。
     *
     * @return 
     */
    @Column(name = "TEST1", length = 30, nullable = true, unique = false)
    public String getTest1() {
        return test1;
    }

    /**
     * を設定します。
     *
     * @param test1 
     */
    public void setTest1(String test1) {
        this.test1 = test1;
    }
    /**
     * を返します。
     *
     * @return 
     */
    @Column(name = "TEST2", precision = 41, nullable = true, unique = false)
    public BigDecimal getTest2() {
        return test2;
    }

    /**
     * を設定します。
     *
     * @param test2 
     */
    public void setTest2(BigDecimal test2) {
        this.test2 = test2;
    }
}
