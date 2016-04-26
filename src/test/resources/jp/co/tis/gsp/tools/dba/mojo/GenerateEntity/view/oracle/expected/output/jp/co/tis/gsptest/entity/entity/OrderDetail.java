package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * OrderDetailエンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "ORDER_DETAIL")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /** orderDetailIdプロパティ */
    private BigDecimal orderDetailId;

    /** productNameプロパティ */
    private String productName;

    /** priceプロパティ */
    private BigDecimal price;
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
     * productNameを返します。
     *
     * @return productName
     */
    @Column(name = "PRODUCT_NAME", length = 200, nullable = false, unique = false)
    public String getProductName() {
        return productName;
    }

    /**
     * productNameを設定します。
     *
     * @param productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }
    /**
     * priceを返します。
     *
     * @return price
     */
    @Column(name = "PRICE", nullable = false, unique = false)
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * priceを設定します。
     *
     * @param price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
