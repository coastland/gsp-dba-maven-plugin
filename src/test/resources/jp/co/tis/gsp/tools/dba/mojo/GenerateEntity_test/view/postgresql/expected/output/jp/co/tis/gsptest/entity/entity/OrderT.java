package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * OrderTエンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "order_t")
public class OrderT implements Serializable {

    private static final long serialVersionUID = 1L;

    /** orderIdプロパティ */
    private Long orderId;

    /** orderDetailIdプロパティ */
    private Long orderDetailId;

    /** customerプロパティ */
    private String customer;

    /** orderDateプロパティ */
    private Date orderDate;
    /**
     * orderIdを返します。
     *
     * @return orderId
     */
    @Column(name = "order_id", precision = 19, nullable = false, unique = false)
    public Long getOrderId() {
        return orderId;
    }

    /**
     * orderIdを設定します。
     *
     * @param orderId
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    /**
     * orderDetailIdを返します。
     *
     * @return orderDetailId
     */
    @Column(name = "order_detail_id", precision = 19, nullable = false, unique = false)
    public Long getOrderDetailId() {
        return orderDetailId;
    }

    /**
     * orderDetailIdを設定します。
     *
     * @param orderDetailId
     */
    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
    }
    /**
     * customerを返します。
     *
     * @return customer
     */
    @Column(name = "customer", length = 30, nullable = true, unique = false)
    public String getCustomer() {
        return customer;
    }

    /**
     * customerを設定します。
     *
     * @param customer
     */
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    /**
     * orderDateを返します。
     *
     * @return orderDate
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "order_date", nullable = true, unique = false)
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * orderDateを設定します。
     *
     * @param orderDate
     */
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
