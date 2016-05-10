package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * View4エンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "VIEW4")
public class View4 implements Serializable {

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
    @Column(name = "OID", nullable = true, unique = false)
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
