package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 注文テーブルエンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "注文テーブル")
public class 注文テーブル implements Serializable {

    private static final long serialVersionUID = 1L;

    /** orderIdプロパティ */
    private BigDecimal orderId;

    /** orderDetailIdプロパティ */
    private BigDecimal orderDetailId;

    /** customerプロパティ */
    private String customer;

    /** orderDateプロパティ */
    private Date orderDate;
    /**
     * orderIdを返します。
     *
     * @return orderId
     */
    @Id
    @Column(name = "ORDER_ID", nullable = false, unique = false)
    public BigDecimal getOrderId() {
        return orderId;
    }

    /**
     * orderIdを設定します。
     *
     * @param orderId
     */
    public void setOrderId(BigDecimal orderId) {
        this.orderId = orderId;
    }
    /**
     * orderDetailIdを返します。
     *
     * @return orderDetailId
     */
    @Column(name = "ORDER_DETAIL_ID", nullable = false, unique = false)
    public BigDecimal getOrderDetailId() {
        return orderDetailId;
    }

    /**
     * orderDetailIdを設定します。
     *
     * @param orderDetailId
     */
    public void setOrderDetailId(BigDecimal orderDetailId) {
        this.orderDetailId = orderDetailId;
    }
    /**
     * customerを返します。
     *
     * @return customer
     */
    @Column(name = "CUSTOMER", length = 30, nullable = true, unique = false)
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
    @Column(name = "ORDER_DATE", nullable = true, unique = false)
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
