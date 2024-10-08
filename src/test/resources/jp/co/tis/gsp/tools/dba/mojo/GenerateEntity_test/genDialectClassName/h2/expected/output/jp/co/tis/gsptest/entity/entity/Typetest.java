package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * タイプテスト
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "PUBLIC", name = "TYPETEST")
public class Typetest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TYPE1 */
    private Long type1;

    /** TYPE2 */
    private byte[] type2;

    /** TYPE3 */
    private byte[] type3;

    /** TYPE4 */
    private String type4;

    /** TYPE5 */
    private String type5;

    /** TYPE6 */
    private String type6;

    /** TYPE7 */
    private BigDecimal type7;

    /** TYPE8 */
    private Long type8;

    /** TYPE9 */
    private Integer type9;

    /** TYPE10 */
    private String type10;

    /** TYPE11 */
    private Float type11;

    /** TYPE12 */
    private Short type12;

    /** TYPE13 */
    private Time type13;

    /** TYPE14 */
    private Timestamp type14;

    /** TYPE15 */
    private String type15;

    /** TYPE16 */
    private String type16;
    /**
     * TYPE1を返します。
     *
     * @return TYPE1
     */
    @Column(name = "TYPE1", precision = 64, nullable = true, unique = false)
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
    @Column(name = "TYPE2", length = 1, nullable = true, unique = false)
    public byte[] getType2() {
        return type2;
    }

    /**
     * TYPE2を設定します。
     *
     * @param type2 TYPE2
     */
    public void setType2(byte[] type2) {
        this.type2 = type2;
    }
    /**
     * TYPE3を返します。
     *
     * @return TYPE3
     */
    @Lob
    @Column(name = "TYPE3", length = 2147483647, nullable = true, unique = false)
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
    @Column(name = "TYPE4", length = 1, nullable = true, unique = false)
    public String getType4() {
        return type4;
    }

    /**
     * TYPE4を設定します。
     *
     * @param type4 TYPE4
     */
    public void setType4(String type4) {
        this.type4 = type4;
    }
    /**
     * TYPE5を返します。
     *
     * @return TYPE5
     */
    @Lob
    @Column(name = "TYPE5", length = 2147483647, nullable = true, unique = false)
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
    @Column(name = "TYPE7", precision = 100000, nullable = true, unique = false)
    public BigDecimal getType7() {
        return type7;
    }

    /**
     * TYPE7を設定します。
     *
     * @param type7 TYPE7
     */
    public void setType7(BigDecimal type7) {
        this.type7 = type7;
    }
    /**
     * TYPE8を返します。
     *
     * @return TYPE8
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TYPE8", precision = 64, nullable = false, unique = true)
    public Long getType8() {
        return type8;
    }

    /**
     * TYPE8を設定します。
     *
     * @param type8 TYPE8
     */
    public void setType8(Long type8) {
        this.type8 = type8;
    }
    /**
     * TYPE9を返します。
     *
     * @return TYPE9
     */
    @Column(name = "TYPE9", precision = 32, nullable = true, unique = false)
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
    @Column(name = "TYPE10", length = 1000000000, nullable = true, unique = false)
    public String getType10() {
        return type10;
    }

    /**
     * TYPE10を設定します。
     *
     * @param type10 TYPE10
     */
    public void setType10(String type10) {
        this.type10 = type10;
    }
    /**
     * TYPE11を返します。
     *
     * @return TYPE11
     */
    @Column(name = "TYPE11", precision = 24, nullable = true, unique = false)
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
    @Column(name = "TYPE12", precision = 16, nullable = true, unique = false)
    public Short getType12() {
        return type12;
    }

    /**
     * TYPE12を設定します。
     *
     * @param type12 TYPE12
     */
    public void setType12(Short type12) {
        this.type12 = type12;
    }
    /**
     * TYPE13を返します。
     *
     * @return TYPE13
     */
    @Column(name = "TYPE13", nullable = true, unique = false)
    public Time getType13() {
        return type13;
    }

    /**
     * TYPE13を設定します。
     *
     * @param type13 TYPE13
     */
    public void setType13(Time type13) {
        this.type13 = type13;
    }
    /**
     * TYPE14を返します。
     *
     * @return TYPE14
     */
    @Column(name = "TYPE14", nullable = true, unique = false)
    public Timestamp getType14() {
        return type14;
    }

    /**
     * TYPE14を設定します。
     *
     * @param type14 TYPE14
     */
    public void setType14(Timestamp type14) {
        this.type14 = type14;
    }
    /**
     * TYPE15を返します。
     *
     * @return TYPE15
     */
    @Column(name = "TYPE15", length = 1000000000, nullable = true, unique = false)
    public String getType15() {
        return type15;
    }

    /**
     * TYPE15を設定します。
     *
     * @param type15 TYPE15
     */
    public void setType15(String type15) {
        this.type15 = type15;
    }
    /**
     * TYPE16を返します。
     *
     * @return TYPE16
     */
    @Column(name = "TYPE16", length = 1000000000, nullable = true, unique = false)
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
