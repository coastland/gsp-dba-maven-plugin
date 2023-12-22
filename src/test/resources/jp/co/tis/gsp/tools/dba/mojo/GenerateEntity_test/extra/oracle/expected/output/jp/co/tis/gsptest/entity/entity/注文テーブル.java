package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
    @GeneratedValue(generator = "ORDER_ID_SEQ", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "ORDER_ID_SEQ", sequenceName = "ORDER_ID_SEQ", initialValue = 1, allocationSize = 1)
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
