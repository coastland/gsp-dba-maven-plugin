package jp.co.tis.gsptest.entity.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * INDEX_TEST3
 *
 */
@Generated("GSP")
@Entity
@Table(name = "INDEX_TEST3")
public class IndexTest3 implements Serializable {

    private static final long serialVersionUID = 1L;

    /** INDEX_TEST3_ID */
    private Long indexTest3Id;

    /** SUB_ID_1 */
    private Long subId1;

    /** SUB_ID_2 */
    private Long subId2;
    /**
     * INDEX_TEST3_IDを返します。
     *
     * @return INDEX_TEST3_ID
     */
    @Id
    @GeneratedValue(generator = "INDEX_TEST3_ID_SEQ", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "INDEX_TEST3_ID_SEQ", sequenceName = "INDEX_TEST3_ID_SEQ", initialValue = 1, allocationSize = 1)
    @Column(name = "INDEX_TEST3_ID", precision = 18, nullable = false, unique = true)
    public Long getIndexTest3Id() {
        return indexTest3Id;
    }

    /**
     * INDEX_TEST3_IDを設定します。
     *
     * @param indexTest3Id INDEX_TEST3_ID
     */
    public void setIndexTest3Id(Long indexTest3Id) {
        this.indexTest3Id = indexTest3Id;
    }
    /**
     * SUB_ID_1を返します。
     *
     * @return SUB_ID_1
     */
    @Column(name = "SUB_ID_1", precision = 18, nullable = false, unique = false)
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
    @Column(name = "SUB_ID_2", precision = 18, nullable = false, unique = false)
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
