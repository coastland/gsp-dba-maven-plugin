package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Table;

/**
 * タイプテスト
 *
 */
@Generated("GSP")
@Entity
@Table(name = "typetest")
public class Typetest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TYPE1 */
    @Column(name = "type1")
    private Long type1;

    /** TYPE2 */
    @Column(name = "type2")
    private Long type2;

    /** TYPE3 */
    @Column(name = "type3")
    private byte[] type3;

    /** TYPE4 */
    @Column(name = "type4")
    private boolean type4;

    /** TYPE5 */
    @Column(name = "type5")
    private byte[] type5;

    /** TYPE6 */
    @Column(name = "type6")
    private String type6;

    /** TYPE7 */
    @Column(name = "type7")
    private Date type7;

    /** TYPE8 */
    @Column(name = "type8")
    private Double type8;

    /** TYPE9 */
    @Column(name = "type9")
    private Integer type9;

    /** TYPE10 */
    @Column(name = "type10")
    private BigDecimal type10;

    /** TYPE11 */
    @Column(name = "type11")
    private Float type11;

    /** TYPE12 */
    @Column(name = "type12")
    private Integer type12;

    /** TYPE13 */
    @Column(name = "type13")
    private Short type13;

    /** TYPE14 */
    @Column(name = "type14")
    private String type14;

    /** TYPE15 */
    @Column(name = "type15")
    private Date type15;

    /** TYPE16 */
    @Column(name = "type16")
    private String type16;
    /**
     * TYPE1を返します。
     *
     * @return TYPE1
     */
    public Long getType1() {
        return type1;
    }

    /**
     * TYPE1を設定します。
     *
     * @param type1 TYPE1
     */
    public void setType1(Long type1) {
        this.type1 = type1;
    }
    /**
     * TYPE2を返します。
     *
     * @return TYPE2
     */
    public Long getType2() {
        return type2;
    }

    /**
     * TYPE2を設定します。
     *
     * @param type2 TYPE2
     */
    public void setType2(Long type2) {
        this.type2 = type2;
    }
    /**
     * TYPE3を返します。
     *
     * @return TYPE3
     */
    public byte[] getType3() {
        return type3;
    }

    /**
     * TYPE3を設定します。
     *
     * @param type3 TYPE3
     */
    public void setType3(byte[] type3) {
        this.type3 = type3;
    }
    /**
     * TYPE4を返します。
     *
     * @return TYPE4
     */
    public boolean isType4() {
        return type4;
    }

    /**
     * TYPE4を設定します。
     *
     * @param type4 TYPE4
     */
    public void setType4(boolean type4) {
        this.type4 = type4;
    }
    /**
     * TYPE5を返します。
     *
     * @return TYPE5
     */
    public byte[] getType5() {
        return type5;
    }

    /**
     * TYPE5を設定します。
     *
     * @param type5 TYPE5
     */
    public void setType5(byte[] type5) {
        this.type5 = type5;
    }
    /**
     * TYPE6を返します。
     *
     * @return TYPE6
     */
    public String getType6() {
        return type6;
    }

    /**
     * TYPE6を設定します。
     *
     * @param type6 TYPE6
     */
    public void setType6(String type6) {
        this.type6 = type6;
    }
    /**
     * TYPE7を返します。
     *
     * @return TYPE7
     */
    public Date getType7() {
        return type7;
    }

    /**
     * TYPE7を設定します。
     *
     * @param type7 TYPE7
     */
    public void setType7(Date type7) {
        this.type7 = type7;
    }
    /**
     * TYPE8を返します。
     *
     * @return TYPE8
     */
    public Double getType8() {
        return type8;
    }

    /**
     * TYPE8を設定します。
     *
     * @param type8 TYPE8
     */
    public void setType8(Double type8) {
        this.type8 = type8;
    }
    /**
     * TYPE9を返します。
     *
     * @return TYPE9
     */
    public Integer getType9() {
        return type9;
    }

    /**
     * TYPE9を設定します。
     *
     * @param type9 TYPE9
     */
    public void setType9(Integer type9) {
        this.type9 = type9;
    }
    /**
     * TYPE10を返します。
     *
     * @return TYPE10
     */
    public BigDecimal getType10() {
        return type10;
    }

    /**
     * TYPE10を設定します。
     *
     * @param type10 TYPE10
     */
    public void setType10(BigDecimal type10) {
        this.type10 = type10;
    }
    /**
     * TYPE11を返します。
     *
     * @return TYPE11
     */
    public Float getType11() {
        return type11;
    }

    /**
     * TYPE11を設定します。
     *
     * @param type11 TYPE11
     */
    public void setType11(Float type11) {
        this.type11 = type11;
    }
    /**
     * TYPE12を返します。
     *
     * @return TYPE12
     */
    public Integer getType12() {
        return type12;
    }

    /**
     * TYPE12を設定します。
     *
     * @param type12 TYPE12
     */
    public void setType12(Integer type12) {
        this.type12 = type12;
    }
    /**
     * TYPE13を返します。
     *
     * @return TYPE13
     */
    public Short getType13() {
        return type13;
    }

    /**
     * TYPE13を設定します。
     *
     * @param type13 TYPE13
     */
    public void setType13(Short type13) {
        this.type13 = type13;
    }
    /**
     * TYPE14を返します。
     *
     * @return TYPE14
     */
    public String getType14() {
        return type14;
    }

    /**
     * TYPE14を設定します。
     *
     * @param type14 TYPE14
     */
    public void setType14(String type14) {
        this.type14 = type14;
    }
    /**
     * TYPE15を返します。
     *
     * @return TYPE15
     */
    public Date getType15() {
        return type15;
    }

    /**
     * TYPE15を設定します。
     *
     * @param type15 TYPE15
     */
    public void setType15(Date type15) {
        this.type15 = type15;
    }
    /**
     * TYPE16を返します。
     *
     * @return TYPE16
     */
    public String getType16() {
        return type16;
    }

    /**
     * TYPE16を設定します。
     *
     * @param type16 TYPE16
     */
    public void setType16(String type16) {
        this.type16 = type16;
    }
}
