package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * INDEX_TEST2
 *
 */
@Generated("GSP")
@Entity
@Table(schema = "PUBLIC", name = "INDEX_TEST2", uniqueConstraints = { @UniqueConstraint(columnNames = { "SUB_ID_1", "SUB_ID_2" }) })
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
// ENTITY_TEMPLATE_TEST!
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.AUTO)
    @Column(name = "INDEX_TEST2_ID", precision = 64, nullable = false, unique = true)
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
