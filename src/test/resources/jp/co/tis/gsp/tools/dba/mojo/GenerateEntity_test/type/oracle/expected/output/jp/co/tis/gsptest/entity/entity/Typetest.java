package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * タイプテスト
 *
 */
@Generated("GSP")
@Entity
@Table(name = "TYPETEST")
public class Typetest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TYPE1 */
    private byte[] type1;

    /** TYPE2 */
    private String type2;

    /** TYPE3 */
    private String type3;

    /** TYPE4 */
    private Date type4;

    /** TYPE5 */
    private String type5;

    /** TYPE6 */
    private String type6;

    /** TYPE7 */
    private String type7;

    /** TYPE8 */
    private boolean type8;

    /** TYPE9 */
    private Short type9;

    /** TYPE10 */
    private Integer type10;

    /** TYPE11 */
    private Long type11;

    /** TYPE12 */
    private BigInteger type12;

    /** TYPE13 */
    private BigDecimal type13;

    /** TYPE14 */
    private String type14;

    /** TYPE15 */
    private Date type15;

    /** TYPE16 */
    private String type16;
    /**
     * TYPE1を返します。
     *
     * @return TYPE1
     */
    @Lob
    @Column(name = "TYPE1", length = 4000, nullable = true, unique = false)
    public byte[] getType1() {
        return type1;
    }

    /**
     * TYPE1を設定します。
     *
     * @param type1 TYPE1
     */
    public void setType1(byte[] type1) {
        this.type1 = type1;
    }
    /**
     * TYPE2を返します。
     *
     * @return TYPE2
     */
    @Column(name = "TYPE2", length = 10, nullable = true, unique = false)
    public String getType2() {
        return type2;
    }

    /**
     * TYPE2を設定します。
     *
     * @param type2 TYPE2
     */
    public void setType2(String type2) {
        this.type2 = type2;
    }
    /**
     * TYPE3を返します。
     *
     * @return TYPE3
     */
    @Lob
    @Column(name = "TYPE3", length = 4000, nullable = true, unique = false)
    public String getType3() {
        return type3;
    }

    /**
     * TYPE3を設定します。
     *
     * @param type3 TYPE3
     */
    public void setType3(String type3) {
        this.type3 = type3;
    }
    /**
     * TYPE4を返します。
     *
     * @return TYPE4
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "TYPE4", nullable = true, unique = false)
    public Date getType4() {
        return type4;
    }

    /**
     * TYPE4を設定します。
     *
     * @param type4 TYPE4
     */
    public void setType4(Date type4) {
        this.type4 = type4;
    }
    /**
     * TYPE5を返します。
     *
     * @return TYPE5
     */
    @Column(name = "TYPE5", nullable = true, unique = false)
    public String getType5() {
        return type5;
    }

    /**
     * TYPE5を設定します。
     *
     * @param type5 TYPE5
     */
    public void setType5(String type5) {
        this.type5 = type5;
    }
    /**
     * TYPE6を返します。
     *
     * @return TYPE6
     */
    @Column(name = "TYPE6", length = 10, nullable = true, unique = false)
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
    @Lob
    @Column(name = "TYPE7", length = 4000, nullable = true, unique = false)
    public String getType7() {
        return type7;
    }

    /**
     * TYPE7を設定します。
     *
     * @param type7 TYPE7
     */
    public void setType7(String type7) {
        this.type7 = type7;
    }
    /**
     * TYPE8を返します。
     *
     * @return TYPE8
     */
    @Column(name = "TYPE8", length = 1, nullable = true, unique = false)
    public boolean isType8() {
        return type8;
    }

    /**
     * TYPE8を設定します。
     *
     * @param type8 TYPE8
     */
    public void setType8(boolean type8) {
        this.type8 = type8;
    }
    /**
     * TYPE9を返します。
     *
     * @return TYPE9
     */
    @Column(name = "TYPE9", precision = 4, nullable = true, unique = false)
    public Short getType9() {
        return type9;
    }

    /**
     * TYPE9を設定します。
     *
     * @param type9 TYPE9
     */
    public void setType9(Short type9) {
        this.type9 = type9;
    }
    /**
     * TYPE10を返します。
     *
     * @return TYPE10
     */
    @Column(name = "TYPE10", precision = 9, nullable = true, unique = false)
    public Integer getType10() {
        return type10;
    }

    /**
     * TYPE10を設定します。
     *
     * @param type10 TYPE10
     */
    public void setType10(Integer type10) {
        this.type10 = type10;
    }
    /**
     * TYPE11を返します。
     *
     * @return TYPE11
     */
    @Column(name = "TYPE11", precision = 18, nullable = true, unique = false)
    public Long getType11() {
        return type11;
    }

    /**
     * TYPE11を設定します。
     *
     * @param type11 TYPE11
     */
    public void setType11(Long type11) {
        this.type11 = type11;
    }
    /**
     * TYPE12を返します。
     *
     * @return TYPE12
     */
    @Column(name = "TYPE12", precision = 19, nullable = true, unique = false)
    public BigInteger getType12() {
        return type12;
    }

    /**
     * TYPE12を設定します。
     *
     * @param type12 TYPE12
     */
    public void setType12(BigInteger type12) {
        this.type12 = type12;
    }
    /**
     * TYPE13を返します。
     *
     * @return TYPE13
     */
    @Column(name = "TYPE13", precision = 18, scale = 1, nullable = true, unique = false)
    public BigDecimal getType13() {
        return type13;
    }

    /**
     * TYPE13を設定します。
     *
     * @param type13 TYPE13
     */
    public void setType13(BigDecimal type13) {
        this.type13 = type13;
    }
    /**
     * TYPE14を返します。
     *
     * @return TYPE14
     */
    @Column(name = "TYPE14", length = 10, nullable = true, unique = false)
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TYPE15", nullable = true, unique = false)
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
    @Column(name = "TYPE16", length = 10, nullable = true, unique = false)
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
