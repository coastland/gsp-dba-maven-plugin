package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Typetestエンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "TYPETEST")
public class Typetest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** type1プロパティ */
    private Long type1;

    /** type2プロパティ */
    private byte[] type2;

    /** type3プロパティ */
    private boolean type3;

    /** type4プロパティ */
    private String type4;

    /** type5プロパティ */
    private Date type5;

    /** type6プロパティ */
    private Date type6;

    /** type7プロパティ */
    private Timestamp type7;

    /** type8プロパティ */
    private String type8;

    /** type9プロパティ */
    private BigDecimal type9;

    /** type10プロパティ */
    private Float type10;

    /** type11プロパティ */
    private byte[] type11;

    /** type12プロパティ */
    private byte[] type12;

    /** type13プロパティ */
    private Integer type13;

    /** type14プロパティ */
    private BigDecimal type14;

    /** type15プロパティ */
    private String type15;

    /** type16プロパティ */
    private String type16;

    /** type17プロパティ */
    private BigDecimal type17;

    /** type18プロパティ */
    private String type18;

    /** type19プロパティ */
    private String type19;

    /** type20プロパティ */
    private Float type20;

    /** type21プロパティ */
    private Date type21;

    /** type22プロパティ */
    private Short type22;

    /** type23プロパティ */
    private BigDecimal type23;

    /** type24プロパティ */
    private String type24;

    /** type25プロパティ */
    private Time type25;

    /** type26プロパティ */
    private Short type26;

    /** type27プロパティ */
    private String type27;

    /** type28プロパティ */
    private byte[] type28;

    /** type29プロパティ */
    private byte[] type29;

    /** type30プロパティ */
    private String type30;

    /** type31プロパティ */
    private String type31;
    /**
     * type1を返します。
     *
     * @return type1
     */
    @Column(name = "TYPE1", precision = 19, nullable = true, unique = false)
    public Long getType1() {
        return type1;
    }

    /**
     * type1を設定します。
     *
     * @param type1
     */
    public void setType1(Long type1) {
        this.type1 = type1;
    }
    /**
     * type2を返します。
     *
     * @return type2
     */
    @Column(name = "TYPE2", length = 1000, nullable = true, unique = false)
    public byte[] getType2() {
        return type2;
    }

    /**
     * type2を設定します。
     *
     * @param type2
     */
    public void setType2(byte[] type2) {
        this.type2 = type2;
    }
    /**
     * type3を返します。
     *
     * @return type3
     */
    @Column(name = "TYPE3", length = 1, nullable = true, unique = false)
    public boolean isType3() {
        return type3;
    }

    /**
     * type3を設定します。
     *
     * @param type3
     */
    public void setType3(boolean type3) {
        this.type3 = type3;
    }
    /**
     * type4を返します。
     *
     * @return type4
     */
    @Column(name = "TYPE4", length = 10, nullable = true, unique = false)
    public String getType4() {
        return type4;
    }

    /**
     * type4を設定します。
     *
     * @param type4
     */
    public void setType4(String type4) {
        this.type4 = type4;
    }
    /**
     * type5を返します。
     *
     * @return type5
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "TYPE5", nullable = true, unique = false)
    public Date getType5() {
        return type5;
    }

    /**
     * type5を設定します。
     *
     * @param type5
     */
    public void setType5(Date type5) {
        this.type5 = type5;
    }
    /**
     * type6を返します。
     *
     * @return type6
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TYPE6", nullable = true, unique = false)
    public Date getType6() {
        return type6;
    }

    /**
     * type6を設定します。
     *
     * @param type6
     */
    public void setType6(Date type6) {
        this.type6 = type6;
    }
    /**
     * type7を返します。
     *
     * @return type7
     */
    @Column(name = "TYPE7", nullable = true, unique = false)
    public Timestamp getType7() {
        return type7;
    }

    /**
     * type7を設定します。
     *
     * @param type7
     */
    public void setType7(Timestamp type7) {
        this.type7 = type7;
    }
    /**
     * type8を返します。
     *
     * @return type8
     */
    @Column(name = "TYPE8", length = 34, nullable = true, unique = false)
    public String getType8() {
        return type8;
    }

    /**
     * type8を設定します。
     *
     * @param type8
     */
    public void setType8(String type8) {
        this.type8 = type8;
    }
    /**
     * type9を返します。
     *
     * @return type9
     */
    @Column(name = "TYPE9", precision = 10, scale = 2, nullable = true, unique = false)
    public BigDecimal getType9() {
        return type9;
    }

    /**
     * type9を設定します。
     *
     * @param type9
     */
    public void setType9(BigDecimal type9) {
        this.type9 = type9;
    }
    /**
     * type10を返します。
     *
     * @return type10
     */
    @Column(name = "TYPE10", precision = 24, nullable = true, unique = false)
    public Float getType10() {
        return type10;
    }

    /**
     * type10を設定します。
     *
     * @param type10
     */
    public void setType10(Float type10) {
        this.type10 = type10;
    }
    /**
     * type11を返します。
     *
     * @return type11
     */
    @Column(name = "TYPE11", length = 892, nullable = true, unique = false)
    public byte[] getType11() {
        return type11;
    }

    /**
     * type11を設定します。
     *
     * @param type11
     */
    public void setType11(byte[] type11) {
        this.type11 = type11;
    }
    /**
     * type12を返します。
     *
     * @return type12
     */
    @Lob
    @Column(name = "TYPE12", length = 2147483647, nullable = true, unique = false)
    public byte[] getType12() {
        return type12;
    }

    /**
     * type12を設定します。
     *
     * @param type12
     */
    public void setType12(byte[] type12) {
        this.type12 = type12;
    }
    /**
     * type13を返します。
     *
     * @return type13
     */
    @Column(name = "TYPE13", precision = 10, nullable = true, unique = false)
    public Integer getType13() {
        return type13;
    }

    /**
     * type13を設定します。
     *
     * @param type13
     */
    public void setType13(Integer type13) {
        this.type13 = type13;
    }
    /**
     * type14を返します。
     *
     * @return type14
     */
    @Column(name = "TYPE14", precision = 19, scale = 4, nullable = true, unique = false)
    public BigDecimal getType14() {
        return type14;
    }

    /**
     * type14を設定します。
     *
     * @param type14
     */
    public void setType14(BigDecimal type14) {
        this.type14 = type14;
    }
    /**
     * type15を返します。
     *
     * @return type15
     */
    @Column(name = "TYPE15", length = 10, nullable = true, unique = false)
    public String getType15() {
        return type15;
    }

    /**
     * type15を設定します。
     *
     * @param type15
     */
    public void setType15(String type15) {
        this.type15 = type15;
    }
    /**
     * type16を返します。
     *
     * @return type16
     */
    @Column(name = "TYPE16", length = 1073741823, nullable = true, unique = false)
    public String getType16() {
        return type16;
    }

    /**
     * type16を設定します。
     *
     * @param type16
     */
    public void setType16(String type16) {
        this.type16 = type16;
    }
    /**
     * type17を返します。
     *
     * @return type17
     */
    @Column(name = "TYPE17", precision = 20, scale = 2, nullable = true, unique = false)
    public BigDecimal getType17() {
        return type17;
    }

    /**
     * type17を設定します。
     *
     * @param type17
     */
    public void setType17(BigDecimal type17) {
        this.type17 = type17;
    }
    /**
     * type18を返します。
     *
     * @return type18
     */
    @Column(name = "TYPE18", length = 20, nullable = true, unique = false)
    public String getType18() {
        return type18;
    }

    /**
     * type18を設定します。
     *
     * @param type18
     */
    public void setType18(String type18) {
        this.type18 = type18;
    }
    /**
     * type19を返します。
     *
     * @return type19
     */
    @Column(name = "TYPE19", length = 2147483647, nullable = true, unique = false)
    public String getType19() {
        return type19;
    }

    /**
     * type19を設定します。
     *
     * @param type19
     */
    public void setType19(String type19) {
        this.type19 = type19;
    }
    /**
     * type20を返します。
     *
     * @return type20
     */
    @Column(name = "TYPE20", precision = 24, nullable = true, unique = false)
    public Float getType20() {
        return type20;
    }

    /**
     * type20を設定します。
     *
     * @param type20
     */
    public void setType20(Float type20) {
        this.type20 = type20;
    }
    /**
     * type21を返します。
     *
     * @return type21
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TYPE21", nullable = true, unique = false)
    public Date getType21() {
        return type21;
    }

    /**
     * type21を設定します。
     *
     * @param type21
     */
    public void setType21(Date type21) {
        this.type21 = type21;
    }
    /**
     * type22を返します。
     *
     * @return type22
     */
    @Column(name = "TYPE22", precision = 5, nullable = true, unique = false)
    public Short getType22() {
        return type22;
    }

    /**
     * type22を設定します。
     *
     * @param type22
     */
    public void setType22(Short type22) {
        this.type22 = type22;
    }
    /**
     * type23を返します。
     *
     * @return type23
     */
    @Column(name = "TYPE23", precision = 10, scale = 4, nullable = true, unique = false)
    public BigDecimal getType23() {
        return type23;
    }

    /**
     * type23を設定します。
     *
     * @param type23
     */
    public void setType23(BigDecimal type23) {
        this.type23 = type23;
    }
    /**
     * type24を返します。
     *
     * @return type24
     */
    @Column(name = "TYPE24", length = 2147483647, nullable = true, unique = false)
    public String getType24() {
        return type24;
    }

    /**
     * type24を設定します。
     *
     * @param type24
     */
    public void setType24(String type24) {
        this.type24 = type24;
    }
    /**
     * type25を返します。
     *
     * @return type25
     */
    @Column(name = "TYPE25", nullable = true, unique = false)
    public Time getType25() {
        return type25;
    }

    /**
     * type25を設定します。
     *
     * @param type25
     */
    public void setType25(Time type25) {
        this.type25 = type25;
    }
    /**
     * type26を返します。
     *
     * @return type26
     */
    @Column(name = "TYPE26", precision = 3, nullable = true, unique = false)
    public Short getType26() {
        return type26;
    }

    /**
     * type26を設定します。
     *
     * @param type26
     */
    public void setType26(Short type26) {
        this.type26 = type26;
    }
    /**
     * type27を返します。
     *
     * @return type27
     */
    @Column(name = "TYPE27", length = 36, nullable = true, unique = false)
    public String getType27() {
        return type27;
    }

    /**
     * type27を設定します。
     *
     * @param type27
     */
    public void setType27(String type27) {
        this.type27 = type27;
    }
    /**
     * type28を返します。
     *
     * @return type28
     */
    @Column(name = "TYPE28", length = 1000, nullable = true, unique = false)
    public byte[] getType28() {
        return type28;
    }

    /**
     * type28を設定します。
     *
     * @param type28
     */
    public void setType28(byte[] type28) {
        this.type28 = type28;
    }
    /**
     * type29を返します。
     *
     * @return type29
     */
    @Column(name = "TYPE29", length = 2147483647, nullable = true, unique = false)
    public byte[] getType29() {
        return type29;
    }

    /**
     * type29を設定します。
     *
     * @param type29
     */
    public void setType29(byte[] type29) {
        this.type29 = type29;
    }
    /**
     * type30を返します。
     *
     * @return type30
     */
    @Column(name = "TYPE30", length = 100, nullable = true, unique = false)
    public String getType30() {
        return type30;
    }

    /**
     * type30を設定します。
     *
     * @param type30
     */
    public void setType30(String type30) {
        this.type30 = type30;
    }
    /**
     * type31を返します。
     *
     * @return type31
     */
    @Column(name = "TYPE31", length = 2147483647, nullable = true, unique = false)
    public String getType31() {
        return type31;
    }

    /**
     * type31を設定します。
     *
     * @param type31
     */
    public void setType31(String type31) {
        this.type31 = type31;
    }
}
