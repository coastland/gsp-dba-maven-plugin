package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * OrgViewエンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "ORG_VIEW")
public class OrgView implements Serializable {

    private static final long serialVersionUID = 1L;

    /** orgIdプロパティ */
    private Short orgId;

    /** mstOrgName関連プロパティ */
    private MstOrgName mstOrgName;
    /**
     * orgIdを返します。
     *
     * @return orgId
     */
    @Id
    @Column(name = "ORG_ID", precision = 3, nullable = false, unique = false, insertable = false, updatable = false)
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
     * mstOrgNameを返します。
     *
     * @return mstOrgName
     */
    @ManyToOne
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
