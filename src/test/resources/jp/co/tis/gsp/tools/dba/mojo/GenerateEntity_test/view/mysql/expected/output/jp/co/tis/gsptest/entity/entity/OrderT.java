package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 *
 */
@Generated("GSP")
@Entity
@Table(name = "order_t")
public class OrderT implements Serializable {

    private static final long serialVersionUID = 1L;

    /**  */
    private Long orderId;

    /**  */
    private Long orderDetailId;

    /**  */
    private String customer;

    /**  */
    private Date orderDate;
    /**
     * を返します。
     *
     * @return 
     */
    @Column(name = "ORDER_ID", precision = 19, nullable = false, unique = false)
    public Long getOrderId() {
        return orderId;
    }

    /**
     * を設定します。
     *
     * @param orderId 
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    /**
     * を返します。
     *
     * @return 
     */
    @Column(name = "ORDER_DETAIL_ID", precision = 19, nullable = false, unique = false)
    public Long getOrderDetailId() {
        return orderDetailId;
    }

    /**
     * を設定します。
     *
     * @param orderDetailId 
     */
    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
    }
    /**
     * を返します。
     *
     * @return 
     */
    @Column(name = "CUSTOMER", length = 30, nullable = true, unique = false)
    public String getCustomer() {
        return customer;
    }

    /**
     * を設定します。
     *
     * @param customer 
     */
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    /**
     * を返します。
     *
     * @return 
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE", nullable = true, unique = false)
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * を設定します。
     *
     * @param orderDate 
     */
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
