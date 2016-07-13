package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * OrgStartDateViewエンティティクラス
 *
 */
@Generated("GSP")
@Entity
@Table(name = "ORG_START_DATE_VIEW")
public class OrgStartDateView implements Serializable {

    private static final long serialVersionUID = 1L;

    /** startDateプロパティ */
    private Date startDate;
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
}
