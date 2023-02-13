package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 *
 */
@Generated("GSP")
@Entity
@Table(name = "typetest")
public class Typetest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** TYPE1 */
    private Long type1;

    /** TYPE2 */
    private byte[] type2;

    /** TYPE3 */
    private String type3;

    /** TYPE4 */
    private LocalDate type4;

    /** TYPE5 */
    private LocalDateTime type5;

    /** TYPE6 */
    private BigDecimal type6;

    /** TYPE7 */
    private Double type7;

    /** TYPE8 */
    private Float type8;

    /** TYPE9 */
    private Integer type9;

    /** TYPE10 */
    private byte[] type10;

    /** TYPE11 */
    private String type11;

    /** TYPE12 */
    private byte[] type12;

    /** TYPE13 */
    private Integer type13;

    /** TYPE14 */
    private String type14;

    /** TYPE15 */
    private Short type15;

    /** TYPE16 */
    private String type16;

    /** TYPE17 */
    private LocalTime type17;

    /** TYPE18 */
    private LocalDateTime type18;

    /** TYPE19 */
    private byte[] type19;

    /** TYPE20 */
    private Byte type20;

    /** TYPE21 */
    private String type21;

    /** TYPE22 */
    private String type22;

    /** TYPE23 */
    private LocalDate type23;
    /**
     * TYPE1を返します。
     *
     * @return TYPE1
     */
    @Column(name = "TYPE1", precision = 19, nullable = true, unique = false)
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
    @Lob
    @Column(name = "TYPE2", length = 65535, nullable = true, unique = false)
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
    @Column(name = "TYPE3", length = 1, nullable = true, unique = false)
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
    public LocalDate getType4() {
        return type4;
    }

    /**
     * TYPE4を設定します。
     *
     * @param type4 TYPE4
     */
    public void setType4(LocalDate type4) {
        this.type4 = type4;
    }
    /**
     * TYPE5を返します。
     *
     * @return TYPE5
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TYPE5", nullable = true, unique = false)
    public LocalDateTime getType5() {
        return type5;
    }

    /**
     * TYPE5を設定します。
     *
     * @param type5 TYPE5
     */
    public void setType5(LocalDateTime type5) {
        this.type5 = type5;
    }
    /**
     * TYPE6を返します。
     *
     * @return TYPE6
     */
    @Column(name = "TYPE6", precision = 10, scale = 2, nullable = true, unique = false)
    public BigDecimal getType6() {
        return type6;
    }

    /**
     * TYPE6を設定します。
     *
     * @param type6 TYPE6
     */
    public void setType6(BigDecimal type6) {
        this.type6 = type6;
    }
    /**
     * TYPE7を返します。
     *
     * @return TYPE7
     */
    @Column(name = "TYPE7", precision = 10, scale = 3, nullable = true, unique = false)
    public Double getType7() {
        return type7;
    }

    /**
     * TYPE7を設定します。
     *
     * @param type7 TYPE7
     */
    public void setType7(Double type7) {
        this.type7 = type7;
    }
    /**
     * TYPE8を返します。
     *
     * @return TYPE8
     */
    @Column(name = "TYPE8", precision = 9, scale = 3, nullable = true, unique = false)
    public Float getType8() {
        return type8;
    }

    /**
     * TYPE8を設定します。
     *
     * @param type8 TYPE8
     */
    public void setType8(Float type8) {
        this.type8 = type8;
    }
    /**
     * TYPE9を返します。
     *
     * @return TYPE9
     */
    @Column(name = "TYPE9", precision = 10, nullable = true, unique = false)
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
    @Lob
    @Column(name = "TYPE10", length = 2147483647, nullable = true, unique = false)
    public byte[] getType10() {
        return type10;
    }

    /**
     * TYPE10を設定します。
     *
     * @param type10 TYPE10
     */
    public void setType10(byte[] type10) {
        this.type10 = type10;
    }
    /**
     * TYPE11を返します。
     *
     * @return TYPE11
     */
    @Lob
    @Column(name = "TYPE11", length = 2147483647, nullable = true, unique = false)
    public String getType11() {
        return type11;
    }

    /**
     * TYPE11を設定します。
     *
     * @param type11 TYPE11
     */
    public void setType11(String type11) {
        this.type11 = type11;
    }
    /**
     * TYPE12を返します。
     *
     * @return TYPE12
     */
    @Lob
    @Column(name = "TYPE12", length = 16777215, nullable = true, unique = false)
    public byte[] getType12() {
        return type12;
    }

    /**
     * TYPE12を設定します。
     *
     * @param type12 TYPE12
     */
    public void setType12(byte[] type12) {
        this.type12 = type12;
    }
    /**
     * TYPE13を返します。
     *
     * @return TYPE13
     */
    @Column(name = "TYPE13", precision = 7, nullable = true, unique = false)
    public Integer getType13() {
        return type13;
    }

    /**
     * TYPE13を設定します。
     *
     * @param type13 TYPE13
     */
    public void setType13(Integer type13) {
        this.type13 = type13;
    }
    /**
     * TYPE14を返します。
     *
     * @return TYPE14
     */
    @Lob
    @Column(name = "TYPE14", length = 16777215, nullable = true, unique = false)
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
    @Column(name = "TYPE15", precision = 5, nullable = true, unique = false)
    public Short getType15() {
        return type15;
    }

    /**
     * TYPE15を設定します。
     *
     * @param type15 TYPE15
     */
    public void setType15(Short type15) {
        this.type15 = type15;
    }
    /**
     * TYPE16を返します。
     *
     * @return TYPE16
     */
    @Lob
    @Column(name = "TYPE16", length = 65535, nullable = true, unique = false)
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
    /**
     * TYPE17を返します。
     *
     * @return TYPE17
     */
    @Column(name = "TYPE17", nullable = true, unique = false)
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TYPE18", nullable = true, unique = false)
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
    @Lob
    @Column(name = "TYPE19", length = 255, nullable = true, unique = false)
    public byte[] getType19() {
        return type19;
    }

    /**
     * TYPE19を設定します。
     *
     * @param type19 TYPE19
     */
    public void setType19(byte[] type19) {
        this.type19 = type19;
    }
    /**
     * TYPE20を返します。
     *
     * @return TYPE20
     */
    @Column(name = "TYPE20", precision = 3, nullable = true, unique = false)
    public Byte getType20() {
        return type20;
    }

    /**
     * TYPE20を設定します。
     *
     * @param type20 TYPE20
     */
    public void setType20(Byte type20) {
        this.type20 = type20;
    }
    /**
     * TYPE21を返します。
     *
     * @return TYPE21
     */
    @Lob
    @Column(name = "TYPE21", length = 255, nullable = true, unique = false)
    public String getType21() {
        return type21;
    }

    /**
     * TYPE21を設定します。
     *
     * @param type21 TYPE21
     */
    public void setType21(String type21) {
        this.type21 = type21;
    }
    /**
     * TYPE22を返します。
     *
     * @return TYPE22
     */
    @Column(name = "TYPE22", length = 20, nullable = true, unique = false)
    public String getType22() {
        return type22;
    }

    /**
     * TYPE22を設定します。
     *
     * @param type22 TYPE22
     */
    public void setType22(String type22) {
        this.type22 = type22;
    }
    /**
     * TYPE23を返します。
     *
     * @return TYPE23
     */
    @Column(name = "TYPE23", nullable = true, unique = false)
    public LocalDate getType23() {
        return type23;
    }

    /**
     * TYPE23を設定します。
     *
     * @param type23 TYPE23
     */
    public void setType23(LocalDate type23) {
        this.type23 = type23;
    }
}
