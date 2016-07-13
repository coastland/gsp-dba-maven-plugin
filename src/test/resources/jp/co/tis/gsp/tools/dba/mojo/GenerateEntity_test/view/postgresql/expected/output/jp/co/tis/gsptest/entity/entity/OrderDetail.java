package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
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
@Table(name = "order_detail")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /** orderDetailIdプロパティ */
    private Long orderDetailId;

    /** productNameプロパティ */
    private String productName;

    /** priceプロパティ */
    private Long price;
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
     * productNameを返します。
     *
     * @return productName
     */
    @Column(name = "product_name", length = 200, nullable = false, unique = false)
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
    @Column(name = "price", precision = 19, nullable = false, unique = false)
    public Long getPrice() {
        return price;
    }

    /**
     * priceを設定します。
     *
     * @param price
     */
    public void setPrice(Long price) {
        this.price = price;
    }
}
