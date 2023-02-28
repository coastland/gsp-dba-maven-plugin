package jp.co.tis.gsptest.entity.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * INDEX_TEST1
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "gspanother", name = "index_test1", uniqueConstraints = { @UniqueConstraint(columnNames = { "sub_id_1", "sub_id_2" }) })
public class IndexTest1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** INDEX_TEST1_ID */
    private Long indexTest1Id;

    /** SUB_ID_1 */
    private Long subId1;

    /** SUB_ID_2 */
    private Long subId2;
    /**
     * INDEX_TEST1_IDを返します。
     *
     * @return INDEX_TEST1_ID
     */
    @Id
    @Column(name = "index_test1_id", precision = 19, nullable = false, unique = true)
    public Long getIndexTest1Id() {
        return indexTest1Id;
    }

    /**
     * INDEX_TEST1_IDを設定します。
     *
     * @param indexTest1Id INDEX_TEST1_ID
     */
    public void setIndexTest1Id(Long indexTest1Id) {
        this.indexTest1Id = indexTest1Id;
    }
    /**
     * SUB_ID_1を返します。
     *
     * @return SUB_ID_1
     */
    @Column(name = "sub_id_1", precision = 19, nullable = false, unique = false)
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
    @Column(name = "sub_id_2", precision = 19, nullable = false, unique = false)
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
