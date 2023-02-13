package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * MstOrgエンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "MST_ORG")
public class MstOrg implements Serializable {

    private static final long serialVersionUID = 1L;

    /** orgIdプロパティ */
    private Short orgId;

    /** startDateプロパティ */
    private Date startDate;

    /** endDateプロパティ */
    private Date endDate;

    /** delFlgプロパティ */
    private boolean delFlg;

    /** mstOrgName関連プロパティ */
    private MstOrgName mstOrgName;
    /**
     * orgIdを返します。
     *
     * @return orgId
     */
    @Id
    @Column(name = "ORG_ID", precision = 3, nullable = false, unique = true, insertable = false, updatable = false)
    public Short getOrgId() {
        return orgId;
    }

    /**
     * orgIdを設定します。
     *
     * @param orgId
     */
    public void setOrgId(Short orgId) {
        this.orgId = orgId;
    }
    /**
     * startDateを返します。
     *
     * @return startDate
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false, unique = false)
    public Date getStartDate() {
        return startDate;
    }

    /**
     * startDateを設定します。
     *
     * @param startDate
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    /**
     * endDateを返します。
     *
     * @return endDate
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false, unique = false)
    public Date getEndDate() {
        return endDate;
    }

    /**
     * endDateを設定します。
     *
     * @param endDate
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    /**
     * delFlgを返します。
     *
     * @return delFlg
     */
    @Column(name = "DEL_FLG", length = 1, nullable = true, unique = false)
    public boolean isDelFlg() {
        return delFlg;
    }

    /**
     * delFlgを設定します。
     *
     * @param delFlg
     */
    public void setDelFlg(boolean delFlg) {
        this.delFlg = delFlg;
    }

    /**
     * mstOrgNameを返します。
     *
     * @return mstOrgName
     */
    @OneToOne
    @JoinColumn(name = "ORG_ID", referencedColumnName = "ORG_ID")
    public MstOrgName getMstOrgName() {
        return mstOrgName;
    }

    /**
     * mstOrgNameを設定します。
     *
     * @param mstOrgName mstOrgName
     */
    public void setMstOrgName(MstOrgName mstOrgName) {
        this.mstOrgName = mstOrgName;
    }
}
