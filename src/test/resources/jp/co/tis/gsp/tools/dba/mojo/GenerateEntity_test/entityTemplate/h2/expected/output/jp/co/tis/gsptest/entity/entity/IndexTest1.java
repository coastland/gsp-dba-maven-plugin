package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * INDEX_TEST1
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "PUBLIC", name = "INDEX_TEST1", uniqueConstraints = { @UniqueConstraint(columnNames = { "SUB_ID_1", "SUB_ID_2" }) })
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
// ENTITY_TEMPLATE_TEST!
    @Id
    @Column(name = "INDEX_TEST1_ID", precision = 64, nullable = false, unique = true)
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
// ENTITY_TEMPLATE_TEST!
    @Column(name = "SUB_ID_1", precision = 64, nullable = false, unique = false)
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
// ENTITY_TEMPLATE_TEST!
    @Column(name = "SUB_ID_2", precision = 64, nullable = false, unique = false)
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
