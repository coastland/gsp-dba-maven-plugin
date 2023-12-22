package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * 
 *
 */
@Generated("GSP")
@Entity
@Table(name = "order_detail")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**  */
    private Long orderDetailId;

    /**  */
    private String productName;

    /**  */
    private Long price;
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
    @Column(name = "PRODUCT_NAME", length = 200, nullable = false, unique = false)
    public String getProductName() {
        return productName;
    }

    /**
     * を設定します。
     *
     * @param productName 
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }
    /**
     * を返します。
     *
     * @return 
     */
    @Column(name = "PRICE", precision = 19, nullable = false, unique = false)
    public Long getPrice() {
        return price;
    }

    /**
     * を設定します。
     *
     * @param price 
     */
    public void setPrice(Long price) {
        this.price = price;
    }
}
