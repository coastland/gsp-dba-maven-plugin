package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * View2エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "VIEW2")
public class View2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** test1プロパティ */
    private String test1;

    /** test2プロパティ */
    private Long test2;
    /**
     * test1を返します。
     *
     * @return test1
     */
    @Column(name = "TEST1", length = 30, nullable = true, unique = false)
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
    @Column(name = "TEST2", precision = 19, nullable = true, unique = false)
    public Long getTest2() {
        return test2;
    }

    /**
     * test2を設定します。
     *
     * @param test2
     */
    public void setTest2(Long test2) {
        this.test2 = test2;
    }
}
