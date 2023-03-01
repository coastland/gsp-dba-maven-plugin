package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * MstOrgNameエンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "MST_ORG_NAME")
public class MstOrgName implements Serializable {

    private static final long serialVersionUID = 1L;

    /** orgIdプロパティ */
    private Short orgId;

    /** orgNameプロパティ */
    private Date orgName;

    /** mstOrg関連プロパティ */
    private MstOrg mstOrg;

    /** orgViewList関連プロパティ */
    private List<OrgView> orgViewList;
    /**
     * orgIdを返します。
     *
     * @return orgId
     */
    @Id
    @GeneratedValue(generator = "ORG_ID_SEQ", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "ORG_ID_SEQ", sequenceName = "ORG_ID_SEQ", initialValue = 1, allocationSize = 1)
    @Column(name = "ORG_ID", precision = 3, nullable = false, unique = true)
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
     * orgNameを返します。
     *
     * @return orgName
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "ORG_NAME", nullable = false, unique = false)
    public Date getOrgName() {
        return orgName;
    }

    /**
     * orgNameを設定します。
     *
     * @param orgName
     */
    public void setOrgName(Date orgName) {
        this.orgName = orgName;
    }

    /**
     * mstOrgを返します。
     *
     * @return mstOrg
     */
    @OneToOne(mappedBy = "mstOrgName")
    public MstOrg getMstOrg() {
        return mstOrg;
    }

    /**
     * mstOrgを設定します。
     *
     * @param mstOrg mstOrg
     */
    public void setMstOrg(MstOrg mstOrg) {
        this.mstOrg = mstOrg;
    }

    /**
     * orgViewListを返します。
     *
     * @return orgViewList
     */
    @OneToMany(mappedBy = "mstOrgName")
    public List<OrgView> getOrgViewList() {
        return orgViewList;
    }

    /**
     * orgViewListを設定します。
     *
     * @param orgViewList orgViewList
     */
    public void setOrgViewList(List<OrgView> orgViewList) {
        this.orgViewList = orgViewList;
    }
}
