package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Table;

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
    @Column(name = "TYPE1")
    private Long type1;

    /** TYPE2 */
    @Column(name = "TYPE2")
    private byte[] type2;

    /** TYPE3 */
    @Column(name = "TYPE3")
    private String type3;

    /** TYPE4 */
    @Column(name = "TYPE4")
    private String type4;

    /** TYPE5 */
    @Column(name = "TYPE5")
    private LocalDate type5;

    /** TYPE6 */
    @Column(name = "TYPE6")
    private String type6;

    /** TYPE7 */
    @Column(name = "TYPE7")
    private BigDecimal type7;

    /** TYPE8 */
    @Column(name = "TYPE8")
    private Double type8;

    /** TYPE9 */
    @Column(name = "TYPE9")
    private Double type9;

    /** TYPE10 */
    @Column(name = "TYPE10")
    private String type10;

    /** TYPE11 */
    @Column(name = "TYPE11")
    private Integer type11;

    /** TYPE12 */
    @Column(name = "TYPE12")
    private String type12;

    /** TYPE13 */
    @Column(name = "TYPE13")
    private String type13;

    /** TYPE14 */
    @Column(name = "TYPE14")
    private BigDecimal type14;

    /** TYPE15 */
    @Column(name = "TYPE15")
    private Float type15;

    /** TYPE16 */
    @Column(name = "TYPE16")
    private Short type16;

    /** TYPE17 */
    @Column(name = "TYPE17")
    private LocalTime type17;

    /** TYPE18 */
    @Column(name = "TYPE18")
    private LocalDateTime type18;

    /** TYPE19 */
    @Column(name = "TYPE19")
    private String type19;

    /** TYPE20 */
    @Column(name = "TYPE20")
    private String type20;

    /** TYPE21 */
    @Column(name = "TYPE21")
    private boolean type21;
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
    public LocalDate getType5() {
        return type5;
    }

    /**
     * TYPE5を設定します。
     *
     * @param type5 TYPE5
     */
    public void setType5(LocalDate type5) {
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
    public Double getType9() {
        return type9;
    }

    /**
     * TYPE9を設定します。
     *
     * @param type9 TYPE9
     */
    public void setType9(Double type9) {
        this.type9 = type9;
    }
    /**
     * TYPE10を返します。
     *
     * @return TYPE10
     */
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
    public Integer getType11() {
        return type11;
    }

    /**
     * TYPE11を設定します。
     *
     * @param type11 TYPE11
     */
    public void setType11(Integer type11) {
        this.type11 = type11;
    }
    /**
     * TYPE12を返します。
     *
     * @return TYPE12
     */
    public String getType12() {
        return type12;
    }

    /**
     * TYPE12を設定します。
     *
     * @param type12 TYPE12
     */
    public void setType12(String type12) {
        this.type12 = type12;
    }
    /**
     * TYPE13を返します。
     *
     * @return TYPE13
     */
    public String getType13() {
        return type13;
    }

    /**
     * TYPE13を設定します。
     *
     * @param type13 TYPE13
     */
    public void setType13(String type13) {
        this.type13 = type13;
    }
    /**
     * TYPE14を返します。
     *
     * @return TYPE14
     */
    public BigDecimal getType14() {
        return type14;
    }

    /**
     * TYPE14を設定します。
     *
     * @param type14 TYPE14
     */
    public void setType14(BigDecimal type14) {
        this.type14 = type14;
    }
    /**
     * TYPE15を返します。
     *
     * @return TYPE15
     */
    public Float getType15() {
        return type15;
    }

    /**
     * TYPE15を設定します。
     *
     * @param type15 TYPE15
     */
    public void setType15(Float type15) {
        this.type15 = type15;
    }
    /**
     * TYPE16を返します。
     *
     * @return TYPE16
     */
    public Short getType16() {
        return type16;
    }

    /**
     * TYPE16を設定します。
     *
     * @param type16 TYPE16
     */
    public void setType16(Short type16) {
        this.type16 = type16;
    }
    /**
     * TYPE17を返します。
     *
     * @return TYPE17
     */
    public LocalTime getType17() {
        return type17;
    }

    /**
     * TYPE17を設定します。
     *
     * @param type17 TYPE17
     */
    public void setType17(LocalTime type17) {
        this.type17 = type17;
    }
    /**
     * TYPE18を返します。
     *
     * @return TYPE18
     */
    public LocalDateTime getType18() {
        return type18;
    }

    /**
     * TYPE18を設定します。
     *
     * @param type18 TYPE18
     */
    public void setType18(LocalDateTime type18) {
        this.type18 = type18;
    }
    /**
     * TYPE19を返します。
     *
     * @return TYPE19
     */
    public String getType19() {
        return type19;
    }

    /**
     * TYPE19を設定します。
     *
     * @param type19 TYPE19
     */
    public void setType19(String type19) {
        this.type19 = type19;
    }
    /**
     * TYPE20を返します。
     *
     * @return TYPE20
     */
    public String getType20() {
        return type20;
    }

    /**
     * TYPE20を設定します。
     *
     * @param type20 TYPE20
     */
    public void setType20(String type20) {
        this.type20 = type20;
    }
    /**
     * TYPE21を返します。
     *
     * @return TYPE21
     */
    public boolean isType21() {
        return type21;
    }

    /**
     * TYPE21を設定します。
     *
     * @param type21 TYPE21
     */
    public void setType21(boolean type21) {
        this.type21 = type21;
    }
}
