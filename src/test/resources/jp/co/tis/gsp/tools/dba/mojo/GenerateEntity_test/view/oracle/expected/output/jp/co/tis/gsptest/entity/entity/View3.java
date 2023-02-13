package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * View3エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "VIEW3")
public class View3 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** oidプロパティ */
    private BigDecimal oid;

    /** customerNameプロパティ */
    private String customerName;
    /**
     * oidを返します。
     *
     * @return oid
     */
    @Column(name = "OID", nullable = false, unique = false)
    public BigDecimal getOid() {
        return oid;
    }

    /**
     * oidを設定します。
     *
     * @param oid
     */
    public void setOid(BigDecimal oid) {
        this.oid = oid;
    }
    /**
     * customerNameを返します。
     *
     * @return customerName
     */
    @Column(name = "CUSTOMER_NAME", length = 30, nullable = true, unique = false)
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
