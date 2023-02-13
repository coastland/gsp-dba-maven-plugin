package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * View4エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "view4")
public class View4 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** oidプロパティ */
    private Long oid;

    /** customerNameプロパティ */
    private String customerName;
    /**
     * oidを返します。
     *
     * @return oid
     */
    @Column(name = "oid", precision = 19, nullable = true, unique = false)
    public Long getOid() {
        return oid;
    }

    /**
     * oidを設定します。
     *
     * @param oid
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    /**
     * customerNameを返します。
     *
     * @return customerName
     */
    @Column(name = "customer_name", length = 30, nullable = true, unique = false)
    public String getCustomerName() {
        return customerName;
    }

    /**
     * customerNameを設定します。
     *
     * @param customerName
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
