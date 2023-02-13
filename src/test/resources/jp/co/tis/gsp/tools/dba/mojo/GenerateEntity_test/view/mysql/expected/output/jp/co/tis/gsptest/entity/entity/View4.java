package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 *
 */
@Generated("GSP")
@Entity
@Table(name = "view4")
public class View4 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**  */
    private Long oid;

    /**  */
    private String customerName;
    /**
     * を返します。
     *
     * @return 
     */
    @Column(name = "OID", precision = 19, nullable = false, unique = false)
    public Long getOid() {
        return oid;
    }

    /**
     * を設定します。
     *
     * @param oid 
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    /**
     * を返します。
     *
     * @return 
     */
    @Column(name = "CUSTOMER_NAME", length = 30, nullable = true, unique = false)
    public String getCustomerName() {
        return customerName;
    }

    /**
     * を設定します。
     *
     * @param customerName 
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
