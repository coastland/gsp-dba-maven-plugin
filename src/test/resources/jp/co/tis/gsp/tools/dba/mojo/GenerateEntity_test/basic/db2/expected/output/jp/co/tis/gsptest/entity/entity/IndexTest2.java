package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * INDEX_TEST2
 *
 */
@Generated("GSP")
@Entity
@Table(name = "INDEX_TEST2", uniqueConstraints = { @UniqueConstraint(columnNames = { "SUB_ID_1", "SUB_ID_2" }) })
public class IndexTest2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** INDEX_TEST2_ID */
    private Long indexTest2Id;

    /** SUB_ID_1 */
    private Long subId1;

    /** SUB_ID_2 */
    private Long subId2;
    /**
     * INDEX_TEST2_IDを返します。
     *
     * @return INDEX_TEST2_ID
     */
    @Id
    @Column(name = "INDEX_TEST2_ID", precision = 19, nullable = false, unique = true)
    public Long getIndexTest2Id() {
        return indexTest2Id;
    }

    /**
     * INDEX_TEST2_IDを設定します。
     *
     * @param indexTest2Id INDEX_TEST2_ID
     */
    public void setIndexTest2Id(Long indexTest2Id) {
        this.indexTest2Id = indexTest2Id;
    }
    /**
     * SUB_ID_1を返します。
     *
     * @return SUB_ID_1
     */
    @Column(name = "SUB_ID_1", precision = 19, nullable = false, unique = false)
    public Long getSubId1() {
        return subId1;
    }

    /**
     * SUB_ID_1を設定します。
     *
     * @param subId1 SUB_ID_1
     */
    public void setSubId1(Long subId1) {
        this.subId1 = subId1;
    }
    /**
     * SUB_ID_2を返します。
     *
     * @return SUB_ID_2
     */
    @Column(name = "SUB_ID_2", precision = 19, nullable = false, unique = false)
    public Long getSubId2() {
        return subId2;
    }

    /**
     * SUB_ID_2を設定します。
     *
     * @param subId2 SUB_ID_2
     */
    public void setSubId2(Long subId2) {
        this.subId2 = subId2;
    }
}
